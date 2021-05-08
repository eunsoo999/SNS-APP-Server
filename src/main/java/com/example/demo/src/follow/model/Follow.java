package com.example.demo.src.follow.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
public class Follow {
    private int idx;
    private int userIdx;
    private int followingUserIdx;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String status;
}
