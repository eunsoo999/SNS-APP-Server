package com.example.demo.src.highlightStory;

import com.example.demo.config.BaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
@Transactional
public class HighlightStoryService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final HighlightStoryDao highlightStoryDao;

    @Autowired
    public HighlightStoryService(HighlightStoryDao highlightStoryDao) {
        this.highlightStoryDao = highlightStoryDao;
    }

    //하이라이트-스토리 추가
    public int createHighlightStory(int storyIdx, int highlightIdx) throws BaseException {
        try {
            int savedHighlightStoryIdx = highlightStoryDao.createHighlightStory(storyIdx, highlightIdx);
            return savedHighlightStoryIdx;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
