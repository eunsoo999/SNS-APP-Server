package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class User {
    private int idx;
    private String userId;
    private String name;
    private String password;
    private String profileUrl;
    private String websiteUrl;
    private String introduction;
    private String email;
    private String phone;
    private LocalDate birth;
    private String gender;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String status;
}
