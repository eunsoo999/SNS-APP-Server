package com.example.demo.src.highlight.model;

import com.example.demo.src.highlightStory.model.PostHighlightStoryReq;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostHighlightReq {
    private String thumbnailUrl;
    private String title;
    private List<PostHighlightStoryReq> highlightStorys = new ArrayList<>();
}
