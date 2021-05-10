package com.example.demo.src.user.model;

import com.example.demo.src.highlight.model.GetHighlightRes;
import com.example.demo.src.post.model.GetPostsRes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetUsersProfileRes {
    private GetUserRes user;
    private List<GetHighlightRes> highlights = new ArrayList<>();
    private List<GetPostsRes> posts = new ArrayList<>();
}
