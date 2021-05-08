package com.example.demo.src.highlightStory;

import com.example.demo.config.BaseException;
import com.example.demo.src.highlightStory.model.PostHighlightStoryReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class HighlightStoryService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final HighlightStoryDao highlightStoryDao;

    @Autowired
    public HighlightStoryService(HighlightStoryDao highlightStoryDao) {
        this.highlightStoryDao = highlightStoryDao;
    }

    //하이라이트-스토리 연결
    public int createHighlightStory(PostHighlightStoryReq postHighlightStoryReq) throws BaseException {
        try {
            int savedHighlightStoryIdx = highlightStoryDao.createHighlightStory(postHighlightStoryReq);
            return savedHighlightStoryIdx;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
