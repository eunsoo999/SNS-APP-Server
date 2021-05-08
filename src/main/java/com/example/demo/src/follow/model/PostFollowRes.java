package com.example.demo.src.follow.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostFollowRes {
    private int followIdx;
    private int followingUserIdx; //사용자가 팔로우한 유저의 번호
    private int followerUserIdx;  //팔로우 신청한 유저
}
