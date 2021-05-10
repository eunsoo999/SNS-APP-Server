package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetCommonUserRes {
    private String CommonFollowCount;
    private String followerCount;
    private String followingCount;
    private List<FollowUser> commonUsers = new ArrayList<>();
    private List<GetRecommendUsersRes> recommendUsers = new ArrayList<>();

}
