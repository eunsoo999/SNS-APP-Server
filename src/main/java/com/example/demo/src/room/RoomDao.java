package com.example.demo.src.room;

import com.example.demo.src.room.model.GetRoomImages;
import com.example.demo.src.room.model.GetRoomsResponse;
import com.example.demo.src.room.model.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class RoomDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetRoomsResponse> selectRooms(int loginIdx) {
        String selectRoomQuery = "SELECT Member.roomIdx, title as roomName, lastChatMessage, " +
                "CASE WHEN timestampdiff(SECOND, lastChatMessageTimestamp, current_timestamp) < 60 " +
                "THEN concat(TIMESTAMPDIFF(SECOND, lastChatMessageTimestamp, CURRENT_TIMESTAMP()), '초') " +
                "WHEN timestampdiff(MINUTE, lastChatMessageTimestamp, current_timestamp) < 60 " +
                "THEN concat(timestampdiff(MINUTE, lastChatMessageTimestamp, current_timestamp), '분') " +
                "WHEN timestampdiff(HOUR, lastChatMessageTimestamp, current_timestamp) < 24 " +
                "THEN concat(timestampdiff(HOUR, lastChatMessageTimestamp, current_timestamp), '시간') " +
                "WHEN timestampdiff(DAY, lastChatMessageTimestamp, current_timestamp) < 31 " +
                "THEN concat(timestampdiff(DAY, lastChatMessageTimestamp, current_timestamp), '일') " +
                "ELSE concat(timestampdiff(WEEK, lastChatMessageTimestamp, current_timestamp), '주') " +
                "END AS lastChatMessageTimestamp " +
                "FROM Member INNER JOIN Room ON Member.roomIdx = Room.idx " +
                "INNER JOIN ( " +
                "SELECT Chat.roomIdx, Chat.message as lastChatMessage, Chat.createdAt as lastChatMessageTimestamp " +
                "FROM Chat INNER JOIN (select roomIdx, max(idx) as CurrentMessageNo from Chat " +
                "group by roomIdx) CurrentMessage " +
                "where CurrentMessageNo = idx) LastChatMessage " +
                "On LastChatMessage.roomIdx = Member.roomIdx " +
                "where userIdx = ?";

        return this.jdbcTemplate.query(selectRoomQuery,
                (rs, rowNum) -> new GetRoomsResponse(
                        rs.getInt("roomIdx"),
                        rs.getString("roomName"),
                        rs.getString("lastChatMessage"),
                        rs.getString("lastChatMessageTimestamp")), loginIdx);
    }

    public List<GetRoomImages> selectRoomImageUrl(int loginIdx, int roomIdx) {
        String selectRoomImageUrl = "SELECT userIdx, profileUrl as 'imageUrl' " +
                "FROM Member INNER JOIN User ON Member.userIdx = User.idx " +
                "WHERE Member.userIdx != ? AND roomIdx = ?";

        return this.jdbcTemplate.query(selectRoomImageUrl,
                (rs, rowNum) -> new GetRoomImages(
                        rs.getString("imageUrl")), loginIdx, roomIdx);
    }

    public int checkRoomsAndMember(int roomIdx, int loginIdx) {
        String checkRoomsAndMemberQuery = "select exists(select idx from Member where roomIdx = ? and userIdx = ? and status = 'Y')";
        return this.jdbcTemplate.queryForObject(checkRoomsAndMemberQuery, int.class, roomIdx, loginIdx);
    }

    public Room selectRoom(int roomIdx) {
        String selectRoomQuery = "SELECT idx, title, createdAt, updatedAt, status FROM Room WHERE idx = ?";

        return this.jdbcTemplate.queryForObject(selectRoomQuery,
                (rs, rowNum) -> new Room(
                        rs.getInt("idx"),
                        rs.getString("title"),
                        rs.getTimestamp("createdAt"),
                        rs.getTimestamp("updatedAt"),
                        rs.getString("status")), roomIdx);
    }
}
