package com.example.demo.src.PostLike.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetPostLikes {
    private int userIdx;
    private String profileUrl;
    private String userId;
    private String name;
    private String followCheck;
}
