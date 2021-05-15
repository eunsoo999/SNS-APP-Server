package com.example.demo.src.highlight;

import com.example.demo.src.highlight.model.GetHighlightRes;
import com.example.demo.src.highlight.model.Highlight;
import com.example.demo.src.highlight.model.PatchHighlightReq;
import com.example.demo.src.highlight.model.PostHighlightReq;
import com.example.demo.src.highlightStory.model.GetHighlightStoryRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class HighlightDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetHighlightRes> getHighlights(int userIdx) {
        String getHighlightsQuery = "SELECT idx AS 'highlightIdx', thumbnailUrl, title " +
                "FROM Highlight " +
                "WHERE userIdx = ? AND status = 'Y' " +
                "ORDER BY Highlight.updatedAt DESC";

        int getUserParams = userIdx;

        return this.jdbcTemplate.query(getHighlightsQuery, (rs, rowNum) -> new GetHighlightRes(
                rs.getInt("highlightIdx"),
                rs.getString("thumbnailUrl"),
                rs.getString("title")), getUserParams);
    }

    public Highlight getHighlight(int highlightIdx) {
        String getHighlightQuery = "SELECT idx, useridx, title, thumbnailUrl, " +
                "createdat, updatedat, status " +
                "FROM Highlight " +
                "WHERE idx = ? AND status = 'Y'";
        int getUserParams = highlightIdx;

        return this.jdbcTemplate.queryForObject(getHighlightQuery, Highlight.class, getUserParams);
    }

    public List<GetHighlightStoryRes> getHighlightStorys(int highlightIdx) {
        String getHighlightStorysQuery = "SELECT HighlightStory.idx AS 'highlightStoryIdx', Story.videoUrl, " +
                "case when TIMESTAMPDIFF(SECOND, Story.createdAt, CURRENT_TIMESTAMP()) <= 60 " +
                "then concat(TIMESTAMPDIFF(SECOND, Story.createdAt, CURRENT_TIMESTAMP()), '초') " +
                "when TIMESTAMPDIFF(MINUTE, Story.createdAt, CURRENT_TIMESTAMP()) <= 60 " +
                "then concat(TIMESTAMPDIFF(MINUTE, Story.createdAt, CURRENT_TIMESTAMP()), '분') " +
                "when TIMESTAMPDIFF(HOUR, Story.createdAt, CURRENT_TIMESTAMP()) <= 24 " +
                "then concat(TIMESTAMPDIFF(HOUR, Story.createdAt, CURRENT_TIMESTAMP()), '시간') " +
                "when TIMESTAMPDIFF(DAY, Story.createdAt, CURRENT_TIMESTAMP()) < 7 " +
                "then concat(TIMESTAMPDIFF(DAY, Story.createdAt, CURRENT_TIMESTAMP()), '일') " +
                "ELSE concat(TIMESTAMPDIFF(WEEK, Story.createdAt, CURRENT_TIMESTAMP()), '주') " +
                "end AS storyTimeStemp " +
                "FROM HighlightStory INNER JOIN Story ON HighlightStory.storyIdx = Story.idx " +
                "WHERE HighlightStory.highlightIdx = ? AND HighlightStory.status = 'Y'";

        int getHighlightParams = highlightIdx;

        return this.jdbcTemplate.query(getHighlightStorysQuery, (rs, rowNum) -> new GetHighlightStoryRes(
                rs.getInt("highlightStoryIdx"),
                rs.getString("videoUrl"),
                rs.getString("storyTimeStemp")), getHighlightParams);

    }

    //하이라이트 스토리 개수
    public int getHighlightStoryCount(int highlightIdx) {
        String getHighlightStoryCountQuery = "SELECT count(*) AS HighlightStoryCount " +
                "FROM HighlightStory " +
                "WHERE HighlightStory.highlightIdx = ? AND HighlightStory.status = 'Y'";

        int getHighlightParams = highlightIdx;

        return this.jdbcTemplate.queryForObject(getHighlightStoryCountQuery, int.class, getHighlightParams);
    }

    public int createHighlight(PostHighlightReq postHighlightReq, int loginIdx) {
        String createHighlightQuery = "INSERT INTO Highlight (userIdx, title, thumbnailUrl) VALUES (?, ?, ?)";
        Object[] createHighlightParams = new Object[]{loginIdx, postHighlightReq.getTitle(), postHighlightReq.getThumbnailUrl()};

        this.jdbcTemplate.update(createHighlightQuery, createHighlightParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }

    public int checkHighlightByIdx(int highlightIdx) {
        String checkHighlightByIdxQuery = "select exists(select idx from Highlight where idx = ? and status != 'N')";
        int checkHighlightIdxParams = highlightIdx;
        return this.jdbcTemplate.queryForObject(checkHighlightByIdxQuery, int.class, checkHighlightIdxParams);
    }

    public int updateHighlights(PatchHighlightReq patchHighlightReq, int highlightIdx) {
        String updateHighlightsQuery = "update Highlight set title = ?, thumbnailUrl = ? where idx = ? ";
        return this.jdbcTemplate.update(updateHighlightsQuery, patchHighlightReq.getTitle(), patchHighlightReq.getThumbnailUrl(), highlightIdx);
    }

    public int checkHighlightOwner(int highlightIdx, int loginIdx) {
        String checkHighlightOwnerQuery = "select exists(select Highlight.idx from Highlight where Highlight.idx = ? and Highlight.userIdx = ? and status != 'N')";
        Object[] params = {highlightIdx, loginIdx};
        return this.jdbcTemplate.queryForObject(checkHighlightOwnerQuery, int.class, params);
    }

    public int checkFollow(int loginIdx, int followingUserIdx) {
        String checkFollowQuery = "select exists(select idx from Follow where userIdx = ? and followingUserIdx = ? and status != 'CANCEL')";

        return this.jdbcTemplate.queryForObject(checkFollowQuery, int.class, loginIdx, followingUserIdx);

    }

    public void updateHighlightsStatus(int highlightIdx) {
        String updateHighlightsQuery = "update Highlight set status = 'N' where idx = ? ";
        this.jdbcTemplate.update(updateHighlightsQuery, highlightIdx);
    }
}
