package com.example.demo.src.post.model;

import com.example.demo.src.story.model.GetStorysRes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetPostsMainFeedRes {
    private List<GetStorysRes> storys = new ArrayList<>();
    private List<GetPostsFeedRes> posts = new ArrayList<>();
}
