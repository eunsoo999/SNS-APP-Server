package com.example.demo.src.post;

import com.example.demo.config.BaseException;
import com.example.demo.src.PostLike.model.GetPostLikes;
import com.example.demo.src.post.model.PostPostReq;
import com.example.demo.src.post.model.PostPostRes;
import com.example.demo.src.postImage.PostImageService;
import com.example.demo.src.postImage.model.PostPostImageReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;
import static com.example.demo.config.BaseResponseStatus.DELETE_POSTS_NOT_EXISTS;

@Service
public class PostService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PostDao postDao;
    private final PostProvider postProvider;
    private final PostImageService postImageService;

    @Autowired
    public PostService(PostDao postDao, PostProvider postProvider, PostImageService postImageService) {
        this.postDao = postDao;
        this.postProvider = postProvider;
        this.postImageService = postImageService;
    }

    public PostPostRes createPost(PostPostReq postPostReq, int loginIdx) throws BaseException{

        try {
            int postIdx = postDao.createPost(postPostReq, loginIdx);
            for (PostPostImageReq postImageReq : postPostReq.getPostImage()) {
                postImageService.createPostImage(postImageReq, postIdx);
            }
            return new PostPostRes(postIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void deletePosts(int postIdx) throws BaseException {
        if (postProvider.checkPostIdx(postIdx) == 0) {
            throw new BaseException(DELETE_POSTS_NOT_EXISTS);
        }
        try {
            postDao.deletePosts(postIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
