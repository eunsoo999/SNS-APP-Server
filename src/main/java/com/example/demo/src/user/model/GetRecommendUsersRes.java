package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetRecommendUsersRes {
    private int userIdx;
    private String userProfileUrl;
    private String userId;
    private String name;
    private String recommendFollowMessage;
    private String followCheck;
}