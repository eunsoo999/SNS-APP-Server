package com.example.demo.src.highlight;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.highlight.model.PostHighlightReq;
import com.example.demo.src.highlightStory.HighlightStoryProvider;
import com.example.demo.src.highlightStory.HighlightStoryService;
import com.example.demo.src.highlightStory.model.GetHighlightStoryRes;
import com.example.demo.src.highlightStory.model.PostHighlightStoryReq;
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

    public HighlightController(HighlightProvider highlightProvider, HighlightService highlightService, HighlightStoryProvider highlightStoryProvider, HighlightStoryService highlightStoryService) {
        this.highlightProvider = highlightProvider;
        this.highlightService = highlightService;
        this.highlightStoryProvider = highlightStoryProvider;
        this.highlightStoryService = highlightStoryService;
    }

    /**
     * 하이라이트 폴더 생성 API
     * [POST] /highlights
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<String> postHighlights(@RequestBody PostHighlightReq postHighlightReq) {
        if (postHighlightReq.getThumbnailUrl() == null) {
            return new BaseResponse<>(POST_HIGHLIGHT_EMPTY_THUMBNAIL);
        }
        if (postHighlightReq.getHighlightStorys().isEmpty()) {
            return new BaseResponse<>(POST_HIGHLIGHT_EMPTY_STORY);
        }
        int loginIdx = 1; //todo 로그인유저 수정

        try {
            //폴더 생성
            int savedHighlightIdx = highlightService.createHighlight(postHighlightReq, loginIdx);
            //폴더를 생성하면서 선택한 스토리를 HighlightStory에 추가
            for (PostHighlightStoryReq postHighlightStoryReq : postHighlightReq.getHighlightStorys()) {
                postHighlightStoryReq.setHighlightIdx(savedHighlightIdx);
                highlightStoryService.createHighlightStory(postHighlightStoryReq);
            }
            String result = "";
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

    //하이라이트 스토리 개수.
}
