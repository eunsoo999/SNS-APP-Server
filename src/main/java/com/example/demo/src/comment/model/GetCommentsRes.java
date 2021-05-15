package com.example.demo.src.comment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetCommentsRes {
    private int commentIdx;
    private int parentIdx;
    private String userId;
    private String userImageUrl;
    private String contents;
    private String timeStamp;
    private int likeCount;
    private int replyCount;
    private boolean likeCheck;
}
//댓글조회
