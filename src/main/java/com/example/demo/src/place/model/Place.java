package com.example.demo.src.place.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
public class Place {
    private int idx;
    private String title;
    private double latitude;
    private double longitude;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
