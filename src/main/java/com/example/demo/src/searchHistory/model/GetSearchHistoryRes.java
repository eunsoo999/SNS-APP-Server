package com.example.demo.src.searchHistory.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // null 필드는 제외하고 Json형식으로 보낸다.
public class GetSearchHistoryRes {
    //private int searchHistoryIdx;
    private String searchType;
    //private int targetIdx;
    private String imageUrl;
    private String mainTitle;
    private String subTitle;
}
