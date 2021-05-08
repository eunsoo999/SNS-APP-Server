package com.example.demo.src.highlight.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetHighlightRes {
    private int highlightIdx;
    private String thumbnailUrl;
    private String title;
}
