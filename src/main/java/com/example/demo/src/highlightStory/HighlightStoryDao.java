package com.example.demo.src.highlightStory;

import com.example.demo.src.highlightStory.model.PostHighlightStoryReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class HighlightStoryDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int createHighlightStory(int storysIdx, int highlightIdx) {
        String createHighlightStoryQuery = "INSERT INTO HighlightStory (highlightIdx, storyIdx) VALUES (?, ?)";
        Object[] createHighlightStoryParams = new Object[]{highlightIdx, storysIdx};

        this.jdbcTemplate.update(createHighlightStoryQuery, createHighlightStoryParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class);
    }

    public int updateHighlightStoryStatus(int highlightIdx) {
        String updateStatusQuery = "UPDATE HighlightStory SET status = 'N' WHERE highlightIdx = ? ";
        this.jdbcTemplate.update(updateStatusQuery, highlightIdx);

        return highlightIdx;
    }
}
