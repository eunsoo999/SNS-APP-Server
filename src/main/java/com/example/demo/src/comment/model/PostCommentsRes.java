package com.example.demo.src.comment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostCommentsRes {
    private int createdCommentIdx;
    private int postIdx;
}
//댓글작성응답