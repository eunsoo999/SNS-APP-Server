package com.example.demo.src.highlightStory.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetHighlightStoryRes {
    private int highlightStoryIdx;
    private String videoUrl;
    private String storyTimeStamp;
}
