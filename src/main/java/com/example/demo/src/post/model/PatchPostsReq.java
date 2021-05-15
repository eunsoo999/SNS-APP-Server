package com.example.demo.src.post.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatchPostsReq {
    private String contents;
    private int placeIdx;
    //private String commentOption; 이건 따로 API 필요 todo
}
