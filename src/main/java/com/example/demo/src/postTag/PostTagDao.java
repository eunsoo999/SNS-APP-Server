package com.example.demo.src.postTag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class PostTagDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int createPostTag(int postIdx, int tagIdx) {
        String createPostTagQuery = "INSERT INTO PostTag (postIdx, tagIdx) VALUES (?, ?)";
        this.jdbcTemplate.update(createPostTagQuery, postIdx, tagIdx);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class);
    }

    public void patchStatusPostTags(int postIdx) {
        String patchStatusPostTagQuery = "UPDATE PostTag set status = 'N' where postIdx = ?";
        this.jdbcTemplate.update(patchStatusPostTagQuery, postIdx);
    }


}
