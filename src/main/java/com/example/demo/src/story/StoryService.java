package com.example.demo.src.story;

import com.example.demo.config.BaseException;
import com.example.demo.src.story.model.PostStoryReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class StoryService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final StoryDao storyDao;
    private final StoryProvider storyProvider;

    @Autowired
    public StoryService(StoryDao storyDao, StoryProvider storyProvider) {
        this.storyDao = storyDao;
        this.storyProvider = storyProvider;
    }

    public int createStory(PostStoryReq postStoryReq, int loginIdx) throws BaseException {
        try {
            return storyDao.createStory(postStoryReq, loginIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void updateStoryStatus(int storyIdx, int loginIdx) throws BaseException {
        if (storyDao.checkStoryIdx(storyIdx) == 0) {
            throw new BaseException(DELETE_STORY_NOT_EXISTS);
        }
        if (storyDao.checkStoryOwner(storyIdx, loginIdx) == 0) {
            throw new BaseException(INVALID_USER_JWT);
        }
        try {
            storyDao.updateStoryStatus(storyIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
