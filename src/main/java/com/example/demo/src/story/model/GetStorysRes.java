package com.example.demo.src.story.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetStorysRes {
    private int idx;
    private int userIdx;
    private String userId;
    private String profileUrl;
}
