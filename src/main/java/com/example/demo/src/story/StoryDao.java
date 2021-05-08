package com.example.demo.src.story;

import com.example.demo.src.story.model.GetStorysRes;
import com.example.demo.src.story.model.GetUserStorysRes;
import com.example.demo.src.story.model.PostStoryReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class StoryDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // 최신 스토리 조회
    public List<GetStorysRes> getStorys(int loginIdx) {
        String getFollowUserStorysQuery =
                "select Story.idx as idx, Story.userIdx as userIdx, FirstStory.userId , FirstStory.profileUrl " +
                "from Story inner join " +
                "(select Story.userIdx, User.userId, User.profileUrl, max(Story.idx) as latestStory " +
                        "from Story inner join User on Story.userIdx = User.idx " +
                        "group by Story.userIdx) FirstStory " +
                "where latestStory = idx and Story.status = 'ACTIVE' and Story.userIdx in " +
                        "(select followingUserIdx from Follow where Follow.userIdx = ?) " +
                "order by Story.createdAt desc";
        int getUserParams = loginIdx;
        return this.jdbcTemplate.query(getFollowUserStorysQuery,
                (rs, rowNum) -> new GetStorysRes(
                        rs.getInt("idx"),
                        rs.getInt("userIdx"),
                        rs.getString("userId"),
                        rs.getString("profileUrl")), getUserParams);
    }

    //특정 유저의 스토리 조회
    public List<GetUserStorysRes> getUserStorys(int userIdx) {
        String query =
                "select User.userId as userId, User.profileUrl as profileUrl, Story.videoUrl as videoUrl, " +
                        "case when TIMESTAMPDIFF(SECOND, Story.createdAt, CURRENT_TIMESTAMP()) <= 60 " +
                        "then concat(TIMESTAMPDIFF(SECOND, Story.createdAt, CURRENT_TIMESTAMP()), '초 전') " +
                        "when TIMESTAMPDIFF(MINUTE, Story.createdAt, CURRENT_TIMESTAMP()) <= 60 " +
                        "then concat(TIMESTAMPDIFF(MINUTE, Story.createdAt, CURRENT_TIMESTAMP()), '분 전') " +
                        "when TIMESTAMPDIFF(HOUR, Story.createdAt, CURRENT_TIMESTAMP()) <= 24 " +
                        "then concat(TIMESTAMPDIFF(HOUR, Story.createdAt, CURRENT_TIMESTAMP()), '시간 전') " +
                        "end as storyTimeStemp " +
                        "from Story inner join User on Story.userIdx = User.idx " +
                        "where Story.userIdx = ? and Story.status = 'ACTIVE' " +
                        "order by Story.createdAt desc";

        int getUserParams = userIdx;

        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new GetUserStorysRes(
                        rs.getString("userId"),
                        rs.getString("profileUrl"),
                        rs.getString("videoUrl"),
                        rs.getString("storyTimeStemp")), getUserParams);

    }

    //특정 유저의 스토리 개수
    public int getStoryCount(int userIdx) {
        String query = "select count(*) from Story where userIdx = ? and status = 'ACTIVE'";
        int getUserParams = userIdx;
        return this.jdbcTemplate.queryForObject(query, int.class, getUserParams);
    }

    //스토리 작성
    public int createStory(PostStoryReq patchStoryReq, int loginIdx) {
        String insertStoryQuery = "INSERT INTO Story (userIdx, videoUrl, status) VALUES (?, ?, 'ACTIVE')";
        this.jdbcTemplate.update(insertStoryQuery, loginIdx, patchStoryReq.getVideoUrl());

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class); //todo 동시에..?
    }

    public void deleteStory(int storyIdx) {
        String deleteStoryQuery = "UPDATE Story set status = 'N' where idx = ?";
        this.jdbcTemplate.update(deleteStoryQuery, storyIdx);
    }

    public int checkStoryIdx(int storyIdx) {
        String checkStoryIdxQuery = "select exists(select idx from Story where idx = ? and status != 'N')";
        int checkStoryIdxParams = storyIdx;
        return this.jdbcTemplate.queryForObject(checkStoryIdxQuery, int.class, checkStoryIdxParams);
    }
}
