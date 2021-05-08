package com.example.demo.src.follow.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostFollowReq {
    private int followingUserIdx;
    private String followingUserStatus; //PUBLIC OR PRIVATE
}
