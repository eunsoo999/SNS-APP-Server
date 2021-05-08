package com.example.demo.src.post.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
public class Post {
    private int idx;
    private int userIdx;
    private String contents;
    private int placeIdx;
    private String commentOption; // 댓글기능사용여부 Y : 댓글기능설정, N : 댓글기능해제
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String status; // 상태 OPEN : 공개, HIDE : 숨김상태, N : 삭제

}
