package com.example.demo.src.highlight;

import com.example.demo.config.BaseException;
import com.example.demo.src.highlight.model.GetHighlightRes;
import com.example.demo.src.highlightStory.model.GetHighlightStoryRes;
import com.example.demo.src.user.UserProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class HighlightProvider {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final HighlightDao highlightDao;
    private final UserProvider userProvider;

    @Autowired
    public HighlightProvider(HighlightDao highlightDao, UserProvider userProvider) {
        this.highlightDao = highlightDao;
        this.userProvider = userProvider;
    }

    //하이라이트 폴더 조회
    public List<GetHighlightRes> getHighlights(int userIdx) throws BaseException{
        if (userProvider.checkUserIdx(userIdx) == 0) {
            throw new BaseException(USERS_IDX_NOT_EXISTS);
        }
        try{
            List<GetHighlightRes> getHighlights = highlightDao.getHighlights(userIdx);
            return getHighlights;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //하이라이트 폴더 컨텐츠 조회
    public List<GetHighlightStoryRes> getHighlightStorys (int highlightIdx) throws BaseException{
        if (highlightDao.checkHighlightByIdx(highlightIdx) == 0) {
            throw new BaseException(HIGHLIGHT_NOT_EXISTS);
        }
        try{
            List<GetHighlightStoryRes> getHighlightStorys = highlightDao.getHighlightStorys(highlightIdx);
            return getHighlightStorys;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
