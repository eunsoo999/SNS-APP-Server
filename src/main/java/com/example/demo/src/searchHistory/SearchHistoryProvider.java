package com.example.demo.src.searchHistory;

import com.example.demo.config.BaseException;
import com.example.demo.src.searchHistory.model.GetSearchHistoryRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;
import static com.example.demo.config.BaseResponseStatus.NOT_EXISTS_SEARCH_TYPE;

@Service
public class SearchHistoryProvider {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SearchHistoryDao searchHistoryDao;

    @Autowired
    public SearchHistoryProvider(SearchHistoryDao searchHistoryDao) {
        this.searchHistoryDao = searchHistoryDao;
    }

    public List<GetSearchHistoryRes> getSearchHistorys(String type, int loginIdx) throws BaseException{
        List<GetSearchHistoryRes> getSearchHistoryResList = new ArrayList<>();

        try {
            if (type.equalsIgnoreCase("USER")) {
                getSearchHistoryResList = searchHistoryDao.getSearchHistorysOfUser(loginIdx);
            } else if (type.equalsIgnoreCase("TAG")) {
                getSearchHistoryResList = searchHistoryDao.getSearchHistorysOfTag(loginIdx);
            } else if (type.equalsIgnoreCase("PLACE")) {
                getSearchHistoryResList = searchHistoryDao.getSearchHistorysOfPlace(loginIdx);
            } else {
                throw new BaseException(NOT_EXISTS_SEARCH_TYPE);
            }
            return getSearchHistoryResList;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    private List<GetSearchHistoryRes> getSearchHistorysOfUser(int loginIdx) throws BaseException {
        List<GetSearchHistoryRes> getSearchHistoryResList = new ArrayList<>();
        try {
            getSearchHistoryResList = searchHistoryDao.getSearchHistorysOfUser(loginIdx);
            return getSearchHistoryResList;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    private List<GetSearchHistoryRes> getSearchHistorysOfTag(int loginIdx) throws BaseException {
        List<GetSearchHistoryRes> getSearchHistoryResList = new ArrayList<>();
        try {
            getSearchHistoryResList = searchHistoryDao.getSearchHistorysOfTag(loginIdx);
            return getSearchHistoryResList;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    private List<GetSearchHistoryRes> getSearchHistorysOfPlace(int loginIdx) throws BaseException {
        List<GetSearchHistoryRes> getSearchHistoryResList = new ArrayList<>();
        try {
            getSearchHistoryResList = searchHistoryDao.getSearchHistorysOfPlace(loginIdx);
            return getSearchHistoryResList;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


    public int checkSearchHistoryIdx(int searchHistoryIdx) {
        return searchHistoryDao.checkSearchHistoryIdx(searchHistoryIdx);
    }
}
