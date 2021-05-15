package com.example.demo.src.highlight.model;

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
public class PatchHighlightReq {
    private String title;
    private String thumbnailUrl;
    private List<Integer> selectedStorys = new ArrayList<>();
}
