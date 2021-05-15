package com.example.demo.src.room;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.room.model.GetChatsResponse;
import com.example.demo.src.room.model.GetRoomsResponse;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rooms")
public class RoomController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final RoomService roomService;
    @Autowired
    private final RoomProvider roomProvider;
    @Autowired
    private final JwtService jwtService;

    public RoomController(RoomService roomService, RoomProvider roomProvider, JwtService jwtService) {
        this.roomService = roomService;
        this.roomProvider = roomProvider;
        this.jwtService = jwtService;
    }

    /**
     * 채팅방 목록 조회 API
     * [GET] /rooms
     * @return BaseResponse<List<GetRoomsResponse>>
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetRoomsResponse>> getRooms() {
        try {
            int loginIdx = jwtService.getUserIdx();
            List<GetRoomsResponse> getRoomsResponses = roomProvider.retrieveRooms(loginIdx);
            return new BaseResponse<>(getRoomsResponses);
        } catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 채팅 조회 API
     * [GET] /rooms/:roomsIdx
     * @return
     */
    @ResponseBody
    @GetMapping("/{roomIdx}")
    public BaseResponse<GetChatsResponse> getRoomChats(@PathVariable int roomIdx) {
        try {
            int loginIdx = jwtService.getUserIdx();
            GetChatsResponse getChatsResponses = roomProvider.retrieveRoomChats(loginIdx, roomIdx);
            return new BaseResponse<>(getChatsResponses);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

}
