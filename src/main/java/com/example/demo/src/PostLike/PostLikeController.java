package com.example.demo.src.PostLike;

import com.example.demo.src.user.UserProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class PostLikeController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final PostLikeProvider postLikeProvider;
    @Autowired
    private final PostLikeService postLikeService;

    public PostLikeController(PostLikeProvider postLikeProvider, PostLikeService postLikeService) {
        this.postLikeProvider = postLikeProvider;
        this.postLikeService = postLikeService;
    }
}
