package com.example.demo.src.searchHistory;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.searchHistory.model.GetSearchHistoryRes;
import com.example.demo.src.searchHistory.model.PostSearchHistoryReq;
import com.example.demo.src.searchHistory.model.PostSearchHistoryRes;
import com.example.demo.utils.JwtService;
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
    @Autowired
    private final JwtService jwtService;

    public SearchHistoryController(SearchHistoryProvider searchHistoryProvider, SearchHistoryService searchHistoryService, JwtService jwtService) {
        this.searchHistoryProvider = searchHistoryProvider;
        this.searchHistoryService = searchHistoryService;
        this.jwtService = jwtService;
    }

    /**
     * 최근 검색 기록 조회
     * [GET] /search/history?type=hit
     * [GET] /search/history?type=user
     * [GET] /search/history?type=tag
     * [GET] /search/history?type=place
     */
    @ResponseBody
    @GetMapping()
    public BaseResponse<List<GetSearchHistoryRes>> getSearchHistorys(@RequestParam String type) {
        if (!type.equalsIgnoreCase("USER")
                    && !type.equalsIgnoreCase("TAG")
                    && !type.equalsIgnoreCase("PLACE") && !type.equalsIgnoreCase("HIT")) {
            return new BaseResponse<>(NOT_EXISTS_SEARCH_TYPE);
        }

        try {
            int loginIdx = jwtService.getUserIdx();
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
    public BaseResponse<PostSearchHistoryRes> postSearchHistorys (@RequestBody PostSearchHistoryReq postSearchHistoryReq) {
        try {
            int loginIdx = jwtService.getUserIdx();
            int createdIdx = searchHistoryService.createSearchHistory(postSearchHistoryReq, loginIdx);
            return new BaseResponse<>(new PostSearchHistoryRes(createdIdx));
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
     * 검색 기록 단건 삭제
     */
    @ResponseBody
    @PatchMapping("/{searchHistoryIdx}/status")
    public BaseResponse<String> updateSearchHistorysStatus (@PathVariable int searchHistoryIdx) {
        try {
            searchHistoryService.updateSearchHistorysStatus(searchHistoryIdx);
            return new BaseResponse<>("");
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 검색 기록 전체 삭제
     */
    @ResponseBody
    @PatchMapping("/status")
    public BaseResponse<String> updateAllSearchHistorysStatus () {
        try {
            int loginIdx = jwtService.getUserIdx();
            searchHistoryService.updateAllSearchHistorysStatus(loginIdx);
            return new BaseResponse<>("");
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
