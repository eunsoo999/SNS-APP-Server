package com.example.demo.src.tag.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
public class Tag {
    private int idx;
    private String tagName;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
