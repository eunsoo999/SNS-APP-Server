package com.example.demo.src.postImage;

import com.example.demo.src.postImage.model.GetPostImagesRes;
import com.example.demo.src.postImage.model.PostPostImageReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class PostImageDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // PostImage 저장
    public void createPostImage(PostPostImageReq postPostImageReq, int postIdx) {
        String createPostImageQuery = "INSERT INTO PostImage (postIdx, imageType, imageUrl) VALUES (?, ?, ?)";
        Object[] createPostImageParams = new Object[]{postIdx, postPostImageReq.getImageType(), postPostImageReq.getImageUrl()};

        this.jdbcTemplate.update(createPostImageQuery, createPostImageParams);
    }

    // 게시글에 해당된 PostImage 조회
    public List<GetPostImagesRes> getPostImage(int postIdx) {
        String getPostImageQuery = "SELECT PostImage.imageType, PostImage.imageUrl " +
                "FROM PostImage WHERE postIdx = ?";

        return this.jdbcTemplate.query(getPostImageQuery,
                (rs, rowNum) -> new GetPostImagesRes(
                        rs.getString("imageType"),
                        rs.getString("imageUrl")), postIdx);
    }
}