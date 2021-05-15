package com.example.demo.src.story;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.story.model.GetStorysRes;
import com.example.demo.src.story.model.PostStoryReq;
import com.example.demo.src.story.model.PostStoryRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.POST_STORY_EMPTY_VIDEO_URL;

@RestController
@RequestMapping("/storys")
public class StoryController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final StoryProvider storyProvider;
    @Autowired
    private final StoryService storyService;
    @Autowired
    private final JwtService jwtService;

    public StoryController(StoryProvider storyProvider, StoryService storyService,JwtService jwtService) {
        this.storyProvider = storyProvider;
        this.storyService = storyService;
        this.jwtService = jwtService;
    }

    /**
     * 최신 스토리 목록 조회 API
     * [GET] /storys
     * @return BaseResponse<List<GetStoryRes>>
     */
    @ResponseBody
    @GetMapping("") // (GET) 127.0.0.1:9000/app/storys?useridx=
    public BaseResponse<List<GetStorysRes>> getStorys() {
        try{
            int loginIdx = jwtService.getUserIdx();
            List<GetStorysRes> getStoryRes = storyProvider.getStorys(loginIdx);
            return new BaseResponse<>(getStoryRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 스토리 작성 API
     * [POST] /storys
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostStoryRes> postStorys(@RequestBody PostStoryReq postStoryReq) {
        if (postStoryReq.getVideoUrl() == null) {
            return new BaseResponse<>(POST_STORY_EMPTY_VIDEO_URL);
        }

        try {
            int loginIdx = jwtService.getUserIdx();
            int createdStoryIdx = storyService.createStory(postStoryReq, loginIdx);
            return new BaseResponse<>(new PostStoryRes(createdStoryIdx));
        } catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 스토리 삭제 API
     * [PATCH] /storys/:storyIdx/status
     */
    @ResponseBody
    @PatchMapping("/{storyIdx}/status")
    public BaseResponse<String> patchStorysStatus(@PathVariable int storyIdx) {
        try {
            int loginIdx = jwtService.getUserIdx();
            storyService.updateStoryStatus(storyIdx, loginIdx);
            String result = "";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
