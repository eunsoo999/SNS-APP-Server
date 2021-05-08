package com.example.demo.src.searchHistory.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetSearchHistoryRes {
    private int searchHistoryIdx;
    private String searchType;
    private int targetIdx;
    private String imageUrl;
    private String mainTitle;
    private String subTitle;
}
