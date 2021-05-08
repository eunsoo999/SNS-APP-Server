package com.example.demo.src.post.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetPostsRes {
    private int postIdx;
    private String postFirstImage;
    private String checkCountStatus;
}

