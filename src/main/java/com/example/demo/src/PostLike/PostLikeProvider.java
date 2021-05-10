package com.example.demo.src.PostLike;

import com.example.demo.src.post.PostDao;
import com.example.demo.src.user.UserProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostLikeProvider {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PostLikeDao postLikeDao;

    @Autowired
    public PostLikeProvider(PostLikeDao postLikeDao) {
        this.postLikeDao = postLikeDao;
    }


}
