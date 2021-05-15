package com.example.demo.src.tag;

import com.example.demo.src.tag.model.Tag;
import com.example.demo.src.user.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class TagDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int createTags(String tagName) {
        String createTagsQuery = "INSERT INTO Tag (tagName) VALUES (?)";
        String createTagsParams = tagName;

        this.jdbcTemplate.update(createTagsQuery, tagName);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class);
    }

    public Tag getTagsByTagName(String tagName) {
        String getTagsByTagNameQuery = "select idx, tagName, createdAt, updatedAt from Tag where tagName = ?";
        return this.jdbcTemplate.queryForObject(getTagsByTagNameQuery,(rs,rowNum)-> new Tag(
                rs.getInt("idx"),
                rs.getString("tagName"),
                rs.getTimestamp("createdAt"),
                rs.getTimestamp("updatedAt")), tagName);
    }

    public int checkTagByTagName(String tagName) {
        String checkTagQuery = "select exists(select idx from Tag where tagName = ?)";
        String checkTagParams = tagName;
        return this.jdbcTemplate.queryForObject(checkTagQuery, int.class, checkTagParams);
    }

    public int checkTagByIdx(int tagIdx) {
        String checkTagByIdxQuery = "select exists(select idx from Tag where idx = ?)";
        int checkTagParams = tagIdx;
        return this.jdbcTemplate.queryForObject(checkTagByIdxQuery, int.class, checkTagParams);
    }
}
