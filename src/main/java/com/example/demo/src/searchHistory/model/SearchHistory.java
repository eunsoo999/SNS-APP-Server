package com.example.demo.src.searchHistory.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
public class SearchHistory {
    private int idx;
    private String searchType; // 검색카테고리 TAG = 태그, USER = 계정, PLACE = 장소
    private int targetIdx;
    private int userIdx;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String status; // 상태 Y : 활성화, N : 삭제
}
