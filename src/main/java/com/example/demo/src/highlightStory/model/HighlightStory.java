package com.example.demo.src.highlightStory.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
public class HighlightStory {
    private int idx;
    private int highlightIdx;
    private int storyIdx;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String status;
}
