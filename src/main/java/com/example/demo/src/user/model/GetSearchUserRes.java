package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetSearchUserRes {
    private int userIdx;
    private String profileUrl;
    private String userId;
    private String name;
}
