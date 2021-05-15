package com.example.demo.src.room.model;

import com.example.demo.src.chat.model.GetChats;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetChatsResponse {
    private List<GetRoomImages> ImageUrls  = new ArrayList<>(); //채팅방 이미지
    private String title;//채팅방이름
    private List<GetChats> getChatList = new ArrayList<>(); // 채팅 
}
