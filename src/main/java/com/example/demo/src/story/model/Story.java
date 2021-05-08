package com.example.demo.src.story.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
public class Story {
    private int idx;
    private int userIdx;
    private String videoUrl;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String status; // ACTIVE : 활성화, INACTIVE : 비활성화, N : 삭제
}
