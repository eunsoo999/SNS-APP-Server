package com.example.demo.src.room.model;

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
public class GetRoomsResponse {
    private int roomIdx;
    private String roomName;
    private String lastChatMessage;
    private String lastChatMessageTimestamp;
    private List<GetRoomImages> usersImageUrl = new ArrayList<>();

    public GetRoomsResponse(int roomIdx, String roomName, String lastChatMessage, String lastChatMessageTimestamp) {
        this.roomIdx = roomIdx;
        this.roomName = roomName;
        this.lastChatMessage = lastChatMessage;
        this.lastChatMessageTimestamp = lastChatMessageTimestamp;
    }
}
