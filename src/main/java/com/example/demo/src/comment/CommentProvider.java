package com.example.demo.src.comment;

import com.example.demo.config.BaseException;
import com.example.demo.src.comment.model.GetCommentsRes;
import com.example.demo.src.post.PostDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class CommentProvider {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final CommentDao commentDao;
    private final PostDao postDao;

    @Autowired
    public CommentProvider(CommentDao commentDao,  PostDao postDao) {
        this.commentDao = commentDao;
        this.postDao = postDao;
    }

    public List<GetCommentsRes> retrieveComments(int postIdx, int loginIdx) throws BaseException {
        // 게시글이 존재하는지 확인
        if (postDao.checkPostIdx(postIdx) == 0) {
            throw new BaseException(POSTS_NOT_EXISTS);
        }
        try {
            List<GetCommentsRes> getCommentsList = commentDao.selectComments(postIdx, loginIdx);
            return getCommentsList;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
