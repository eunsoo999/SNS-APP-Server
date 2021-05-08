package com.example.demo.src.post;

import com.example.demo.config.BaseException;
import com.example.demo.src.post.model.GetPostsFeedRes;
import com.example.demo.src.post.model.GetPostsRes;
import com.example.demo.src.user.UserProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;
import static com.example.demo.config.BaseResponseStatus.USERS_IDX_NOT_EXISTS;

@Service
public class PostProvider {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PostDao postDao;
    private final UserProvider userProvider;

    @Autowired
    public PostProvider(PostDao postDao, UserProvider userProvider) {
        this.postDao = postDao;
        this.userProvider = userProvider;
    }

    public List<GetPostsFeedRes> getPosts(int loginIdx) throws BaseException {
        try {
            List<GetPostsFeedRes> getPostRes = postDao.getPosts(loginIdx);
            return getPostRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //게시물미리보기
    public List<GetPostsRes> getPostsByUserIdx(int userIdx) throws BaseException {
        if (userProvider.checkUserIdx(userIdx) == 0) {
            throw new BaseException(USERS_IDX_NOT_EXISTS);
        }
        try {
            List<GetPostsRes> getPostRes = postDao.getPostsByUserIdx(userIdx);
            return getPostRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetPostsFeedRes> getPostsFeedByUserIdx(int userIdx) throws BaseException {
        if (userProvider.checkUserIdx(userIdx) == 0) {
            throw new BaseException(USERS_IDX_NOT_EXISTS);
        }
        try {
            List<GetPostsFeedRes> getPostRes = postDao.getPostsFeedByUserIdx(userIdx);
            return getPostRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkPostIdx(int postIdx) {
        return postDao.checkPostIdx(postIdx);
    }


}
