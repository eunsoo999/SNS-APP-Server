package com.example.demo.src.post;

import com.example.demo.src.PostLike.model.GetPostLikes;
import com.example.demo.src.PostLike.model.PostLike;
import com.example.demo.src.follow.model.Follow;
import com.example.demo.src.post.model.GetPostsFeedRes;
import com.example.demo.src.post.model.GetPostsRes;
import com.example.demo.src.post.model.Post;
import com.example.demo.src.post.model.PostPostReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class PostDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetPostsFeedRes> getPosts(int loginIdx) {
        String getUsersQuery = "SELECT Post.idx AS 'postIdx', " +
                "User.idx AS 'userIdx', " +
                "(SELECT title FROM Place WHERE Place.idx = Post.placeIdx) AS placeTitle, " +
                "User.userId, " +
                "User.profileUrl AS 'userProfileUrl', " +
                "(SELECT count(*) FROM PostImage WHERE Post.idx = PostImage.postIdx) AS 'postImgCount', " +
                "(SELECT FORMAT(count(*), '#,#') " +
                "FROM PostLike " +
                "WHERE PostLike.postIdx = Post.idx) AS 'postLikeCount', " +
                "Post.contents AS 'postContents', " +
                "(SELECT FORMAT(count(*), '#,#') " +
                "FROM Comment " +
                "WHERE Post.idx = Comment.postIdx) AS 'commentCount', " +
                "CASE WHEN TIMESTAMPDIFF(SECOND, Post.createdAt, CURRENT_TIMESTAMP()) <= 60 " +
                "THEN concat(TIMESTAMPDIFF(SECOND, Post.createdAt, CURRENT_TIMESTAMP()), '초 전') " +
                "WHEN TIMESTAMPDIFF(MINUTE , Post.createdAt, CURRENT_TIMESTAMP()) <= 60 " +
                "then concat(TIMESTAMPDIFF(MINUTE, Post.createdAt, CURRENT_TIMESTAMP()), '분 전') " +
                "WHEN TIMESTAMPDIFF(HOUR, Post.createdAt, CURRENT_TIMESTAMP()) <= 24 " +
                "then concat(TIMESTAMPDIFF(HOUR, Post.createdAt, CURRENT_TIMESTAMP()), '시간 전') " +
                "WHEN TIMESTAMPDIFF(DAY, Post.createdAt, CURRENT_TIMESTAMP()) <= 7 " +
                "then concat(TIMESTAMPDIFF(DAY, Post.createdAt, CURRENT_TIMESTAMP()), '일 전') " +
                "WHEN TIMESTAMPDIFF(YEAR, Post.createdAt, CURRENT_TIMESTAMP()) < 1 " +
                "then date_format(Post.createdAt, '%c월 %e일') " +
                "ELSE date_format(Post.createdAt, '%Y년 %c월 %e일') " +
                "end as postTimeStamp " +
                "FROM Post INNER JOIN User ON Post.userIdx = User.idx " +
                "WHERE User.idx = ? OR User.idx IN (SELECT followingUserIdx From Follow Where userIdx = ?) AND Post.status = 'OPEN' " +
                "ORDER BY Post.createdAt DESC";

        return this.jdbcTemplate.query(getUsersQuery,
                (rs, rowNum) -> new GetPostsFeedRes(
                        rs.getInt("postIdx"),
                        rs.getInt("userIdx"),
                        rs.getString("placeTitle"),
                        rs.getString("userId"),
                        rs.getString("userProfileUrl"),
                        rs.getInt("postImgCount"),
                        rs.getInt("postLikeCount"),
                        rs.getString("postContents"),
                        rs.getString("commentCount"),
                        rs.getString("postTimeStamp")), loginIdx, loginIdx);
    }

    public List<GetPostsFeedRes> getPostsFeedByUserIdx(int userIdx) {
        String getPostsFeedByUserIdxQuery = "SELECT Post.idx AS 'postIdx', User.idx AS 'userIdx', " +
                "(SELECT title FROM Place WHERE Place.idx = Post.placeIdx) AS placeTitle, " +
                "User.userId, " +
                "User.profileUrl AS 'userProfileUrl', " +
                "(SELECT count(*) " +
                "FROM PostImage " +
                "WHERE Post.idx = PostImage.postIdx) AS 'postImgCount', " +
                "(SELECT FORMAT(count(*), '#,#') " +
                "FROM PostLike " +
                "WHERE PostLike.postIdx = Post.idx) AS 'postLikeCount', " +
                "Post.contents AS 'postContents', " +
                "(SELECT FORMAT(count(*), '#,#') " +
                "FROM Comment WHERE Post.idx = Comment.postIdx) AS 'commentCount', " +
                "CASE WHEN TIMESTAMPDIFF(SECOND, Post.createdAt, CURRENT_TIMESTAMP()) <= 60 " +
                "THEN concat(TIMESTAMPDIFF(SECOND, Post.createdAt, CURRENT_TIMESTAMP()), '초 전') " +
                "WHEN TIMESTAMPDIFF(MINUTE , Post.createdAt, CURRENT_TIMESTAMP()) <= 60 " +
                "then concat(TIMESTAMPDIFF(MINUTE, Post.createdAt, CURRENT_TIMESTAMP()), '분 전') " +
                "WHEN TIMESTAMPDIFF(HOUR , Post.createdAt, CURRENT_TIMESTAMP()) <= 24 " +
                "then concat(TIMESTAMPDIFF(HOUR , Post.createdAt, CURRENT_TIMESTAMP()), '시간 전') " +
                "WHEN TIMESTAMPDIFF(DAY, Post.createdAt, CURRENT_TIMESTAMP()) <= 7 " +
                "then concat(TIMESTAMPDIFF(DAY, Post.createdAt, CURRENT_TIMESTAMP()), '일 전') " +
                "WHEN TIMESTAMPDIFF(YEAR, Post.createdAt, CURRENT_TIMESTAMP()) < 1 " +
                "then concat(Post.createdAt, '%c월 %e일') " +
                "ELSE date_format(Post.createdAt, '%Y년 %c월 %e일') " +
                "end as postTimeStamp " +
                "FROM Post INNER JOIN User ON Post.userIdx = User.idx " +
                "WHERE User.idx = ? AND Post.status = 'OPEN' " +
                "ORDER BY Post.createdAt DESC";

        return this.jdbcTemplate.query(getPostsFeedByUserIdxQuery,
                (rs, rowNum) -> new GetPostsFeedRes(
                        rs.getInt("postIdx"),
                        rs.getInt("userIdx"),
                        rs.getString("placeTitle"),
                        rs.getString("userId"),
                        rs.getString("userProfileUrl"),
                        rs.getInt("postImgCount"),
                        rs.getInt("postLikeCount"),
                        rs.getString("postContents"),
                        rs.getString("commentCount"),
                        rs.getString("postTimeStamp")), userIdx);

    }

    public List<GetPostsRes> getPostsByUserIdx(int userIdx) {
        String getPostsByUserIdx = "SELECT Post.idx AS 'postIdx', " +
                "PostImage.imageUrl AS 'postFirstImage', " +
                "CASE WHEN (SELECT count(*) FROM PostImage WHERE PostImage.postIdx = Post.idx) = 1 AND imageType = 'IMG' " +
                "THEN 'IMG' " +
                "WHEN (SELECT count(*) FROM PostImage WHERE PostImage.postIdx = Post.idx) = 1 AND imageType = 'MV' " +
                "THEN 'MV' " +
                "WHEN  (SELECT count(*) FROM PostImage WHERE PostImage.postIdx = Post.idx) > 1 " +
                "THEN 'IMGS' " +
                "END " +
                "AS 'checkCountStatus' " +
                "From (SELECT postIdx, min(idx) AS 'firstPostImageNo' " +
                "FROM PostImage " +
                "group by postIdx) FirstPostImageTB INNER JOIN PostImage ON FirstPostImageTB.firstPostImageNo = PostImage.idx INNER JOIN " +
                "Post ON PostImage.postIdx = Post.idx AND Post.status = 'OPEN' AND userIdx = ? " +
                "ORDER BY Post.createdAt DESC";
        int PostsByUserIdxParam = userIdx;

        return this.jdbcTemplate.query(getPostsByUserIdx,
                (rs, rowNum) -> new GetPostsRes(
                        rs.getInt("postIdx"),
                        rs.getString("postFirstImage"),
                        rs.getString("checkCountStatus")), PostsByUserIdxParam);
    }

    // Post 저장 idx 반환
    public int createPost(PostPostReq postPostReq, int loginIdx) {
        String createPostQuery = "INSERT INTO Post (userIdx, contents, placeIdx, commentOption) " +
                "VALUES (?, ?, ?, ?)";
        Object[] createPostImageParams = new Object[]{loginIdx, postPostReq.getContents(),
                postPostReq.getPlaceIdx(), postPostReq.getCommentOption()};

        this.jdbcTemplate.update(createPostQuery, createPostImageParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class);
    }

    public void deletePosts(int postIdx) {
        String deletePostQuery = "UPDATE Post set status = 'N' where idx = ?";
        this.jdbcTemplate.update(deletePostQuery, postIdx);
    }

    public int checkPostIdx(int postIdx){
        String checkPostIdxQuery = "select exists(select idx from Post where idx = ? and status != 'N')";
        int checkPostIdxParams = postIdx;
        return this.jdbcTemplate.queryForObject(checkPostIdxQuery, int.class, checkPostIdxParams);
    }

    public List<GetPostLikes> retrievePostLikedUsers(int postIdx, int loginIdx) {
        String retrievePostLikedUsersQuery = "SELECT PostLike.userIdx, User.profileUrl, User.userId, User.name, " +
                "CASE WHEN User.idx IN (SELECT followingUserIdx FROM Follow WHERE userIdx = ? AND status = 'ACTIVE') " +
                "THEN '팔로잉' " +
                "WHEN User.idx IN (SELECT followingUserIdx FROM Follow WHERE userIdx = ? AND status = 'WAIT') " +
                "THEN '요청됨' " +
                "WHEN User.idx = ? " +
                "THEN '-' " +
                "ELSE '팔로우' " +
                "END as followCheck " +
                "FROM PostLike INNER JOIN User ON PostLike.userIdx = User.idx INNER JOIN Post ON PostLike.postIdx = Post.idx " +
                "WHERE PostLike.postIdx = ? AND PostLike.status = 'Y' AND Post.status != 'N' " +
                "order by FIELD(followCheck, '-', '팔로잉', '팔로우')";

        Object[] postLikedUsersParams = new Object[]{loginIdx, loginIdx, loginIdx, postIdx};

        return this.jdbcTemplate.query(retrievePostLikedUsersQuery,
                (rs, rowNum) -> new GetPostLikes(
                        rs.getInt("userIdx"),
                        rs.getString("profileUrl"),
                        rs.getString("userId"),
                        rs.getString("name"),
                        rs.getString("followCheck")), postLikedUsersParams);
    }
}
