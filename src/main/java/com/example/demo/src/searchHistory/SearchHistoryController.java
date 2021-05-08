package com.example.demo.src.searchHistory;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.searchHistory.model.GetSearchHistoryRes;
import com.example.demo.src.searchHistory.model.PostSearchHistoryReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.NOT_EXISTS_SEARCH_TYPE;

@RestController
@RequestMapping("/search/historys")
public class SearchHistoryController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private final SearchHistoryProvider searchHistoryProvider;
    @Autowired
    private final SearchHistoryService searchHistoryService;

    public SearchHistoryController(SearchHistoryProvider searchHistoryProvider, SearchHistoryService searchHistoryService) {
        this.searchHistoryProvider = searchHistoryProvider;
        this.searchHistoryService = searchHistoryService;
    }

    /**
     * 최근 검색 기록 조회
     * [GET] /search/history?type=hit || /search/history
     * [GET] /search/history?type=user
     * [GET] /search/history?type=tag
     * [GET] /search/history?type=place
     */
    @ResponseBody
    @GetMapping()
    public BaseResponse<List<GetSearchHistoryRes>> getSearchHistorys(@RequestParam(required = false) String type) {
        if (!type.equalsIgnoreCase("USER")
                    && !type.equalsIgnoreCase("TAG")
                    && !type.equalsIgnoreCase("PLACE")) {
            return new BaseResponse<>(NOT_EXISTS_SEARCH_TYPE);
        }

        int loginIdx = 1; //todo 로그인 유저 수정

        try {
            List<GetSearchHistoryRes> getSearchHistoryResList = searchHistoryProvider.getSearchHistorys(type, loginIdx);
            return new BaseResponse<>(getSearchHistoryResList);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 검색 기록 추가
     * [POST] /search/history
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<String> postSearchHistorys (@RequestBody PostSearchHistoryReq postSearchHistoryReq) {
        int loginIdx = 1; //todo 로그인유저 수정

        try {
            searchHistoryService.createSearchHistory(postSearchHistoryReq, loginIdx);
            String result = "";
            return new BaseResponse<>(result);
        } catch(BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 검색 기록 재 조회 API
     * [PATCH] /search/history/:searchHistoryIdx
     */
    @ResponseBody
    @PatchMapping("/{searchHistoryIdx}")
    public BaseResponse<String> patchSearchHistorys (@PathVariable int searchHistoryIdx) {
        try {
            searchHistoryService.updateSearchHistory(searchHistoryIdx);
            String result = "";
            return new BaseResponse<>(result);
        } catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 검색 기록 삭제
     */
    @ResponseBody
    @DeleteMapping("/{searchHistoryIdx}")
    public BaseResponse<String> deleteSearchHistorys (@PathVariable int searchHistoryIdx) {
        try {
            searchHistoryService.deleteSearchHistory(searchHistoryIdx);
            return new BaseResponse<>("");
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }


    }
}
