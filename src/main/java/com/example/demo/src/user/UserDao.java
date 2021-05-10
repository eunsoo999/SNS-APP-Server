package com.example.demo.src.user;


import com.example.demo.src.follow.model.Follow;
import com.example.demo.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class UserDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetUserRes> getUsers(){
        String getUsersQuery = "select idx as 'userIdx', userId, name, profileUrl, websiteUrl, introduction, " +
                "(select FORMAT(count(*), '#,#') from Post where Post.userIdx = User.idx) AS postCount, " +
                "CASE WHEN (select count(*) from Follow where User.idx = Follow.followingUserIdx) = 0 " +
                "THEN 0 " +
                "WHEN (select count(*) from Follow where User.idx = Follow.followingUserIdx) >= 10000 " +
                "THEN (select CONCAT(TRUNCATE(count(*) / 10000, 0), '만') from Follow where User.idx = Follow.followingUserIdx) " +
                "ELSE (select count(*) from Follow where User.idx = Follow.followingUserIdx) " +
                "END as followerCount, " +
                "(select FORMAT(count(*), '#,#') from Follow where User.idx = Follow.userIdx) AS followingCount " +
                "from User";

        return this.jdbcTemplate.query(getUsersQuery,
                (rs,rowNum) -> new GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("userId"),
                        rs.getString("name"),
                        rs.getString("profileUrl"),
                        rs.getString("websiteUrl"),
                        rs.getString("introduction"),
                        rs.getString("postCount"),
                        rs.getString("followerCount"),
                        rs.getString("followingCount"))
                );
    }

    public GetUserRes getUser(int userIdx){
        String getUserQuery = "select idx as 'userIdx', userId, name, profileUrl, websiteUrl, introduction, " +
                "(select FORMAT(count(*), '#,#') from Post where Post.userIdx = User.idx and Post.status != 'N') AS postCount, " +
                "CASE WHEN (select count(*) from Follow where User.idx = Follow.followingUserIdx) = 0 " +
                "THEN 0 " +
                "WHEN (select count(*) from Follow where User.idx = Follow.followingUserIdx) >= 10000 " +
                "THEN (select CONCAT(TRUNCATE(count(*) / 10000, 0), '만') from Follow where User.idx = Follow.followingUserIdx) " +
                "ELSE (select count(*) from Follow where User.idx = Follow.followingUserIdx) " +
                "END as followerCount, " +
                "(select FORMAT(count(*), '#,#') from Follow where User.idx = Follow.userIdx) AS followingCount " +
                "from User " +
                "where User.idx = ?";

        int getUserParams = userIdx;

        return this.jdbcTemplate.queryForObject(getUserQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("userId"),
                        rs.getString("name"),
                        rs.getString("profileUrl"),
                        rs.getString("websiteUrl"),
                        rs.getString("introduction"),
                        rs.getString("postCount"),
                        rs.getString("followerCount"),
                        rs.getString("followingCount")),
                getUserParams);
    }

    //함께 아는 친구 List
    public List<FollowUser> getCommonFollow(int loginIdx, int userIdx) {
        String getCommonFollowQuery = "select User.idx AS 'userIdx', " +
                "User.profileUrl AS 'userProfileUrl', " +
                "userId, User.name AS 'name', " +
                "CASE WHEN User.idx IN (select followingUserIdx from Follow where userIdx = ? and status = 'ACTIVE') " +
                "THEN '팔로잉' " +
                "WHEN User.idx IN (select followingUserIdx from Follow where userIdx = ? and status = 'WAIT') " +
                "THEN '요청됨' " +
                "ELSE '팔로우' " +
                "END as followCheck " +
                "from Follow inner join User ON Follow.userIdx = User.idx " +
                "where followingUserIdx = ? and Follow.status = 'ACTIVE' and User.status != 'N' and User.idx in (select followingUserIdx " +
                "from Follow " +
                "where userIdx = ? and status = 'ACTIVE')";

        int loginParams = loginIdx;
        int getUserParams = userIdx;

        return this.jdbcTemplate.query(getCommonFollowQuery,
                (rs,rowNum) -> new FollowUser(
                        rs.getInt("userIdx"),
                        rs.getString("userProfileUrl"),
                        rs.getString("userId"),
                        rs.getString("name"),
                        rs.getString("followCheck")), loginParams, loginParams, getUserParams, loginParams);

    }


    //함께 아는 친구 Count
    public String getCommonFollowCount(int loginIdx, int userIdx) {
        String getCommonFollowQuery = "select CASE WHEN count(*) >= 10000 " +
                "THEN CONCAT(TRUNCATE(count(*) / 10000, 0), '만명') " +
                "ELSE CONCAT(FORMAT(count(*), '#,#'), '명') " +
                "END as count " +
                "from Follow " +
                "where followingUserIdx = ? and status = 'ACTIVE' and userIdx in (SELECT followingUserIdx " +
                "from Follow " +
                "where userIdx = ? and status = 'ACTIVE')";

        int loginParams = loginIdx;
        int getUserParams = userIdx;

        return this.jdbcTemplate.queryForObject(getCommonFollowQuery, String.class, getUserParams, loginParams);

    }

    //특정 유저 팔로잉 List
    public List<FollowUser> getFollowing(int loginIdx, int userIdx) {
        String query = "select User.idx as 'userIdx', User.profileUrl as 'userProfileUrl', " +
                "userId, User.name as 'name', " +
                "CASE WHEN User.idx IN (select followingUserIdx from Follow where userIdx = ? and status = 'ACTIVE') " +
                "THEN '팔로잉' " +
                "WHEN User.idx in (select followingUserIdx from Follow where userIdx = ? and status = 'WAIT') " +
                "THEN '요청됨' " +
                "WHEN User.idx = ? " +
                "THEN '-' " +
                "ELSE '팔로우' " +
                "END as followCheck " +
                "from Follow inner join User on Follow.followingUserIdx = User.idx " +
                "where Follow.userIdx = ? and Follow.status = 'ACTIVE' and User.status != 'N' " +
                "order by FIELD(followCheck, '-', '팔로잉', '팔로우')";

        int getUserParams = userIdx;

        return this.jdbcTemplate.query(query,
                (rs,rowNum) -> new FollowUser(
                        rs.getInt("userIdx"),
                        rs.getString("userProfileUrl"),
                        rs.getString("userId"),
                        rs.getString("name"),
                        rs.getString("followCheck")), loginIdx, loginIdx, loginIdx, getUserParams);
    }

    //특정 유저 팔로워 List
    public List<FollowUser> getFollower(int loginIdx, int userIdx) {
        String query = "select User.idx AS 'userIdx', User.profileUrl AS 'userProfileUrl', " +
                "userId, User.name AS 'name', " +
                "CASE WHEN User.idx IN (select followingUserIdx from Follow where userIdx = ? and status = 'ACTIVE') " +
                "THEN '팔로잉' " +
                "WHEN User.idx IN (select followingUserIdx from Follow where userIdx = ? and status = 'WAIT') " +
                "THEN '요청됨' " +
                "WHEN User.idx = ? " +
                "THEN '-' " +
                "ELSE '팔로우' " +
                "END as followCheck " +
                "from Follow inner join User on Follow.userIdx = User.idx " +
                "where followingUserIdx = ? and Follow.status = 'ACTIVE' and User.status != 'N' " +
                "order by FIELD(followCheck, '-', '팔로잉', '팔로우')";

        int getUserParams = userIdx;

        return this.jdbcTemplate.query(query,
                (rs,rowNum) -> new FollowUser(
                        rs.getInt("userIdx"),
                        rs.getString("userProfileUrl"),
                        rs.getString("userId"),
                        rs.getString("name"),
                        rs.getString("followCheck")), loginIdx, loginIdx, loginIdx, getUserParams);
    }

    //특정 유저 팔로잉 숫자
    public String getFollowingCount(int userIdx) {
        String query = "select CASE WHEN count(*) >= 10000 " +
                "THEN CONCAT(TRUNCATE(count(*) / 10000, 0), '만명') " +
                "ELSE CONCAT(FORMAT(count(*), '#,#'), '명') " +
                "END as count " +
                "from Follow INNER JOIN User ON Follow.followingUserIdx = User.idx " +
                "where Follow.userIdx = ? and Follow.status = 'ACTIVE' and User.status != 'N'";

        int getUserParams = userIdx;

        return this.jdbcTemplate.queryForObject(query, String.class, getUserParams);
    }

    //특정 유저 팔로워 숫자
    public String getFollowerCount(int userIdx) {
        String query = "select CASE WHEN count(*) >= 10000 " +
                "THEN CONCAT(TRUNCATE(count(*) / 10000, 0), '만명') " +
                "ELSE CONCAT(FORMAT(count(*), '#,#'), '명') " +
                "END as count " +
                "from Follow INNER JOIN User ON Follow.userIdx = User.idx " +
                "where followingUserIdx = ? and Follow.status = 'ACTIVE' and User.status != 'N'";

        int getUserParams = userIdx;

        return this.jdbcTemplate.queryForObject(query, String.class, getUserParams);
    }

    public List<GetSearchUserRes> getUsersByKeyword(String keyword) {
        String getUsersByKeywordQuery = "select idx as 'userIdx', profileUrl, userId, name " +
                "from User " +
                "where (userId like ? OR name like ?) and status != 'N'";
        String searchParam = keyword + "%";

        return this.jdbcTemplate.query(getUsersByKeywordQuery,
                (rs, rowNum) -> new GetSearchUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("profileUrl"),
                        rs.getString("userId"),
                        rs.getString("name")), searchParam, searchParam);
    }

    public List<GetUserRes> getUsersByEmail(String email){
        String getUsersByEmailQuery = "select * from UserInfo where email =?";
        String getUsersByEmailParams = email;
        return this.jdbcTemplate.query(getUsersByEmailQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("userId"),
                        rs.getString("name"),
                        rs.getString("profileUrl"),
                        rs.getString("websiteUrl"),
                        rs.getString("introduction"),
                        rs.getString("postCount"),
                        rs.getString("followerCount"),
                        rs.getString("followingCount")),
                getUsersByEmailParams);
    }

    public int createUserByEmail(PostUserReq postUserReq){
        String createUserQuery = "INSERT INTO User (userId, password, email) VALUES (?, ?, ?)";
        Object[] createUserParams = new Object[]{postUserReq.getUserId(), postUserReq.getPassword(), postUserReq.getEmail()};
        this.jdbcTemplate.update(createUserQuery, createUserParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class);
    }

    public int createUserByPhone(PostUserReq postUserReq){
        String createUserQuery = "INSERT INTO User (userId, password, phone) VALUES (?, ?, ?)";
        Object[] createUserParams = new Object[]{postUserReq.getUserId(), postUserReq.getPassword(), postUserReq.getPhone()};
        this.jdbcTemplate.update(createUserQuery, createUserParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class);
    }

    public int checkEmail(String email){
        String checkEmailQuery = "select exists(select email from User where email = ? and status != 'N')";
        String checkEmailParams = email;
        return this.jdbcTemplate.queryForObject(checkEmailQuery,
                int.class,
                checkEmailParams);
    }

    public int checkPhone(String phone){
        String checkPhoneQuery = "select exists(select phone from User where phone = ? and status != 'N')";
        String checkPhoneParams = phone;
        return this.jdbcTemplate.queryForObject(checkPhoneQuery,
                int.class,
                checkPhoneParams);
    }

    public int checkUserId(String userId) {
        String checkUserIdQuery = "select exists(select userId from User where userId = ? and status != 'N')";
        String checkUserIdParams = userId;
        return this.jdbcTemplate.queryForObject(checkUserIdQuery, int.class, checkUserIdParams);

    }

    public int checkDeletedUser(String userId) {
        String checkDeletedUserQuery = "SELECT exists(SELECT idx FROM User " +
                "WHERE User.userId = ? AND User.status = 'N')";
        String checkDeletedUserParams = userId;

        return this.jdbcTemplate.queryForObject(checkDeletedUserQuery, int.class, checkDeletedUserParams);
    }


    public int checkUserIdx(int userIdx) {
        String checkUserIdxQuery = "select exists(select idx from User where idx = ? and status != 'N')";
        int checkUserIdxParams = userIdx;
        return this.jdbcTemplate.queryForObject(checkUserIdxQuery, int.class, checkUserIdxParams);
    }

    public int modifyUserName(PatchUserReq patchUserReq){
        String modifyUserNameQuery = "update User set name = ? where idx = ? ";
        Object[] modifyUserNameParams = new Object[]{patchUserReq.getUserName(), patchUserReq.getUserIdx()};

        return this.jdbcTemplate.update(modifyUserNameQuery,modifyUserNameParams);
    }

    public UserInfo getPwd(PostLoginReq postLoginReq){
        String getPwdQuery = "select idx AS 'userIdx', userId, password, email, phone from User where userId = ? and status != 'N'";
        String getPwdParams = postLoginReq.getUserId();

        return this.jdbcTemplate.queryForObject(getPwdQuery,
                (rs,rowNum)-> new UserInfo(
                        rs.getInt("userIdx"),
                        rs.getString("userId"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("phone")
                ), getPwdParams);
    }

    //나의 유저 팔로잉 List
    public List<FollowUser> getMyFollowing(int loginIdx, String sort) {
        String query = "select User.idx as 'userIdx', User.profileUrl as 'userProfileUrl', " +
                "userId, User.name as 'name', '팔로잉' as 'followCheck' " +
                "from Follow inner join User on Follow.followingUserIdx = User.idx " +
                "where Follow.userIdx = ? and Follow.status = 'ACTIVE'";

        if (sort != null) {
            if (sort.equalsIgnoreCase("followingdate_asc")) {
                query += "ASC";
            } else if (sort.equalsIgnoreCase("followingdate_desc")) {
                query += "DESC";
            }
        }

        int getUserParams = loginIdx;

        return this.jdbcTemplate.query(query,
                (rs,rowNum) -> new FollowUser(
                        rs.getInt("userIdx"),
                        rs.getString("userProfileUrl"),
                        rs.getString("userId"),
                        rs.getString("name"),
                        rs.getString("followCheck")), getUserParams);
    }

    public List<GetRecommendUsersRes> getRecommendUsers(int loginIdx) {
        String getRecommendUsers = "select DISTINCT User.idx as 'userIdx', User.profileUrl as 'userProfileUrl', " +
                "userId, User.name as 'name', " +
                "CASE WHEN (select count(*) from Follow where Follow.useridx IN (select Follow.followingUserIdx from Follow " +
                "where Follow.userIdx = ? and Follow.status = 'ACTIVE') AND Follow.followingUserIdx = User.idx) = 1 " +
                "THEN concat((select subUser.name from Follow inner join User subUser on Follow.userIdx = subUser.idx " +
                "where Follow.followingUserIdx = User.idx and subUser.idx != ? ORDER BY RAND() limit 1), '님이 팔로우합니다') " +
                "WHEN (select count(*) from Follow where Follow.useridx IN (select Follow.followingUserIdx from Follow " +
                "where Follow.userIdx = ? and Follow.status = 'ACTIVE') AND Follow.followingUserIdx = User.idx) > 1 " +
                "THEN concat((select subUser.name from Follow inner join User subUser on Follow.userIdx = subUser.idx " +
                "where Follow.followingUserIdx = User.idx and subUser.idx != ? ORDER BY RAND() limit 1), '님 외 ' , (select count(*) - 1 from Follow where Follow.useridx IN (select Follow.followingUserIdx from Follow " +
                "where Follow.userIdx = ? and Follow.status = 'ACTIVE') AND Follow.followingUserIdx = User.idx), '명이 팔로우합니다') " +
                "END AS 'recommendFollowMessage', '팔로우' AS 'followCheck' from Follow inner join User on Follow.followingUserIdx = User.idx " +
                "where Follow.userIdx IN " +
                "(select Follow.followingUserIdx from Follow where Follow.userIdx = ? and Follow.status = 'ACTIVE') " +
                "and User.idx NOT IN (select Follow.followingUserIdx from Follow where Follow.userIdx = ? and Follow.status = 'ACTIVE') " +
                "and User.idx != ? GROUP BY User.idx";

        Object[] recommendUsersParams = new Object[]{loginIdx, loginIdx, loginIdx, loginIdx, loginIdx, loginIdx, loginIdx, loginIdx};

        return this.jdbcTemplate.query(getRecommendUsers,
                (rs,rowNum) -> new GetRecommendUsersRes(
                        rs.getInt("userIdx"),
                        rs.getString("userProfileUrl"),
                        rs.getString("userId"),
                        rs.getString("name"),
                        rs.getString("recommendFollowMessage"),
                        rs.getString("followCheck")), recommendUsersParams);
    }

}
