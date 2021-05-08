package com.example.demo.src.highlight.model;

import com.example.demo.src.highlightStory.model.PostHighlightStoryRes;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PostHighlightRes {
    private int highligntIdx;
    private String thumbnailurlUrl;
    private String title;
    private List<PostHighlightStoryRes> highlightStorys = new ArrayList<>();
}
