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

    public int createHighlightStory(PostHighlightStoryReq postHighlightStoryReq) {
        String createHighlightStoryQuery = "INSERT INTO HighlightStory (highlightIdx, storyIdx) VALUES (?, ?)";
        Object[] createHighlightStoryParams = new Object[]{postHighlightStoryReq.getHighlightIdx(), postHighlightStoryReq.getStoryIdx()};

        this.jdbcTemplate.update(createHighlightStoryQuery, createHighlightStoryParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class);
    }
}
