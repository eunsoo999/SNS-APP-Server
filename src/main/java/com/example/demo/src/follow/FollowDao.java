package com.example.demo.src.follow;

import com.example.demo.src.follow.model.PostFollowReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class FollowDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int createFollow(PostFollowReq postFollowReq, int loginIdx){
        String createFollowQuery = "INSERT INTO Follow (userIdx, followingUserIdx, status) VALUES (?, ?, ?)";

        String followingUserStatus = postFollowReq.getFollowingUserStatus();
        String followStatus = "";
        if (followingUserStatus.equals("PUBLIC")) {
            followStatus = "ACTIVE";
        } else if (followingUserStatus.equals("PRIVATE")) {
            followStatus = "WAIT";
        }
        Object[] createFollowParams = new Object[]{loginIdx, postFollowReq.getFollowingUserIdx(), followStatus};
        this.jdbcTemplate.update(createFollowQuery, createFollowParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }

    public void deleteFollow(int followIdx) {
        String deleteFollowQuery = "UPDATE Follow set status = 'CANCEL' where idx = ?";
        this.jdbcTemplate.update(deleteFollowQuery, followIdx);
    }

    public int checkFollowsIdx(int followIdx){
        String checkFollowIdxQuery = "select exists(select idx from Follow where idx = ? and status != 'CANCEL')";
        int checkFollowIdxParams = followIdx;
        return this.jdbcTemplate.queryForObject(checkFollowIdxQuery, int.class, checkFollowIdxParams);

    }

}
