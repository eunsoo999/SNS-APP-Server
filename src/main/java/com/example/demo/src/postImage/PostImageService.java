package com.example.demo.src.postImage;

import com.example.demo.src.postImage.model.PostPostImageReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostImageService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PostImageDao postImageDao;

    @Autowired
    public PostImageService(PostImageDao postImageDao) {
        this.postImageDao = postImageDao;
    }

    public void createPostImage(PostPostImageReq postPostImageReq, int postIdx) {
        postImageDao.createPostImage(postPostImageReq, postIdx);
    }
}
