package com.example.demo.src.highlight;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.highlight.model.PatchHighlightReq;
import com.example.demo.src.highlight.model.PatchHighlightRes;
import com.example.demo.src.highlight.model.PostHighlightReq;
import com.example.demo.src.highlight.model.PostHighlightRes;
import com.example.demo.src.highlightStory.HighlightStoryProvider;
import com.example.demo.src.highlightStory.HighlightStoryService;
import com.example.demo.src.highlightStory.model.GetHighlightStoryRes;
import com.example.demo.src.highlightStory.model.PostHighlightStoryReq;
import com.example.demo.utils.JwtService;
import io.jsonwebtoken.Jwt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.POST_HIGHLIGHT_EMPTY_STORY;
import static com.example.demo.config.BaseResponseStatus.POST_HIGHLIGHT_EMPTY_THUMBNAIL;

@RestController
@RequestMapping("/highlights")
public class HighlightController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final HighlightProvider highlightProvider;
    @Autowired
    private final HighlightService highlightService;
    @Autowired
    private final HighlightStoryProvider highlightStoryProvider;
    @Autowired
    private final HighlightStoryService highlightStoryService;
    @Autowired
    private final JwtService jwtService;

    public HighlightController(HighlightProvider highlightProvider, HighlightService highlightService, HighlightStoryProvider highlightStoryProvider, HighlightStoryService highlightStoryService, JwtService jwtService) {
        this.highlightProvider = highlightProvider;
        this.highlightService = highlightService;
        this.highlightStoryProvider = highlightStoryProvider;
        this.highlightStoryService = highlightStoryService;
        this.jwtService = jwtService;
    }

    /**
     * 하이라이트 폴더 생성 API
     * [POST] /highlights
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostHighlightRes> postHighlights(@RequestBody PostHighlightReq postHighlightReq) {
        // 이미지 검증
        if (postHighlightReq.getThumbnailUrl() == null) {
            return new BaseResponse<>(POST_HIGHLIGHT_EMPTY_THUMBNAIL);
        }
        //삽입할 스토리 검증
        if (postHighlightReq.getSelectedStorys().isEmpty()) {
            return new BaseResponse<>(POST_HIGHLIGHT_EMPTY_STORY);
        }

        try {
            int userIdxByJwt = jwtService.getUserIdx();
            int savedHighlightIdx = highlightService.createHighlight(postHighlightReq, userIdxByJwt);
            PostHighlightRes result = new PostHighlightRes(savedHighlightIdx);
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 하이라이트 폴더 내부 조회 API
     * [GET] /highlights/:highlightIdx
     * @return BaseResponse<List<GetHighlightStoryRes>>
     */
    @ResponseBody
    @GetMapping("/{highlightIdx}")
    public BaseResponse<List<GetHighlightStoryRes>> getHighlightStorys(@PathVariable int highlightIdx) {
        try {
            List<GetHighlightStoryRes> getHighlightStoryResList = highlightProvider.getHighlightStorys(highlightIdx);
            return new BaseResponse<>(getHighlightStoryResList);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 하이라이트 폴더 수정 API
     * [PATCH] /highlights/:highlightIdx
     */
    @ResponseBody
    @PatchMapping("/{highlightIdx}")
    public BaseResponse<String> patchHighlights(@PathVariable int highlightIdx, @RequestBody PatchHighlightReq patchHighlightReq) {
        try {
            int userIdxByJwt = jwtService.getUserIdx();
            highlightService.updateHighlights(highlightIdx, patchHighlightReq, userIdxByJwt);
            return new BaseResponse<>("");
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 하이라이트 폴더 삭제 API
     * [PATCH] /highlights/:highlightIdx/status
     */
    @ResponseBody
    @PatchMapping("/{highlightIdx}/status")
    public BaseResponse<String> patchHighlightsStatus(@PathVariable int highlightIdx) {
        try {
            int userIdxByJwt = jwtService.getUserIdx();
            int deletedHighlightIdx =  highlightService.updateHighlightsStatus(highlightIdx, userIdxByJwt);
            return new BaseResponse<>("");
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
