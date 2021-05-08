package com.example.demo.src.place.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetSearchPlaceRes {
    private int placeIdx;
    private String title;
    private String address;
}
