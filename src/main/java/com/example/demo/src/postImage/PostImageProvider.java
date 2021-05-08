package com.example.demo.src.postImage;

import com.example.demo.src.postImage.model.GetPostImagesRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostImageProvider {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PostImageDao postImageDao;

    @Autowired
    public PostImageProvider(PostImageDao postImageDao) {
        this.postImageDao = postImageDao;
    }

    public List<GetPostImagesRes> getPostImage(int postIdx) {
        return postImageDao.getPostImage(postIdx);
    }
}
