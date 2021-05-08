package com.example.demo.src.searchHistory.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostSearchHistoryReq {
    private String searchType;
    private int targetIdx;
}