package com.example.demo.src.story;

import com.example.demo.config.BaseException;
import com.example.demo.src.story.model.GetStorysRes;
import com.example.demo.src.story.model.GetUserStorysRes;
import com.example.demo.src.user.UserProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;
import static com.example.demo.config.BaseResponseStatus.USERS_IDX_NOT_EXISTS;

@Service
public class StoryProvider {

    private final StoryDao storyDao;
    private final UserProvider userProvider;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public StoryProvider(StoryDao storyDao, UserProvider userProvider) {
        this.storyDao = storyDao;
        this.userProvider = userProvider;
    }

    // 메인 인스타스토리 목록
    public List<GetStorysRes> getStorys(int loginIdx) throws BaseException {
        try {
            List<GetStorysRes> getFollowUserStoryRes = storyDao.getStorys(loginIdx);
            return getFollowUserStoryRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 특정 유저의 인스타스토리
    public List<GetUserStorysRes> getUserStorys(int userIdx)  throws BaseException {
        if (userProvider.checkUserIdx(userIdx) == 0) {
            throw new BaseException(USERS_IDX_NOT_EXISTS);
        }
        try {
            List<GetUserStorysRes> getUserStorysRes = storyDao.getUserStorys(userIdx);
            return getUserStorysRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }




}
