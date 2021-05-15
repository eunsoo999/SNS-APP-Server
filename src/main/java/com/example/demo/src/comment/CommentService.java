package com.example.demo.src.comment;

import com.example.demo.config.BaseException;
import com.example.demo.src.comment.model.PostCommentsReq;
import com.example.demo.src.comment.model.PostCommentsRes;
import com.example.demo.src.post.PostDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
@Transactional(rollbackOn = BaseException.class)
public class CommentService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PostDao postDao;
    private final CommentDao commentDao;

    @Autowired
    public CommentService(PostDao postDao, CommentDao commentDao) {
        this.postDao = postDao;
        this.commentDao = commentDao;
    }

    public PostCommentsRes createComments(PostCommentsReq postCommentsReq, int loginIdx) throws BaseException {
        // 게시판 존재 확인
        if (postDao.checkPostIdx(postCommentsReq.getPostIdx()) == 0) {
            throw new BaseException(POSTS_NOT_EXISTS);
        }
        // 부모댓글이 있는지 확인
        if (postCommentsReq.getParentIdx() != 0 && commentDao.checkParentComment(postCommentsReq.getPostIdx(), postCommentsReq.getParentIdx()) == 0) {
            throw new BaseException(POST_COMMENTS_NOT_EXISTS);
        }

        try {
            // 부모댓글인 경우
            if (postCommentsReq.getParentIdx() == 0) {
                int createdIdx = commentDao.insertParentComment(postCommentsReq, loginIdx);
                return new PostCommentsRes(createdIdx, postCommentsReq.getPostIdx());
            } else {
                // 자식댓글(대댓글)인 경우
                int createdIdx = commentDao.insertChildComment(postCommentsReq, loginIdx);
                return new PostCommentsRes(createdIdx, postCommentsReq.getPostIdx());
            }
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int updateCommentStatus(int commentIdx, int loginIdx) throws BaseException {
        // 댓글이 존재하는지
        if (commentDao.checkComment(commentIdx) == 0) {
            throw new BaseException(COMMENTS_NOT_EXISTS);
        }
        if (commentDao.checkCommentOwner(commentIdx, loginIdx) == 0) {
            throw new BaseException(INVALID_USER_JWT);
        }
        try {
            if (commentDao.isParentComment(commentIdx) == 1) {
                //부모댓글인 경우 자식댓글도 삭제처리
                commentDao.updateChildCommentsStatus(commentIdx);
                commentDao.updateCommentStatus(commentIdx);
            } else {
                commentDao.updateCommentStatus(commentIdx);
            }
            return commentIdx;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
