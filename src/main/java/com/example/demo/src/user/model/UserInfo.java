package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserInfo {
    private int userIdx;
    private String userId;
    private String password;
    private String email;
    private String phone;
}
