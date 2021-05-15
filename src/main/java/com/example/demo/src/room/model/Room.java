package com.example.demo.src.room.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
public class Room {
    private int idx;
    private String title;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String status;
}