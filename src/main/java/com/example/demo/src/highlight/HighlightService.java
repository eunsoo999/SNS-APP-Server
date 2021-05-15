package com.example.demo.src.highlight;

import com.example.demo.config.BaseException;
import com.example.demo.src.highlight.model.GetHighlightRes;
import com.example.demo.src.highlight.model.Highlight;
import com.example.demo.src.highlight.model.PatchHighlightReq;
import com.example.demo.src.highlight.model.PostHighlightReq;
import com.example.demo.src.highlightStory.HighlightStoryDao;
import com.example.demo.src.highlightStory.HighlightStoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
@Transactional(rollbackOn = BaseException.class)
public class HighlightService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final HighlightDao highlightDao;
    private final HighlightStoryDao highlightStoryDao;

    @Autowired
    public HighlightService(HighlightDao highlightDao, HighlightStoryDao highlightStoryDao) {
        this.highlightDao = highlightDao;
        this.highlightStoryDao = highlightStoryDao;
    }

    public int createHighlight(PostHighlightReq postHighlightReq, int loginIdx) throws BaseException {
        try {
            int highlightIdx = highlightDao.createHighlight(postHighlightReq, loginIdx); // 폴더 생성

            //폴더를 생성하면서 선택한 스토리를 HighlightStory에 추가
            for (int selectedStoryIdx : postHighlightReq.getSelectedStorys()) {
                highlightStoryDao.createHighlightStory(selectedStoryIdx, highlightIdx);
            }
            return highlightIdx;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int updateHighlights(int highlightIdx, PatchHighlightReq patchHighlightReq, int loginIdx) throws BaseException {
        // 하이라이트 존재 검증
        if (highlightDao.checkHighlightByIdx(highlightIdx) == 0) {
            throw new BaseException(HIGHLIGHT_NOT_EXISTS);
        }
        // 로그인유저와 작성자가 맞는지 검증
        if (highlightDao.checkHighlightOwner(highlightIdx, loginIdx) == 0) {
            throw new BaseException(INVALID_USER_JWT);
        }

        try {
            //하이라이트 정보 수정
            highlightDao.updateHighlights(patchHighlightReq, highlightIdx);
            //하이라이트 내부 스토리 삭제처리
            highlightStoryDao.updateHighlightStoryStatus(highlightIdx);
            //하이라이트 추가/수정한 스토리 저장
            for (int selectedStoryIdx : patchHighlightReq.getSelectedStorys()) {
                highlightStoryDao.createHighlightStory(selectedStoryIdx, highlightIdx);
            }
            return highlightIdx;
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int updateHighlightsStatus(int highlightIdx, int loginIdx) throws BaseException {
        // 하이라이트 존재 검증
        if (highlightDao.checkHighlightByIdx(highlightIdx) == 0) {
            throw new BaseException(HIGHLIGHT_NOT_EXISTS);
        }
        // 로그인유저와 작성자가 맞는지 검증
        if (highlightDao.checkHighlightOwner(highlightIdx, loginIdx) == 0) {
            throw new BaseException(INVALID_USER_JWT);
        }
        try {
            //하이라이트 내부 스토리 삭제처리
            highlightStoryDao.updateHighlightStoryStatus(highlightIdx);
            highlightDao.updateHighlightsStatus(highlightIdx);
            return highlightIdx;
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
