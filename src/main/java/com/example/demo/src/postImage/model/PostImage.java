package com.example.demo.src.postImage.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
public class PostImage {
    private int idx;
    private int postIdx;
    private String imageType;
    private String imageUrl;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
