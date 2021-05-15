package com.example.demo.src.highlight.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
public class Highlight {
    private Integer idx;
    private Integer useridx;
    private String title;
    private String thumbnailUrl;
    private Timestamp createdat;
    private Timestamp updatedat;
    private String status; // 상태 Y : 활성, N : 삭제
}
