package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FollowUser {
    private int userIdx;
    private String userProfileUrl;
    private String userId;
    private String name;
    private String followCheck;
}
