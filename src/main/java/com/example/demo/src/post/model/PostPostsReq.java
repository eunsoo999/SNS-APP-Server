package com.example.demo.src.post.model;

import com.example.demo.src.postImage.model.PostPostImageReq;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PostPostsReq {
    private String contents;
    private int placeIdx;
    private String commentOption;
    private List<PostPostImageReq> postImage = new ArrayList<>();
}
