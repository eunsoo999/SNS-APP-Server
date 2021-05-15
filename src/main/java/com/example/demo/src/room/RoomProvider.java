package com.example.demo.src.room;

import com.example.demo.config.BaseException;
import com.example.demo.src.chat.ChatDao;
import com.example.demo.src.chat.model.GetChats;
import com.example.demo.src.room.model.GetChatsResponse;
import com.example.demo.src.room.model.GetRoomImages;
import com.example.demo.src.room.model.GetRoomsResponse;
import com.example.demo.src.room.model.Room;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class RoomProvider {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final RoomDao roomDao;
    private final RoomService roomService;
    private final ChatDao chatDao;

    @Autowired
    public RoomProvider(RoomDao roomDao, RoomService roomService, ChatDao chatDao) {
        this.roomDao = roomDao;
        this.roomService = roomService;
        this.chatDao = chatDao;
    }

    public List<GetRoomsResponse> retrieveRooms(int loginIdx) throws BaseException {
        try {
            List<GetRoomsResponse> getRoomsResponses = roomDao.selectRooms(loginIdx);
            for (GetRoomsResponse rooms : getRoomsResponses) {
                List<GetRoomImages> getRoomImages = roomDao.selectRoomImageUrl(loginIdx, rooms.getRoomIdx());
                rooms.setUsersImageUrl(getRoomImages);
            }
            return getRoomsResponses;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetChatsResponse retrieveRoomChats(int loginIdx, int roomIdx) throws BaseException {
        //roomIdx가 내 채팅방인지.
        if (roomDao.checkRoomsAndMember(roomIdx, loginIdx) != 1) {
            throw new BaseException(ROOMS_NOT_EXISTS);
        }

        try {
            //채팅방 이미지가져오기
            List<GetRoomImages> getRoomImages = roomDao.selectRoomImageUrl(loginIdx, roomIdx);
            // 채팅방 정보
            Room room = roomDao.selectRoom(roomIdx);
            //채팅 전체 가져오기
            List<GetChats> getChats = chatDao.selectChats(roomIdx, loginIdx);
            return new GetChatsResponse(getRoomImages, room.getTitle(), getChats);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }

}
