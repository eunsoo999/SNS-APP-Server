package com.example.demo.src.user.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetUserRes {
    private int userIdx;
    private String userId;
    private String name;
    private String profileUrl;
    private String websiteUrl;
    private String introduction;
    private String postCount;
    private String followerCount;
    private String followingCount;
}
