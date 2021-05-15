package com.example.demo.src.chat;

import com.example.demo.src.chat.model.GetChats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class ChatDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetChats> selectChats(int roomIdx, int loginIdx) {
        String selectChatsQuery = "SELECT User.idx as userIdx, IF(User.idx = ?, null, User.profileUrl) as userProfileUrl, Chat.message, " +
                "IF(User.idx = ?, true, false) as 'isMine' " +
                "FROM Chat INNER JOIN User ON Chat.userIdx = User.idx " +
                "WHERE Chat.roomIdx = ? AND Chat.status = 'Y' " +
                "ORDER BY Chat.createdAt DESC";

        return this.jdbcTemplate.query(selectChatsQuery,
                (rs, rowNum) -> new GetChats(
                        rs.getInt("userIdx"),
                        rs.getString("userProfileUrl"),
                        rs.getString("message"),
                        rs.getBoolean("isMine")), loginIdx, loginIdx, roomIdx);
    }


}
