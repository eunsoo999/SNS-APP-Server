package com.example.demo.src.story;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.story.model.GetStorysRes;
import com.example.demo.src.story.model.PostStoryReq;
import com.example.demo.src.story.model.PostStoryRes;
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

    public StoryController(StoryProvider storyProvider, StoryService storyService) {
        this.storyProvider = storyProvider;
        this.storyService = storyService;
    }

    /**
     * 최신 스토리 목록 조회 API
     * [GET] /storys
     * @return BaseResponse<List<GetStoryRes>>
     */
    @ResponseBody
    @GetMapping("") // (GET) 127.0.0.1:9000/app/storys?useridx=
    public BaseResponse<List<GetStorysRes>> getStorys() {
        int loginIdx = 1;
        //todo 로그인 유저 수정
        try{
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
        int loginIdx = 1;  //todo 로그인유저 수정
        try {
            int createdStoryIdx = storyService.createStory(postStoryReq, loginIdx);
            return new BaseResponse<>(new PostStoryRes(createdStoryIdx));
        } catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 스토리 삭제 API
     * [DELETE] /storys/:storyIdx
     */
    @ResponseBody
    @DeleteMapping("/{storyIdx}")
    public BaseResponse<String> deleteStorys(@PathVariable int storyIdx) {
        try {
            storyService.deleteStorys(storyIdx);
            String result = "";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


}
