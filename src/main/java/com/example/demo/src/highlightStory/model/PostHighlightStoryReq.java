package com.example.demo.src.highlightStory.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostHighlightStoryReq {
    private int highlightIdx;
    private int storyIdx;
}
