package com.example.demo.src.story.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetUserStorysRes {
    private String userId;
    private String profileUrl;
    private String videoUrl;
    private String storyTimeStemp;
}
