package com.example.demo.src.comment;

import com.example.demo.src.comment.model.GetCommentsRes;
import com.example.demo.src.comment.model.PostCommentsReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class CommentDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int insertParentComment(PostCommentsReq postCommentsReq, int userIdx) {
        String insertParentCommentQuery = "INSERT INTO Comment(postIdx, contents, userIdx) VALUES(?, ?, ?)";
        this.jdbcTemplate.update(insertParentCommentQuery, postCommentsReq.getPostIdx(), postCommentsReq.getContents(), userIdx);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }

    public int insertChildComment(PostCommentsReq postCommentsReq, int userIdx) {
        String insertChildCommentQuery = "INSERT INTO Comment(postIdx, contents, parentIdx, userIdx) VALUES(?, ?, ?, ?)";
        this.jdbcTemplate.update(insertChildCommentQuery, postCommentsReq.getPostIdx(),
                postCommentsReq.getContents(), postCommentsReq.getParentIdx(), userIdx);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }

    public List<GetCommentsRes> selectComments(int postIdx, int loginIdx) {
        String selectParentCommentsQuery = "SELECT Comment.idx AS 'commentIdx', " +
                "Comment.parentIdx, User.userId, User.profileUrl AS 'userImageUrl', Comment.contents, " +
                "CASE WHEN timestampdiff(SECOND, Comment.createdAt, current_timestamp) < 60 " +
                "THEN concat(TIMESTAMPDIFF(SECOND, Comment.createdAt, CURRENT_TIMESTAMP()), '초') " +
                "WHEN timestampdiff(MINUTE, Comment.createdAt, current_timestamp) < 60 " +
                "THEN concat(timestampdiff(MINUTE, Comment.createdAt, current_timestamp), '분') " +
                "WHEN timestampdiff(HOUR, Comment.createdAt, current_timestamp) < 24 " +
                "THEN concat(timestampdiff(HOUR, Comment.createdAt, current_timestamp), '시간') " +
                "WHEN timestampdiff(DAY, Comment.createdAt, current_timestamp) < 31 " +
                "THEN concat(timestampdiff(DAY, Comment.createdAt, current_timestamp), '일') " +
                "ELSE concat(timestampdiff(WEEK, Comment.createdAt, current_timestamp), '주') " +
                "END AS timestamp, " +
                "(SELECT count(*) FROM CommentLike WHERE CommentLike.commentIdx = Comment.idx AND CommentLike.status = 'Y') AS 'likeCount', " +
                "(SELECT count(*) FROM Comment child WHERE child.parentIdx = Comment.idx) AS 'replyCount', " +
                "(SELECT IF((SELECT count(*) FROM CommentLike WHERE CommentLike.commentIdx = Comment.idx AND CommentLike.userIdx = ?) > 0, true, false)) AS likeCheck " +
                "FROM Post JOIN Comment ON Post.idx = Comment.postIdx JOIN User ON Comment.userIdx = User.idx " +
                "WHERE Comment.status = 'Y' AND Post.idx = ? " +
                "ORDER BY COALESCE(Comment.parentIdx, Comment.idx), Comment.parentIdx IS NOT NULL, Comment.idx";

        return this.jdbcTemplate.query(selectParentCommentsQuery,
                (rs, rowNum) -> new GetCommentsRes(
                        rs.getInt("commentIdx"),
                        rs.getInt("parentIdx"),
                        rs.getString("userId"),
                        rs.getString("userImageUrl"),
                        rs.getString("contents"),
                        rs.getString("timestamp"),
                        rs.getInt("likeCount"),
                        rs.getInt("replyCount"),
                        rs.getBoolean("likeCheck")), loginIdx, postIdx);
    }

    // 댓글 상태 수정
    public int updateCommentStatus(int commentIdx) {
        String updateCommentStatusQuery =  "UPDATE Comment SET status = 'N' WHERE idx = ?";
        return this.jdbcTemplate.update(updateCommentStatusQuery, commentIdx);
    }

    // 부모댓글인지 확인
    public int isParentComment(int commentIdx) {
        String checkParentCommentQuery = "select exists(select idx from Comment where idx = ? and status = 'Y' and parentIdx IS NULL)";
        return this.jdbcTemplate.queryForObject(checkParentCommentQuery, int.class, commentIdx);
    }

    // 해당 게시글의 부모댓글이 있는지 확인
    public int checkParentComment(int postIdx, int parentIdx) {
        String checkParentCommentQuery = "select exists(select idx from Comment where idx = ? and postIdx = ? and status = 'Y' and parentIdx IS NULL)";
        return this.jdbcTemplate.queryForObject(checkParentCommentQuery, int.class, parentIdx, postIdx);
    }

    public int checkComment(int commentIdx) {
        String checkCommentQuery = "select exists(select idx from Comment where idx = ? and status = 'Y')";
        return this.jdbcTemplate.queryForObject(checkCommentQuery, int.class, commentIdx);
    }

    public int updateChildCommentsStatus(int commentIdx) {
        String updateCommentStatusQuery =  "UPDATE Comment SET status = 'N' WHERE parentIdx = ?";
        return this.jdbcTemplate.update(updateCommentStatusQuery, commentIdx);
    }

    public int checkCommentOwner(int commentIdx, int loginIdx) {
        String checkCommentQuery = "select exists(select idx from Comment where idx = ? and userIdx = ? and status = 'Y')";
        return this.jdbcTemplate.queryForObject(checkCommentQuery, int.class, commentIdx, loginIdx);
    }
}
