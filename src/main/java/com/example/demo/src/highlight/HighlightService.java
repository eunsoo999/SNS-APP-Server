package com.example.demo.src.highlight;

import com.example.demo.config.BaseException;
import com.example.demo.src.highlight.model.PostHighlightReq;
import com.example.demo.src.highlightStory.HighlightStoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class HighlightService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final HighlightDao highlightDao;
    private final HighlightStoryService highlightStoryService;

    @Autowired
    public HighlightService(HighlightDao highlightDao, HighlightStoryService highlightStoryService) {
        this.highlightDao = highlightDao;
        this.highlightStoryService = highlightStoryService;
    }

    public int createHighlight(PostHighlightReq postHighlightReq, int loginIdx) throws BaseException {
        try {
            int highlightIdx = highlightDao.createHighlight(postHighlightReq, loginIdx); // 폴더 생성
            
            return highlightIdx; //생성된 idx 리턴
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
