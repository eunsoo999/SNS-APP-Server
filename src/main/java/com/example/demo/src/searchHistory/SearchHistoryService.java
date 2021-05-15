package com.example.demo.src.searchHistory;

import com.example.demo.config.BaseException;
import com.example.demo.src.searchHistory.model.PostSearchHistoryReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;
import static com.example.demo.config.BaseResponseStatus.DELETE_SEARCH_HISTORY_NOT_EXISTS;

@Service
public class SearchHistoryService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SearchHistoryDao searchHistoryDao;
    private final SearchHistoryProvider searchHistoryProvider;

    @Autowired
    public SearchHistoryService(SearchHistoryDao searchHistoryDao, SearchHistoryProvider searchHistoryProvider) {
        this.searchHistoryDao = searchHistoryDao;
        this.searchHistoryProvider = searchHistoryProvider;
    }

    // 검색기록 추가
    public int createSearchHistory(PostSearchHistoryReq postSearchHistoryReq, int loginIdx) throws BaseException {
        try {
            return searchHistoryDao.createSearchHistory(postSearchHistoryReq,loginIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 검색기록 수정(검색기록을 선택해서 상위로 올리기위한 수정)
    public int updateSearchHistory(int searchHistoryIdx) throws BaseException {
        if (searchHistoryDao.checkSearchHistoryIdx(searchHistoryIdx) == 0) {
            throw new BaseException(DELETE_SEARCH_HISTORY_NOT_EXISTS);
        }
        try {
            return searchHistoryDao.updateSearchHistory(searchHistoryIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void updateSearchHistorysStatus(int searchHistoryIdx) throws BaseException {
        // 검색기록이 존재하는지
        if (searchHistoryProvider.checkSearchHistoryIdx(searchHistoryIdx) == 0) {
            throw new BaseException(DELETE_SEARCH_HISTORY_NOT_EXISTS);
        }
        //todo 회원이 검색한 기록인지

        try {
            searchHistoryDao.updateSearchHistorysStatus(searchHistoryIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void updateAllSearchHistorysStatus(int loginIdx) throws BaseException {
        try {
            searchHistoryDao.updateAllSearchHistorysStatusByUserIdx(loginIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
