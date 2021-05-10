package com.example.demo.src.follow;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.follow.model.PostFollowReq;
import com.example.demo.src.follow.model.PostFollowRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/follows")
public class FollowController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final FollowService followService;
    @Autowired
    private final FollowProvider followProvider;

    public FollowController(FollowService followService, FollowProvider followProvider){
        this.followService = followService;
        this.followProvider = followProvider;
    }

    /**
     * 팔로우 신청 API
     * [POST] /follows
     * @return
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostFollowRes> postFollows(@RequestBody PostFollowReq postFollowReq) {
        if (!postFollowReq.getFollowingUserStatus().equals("PUBLIC")
                && !postFollowReq.getFollowingUserStatus().equals("PRIVATE")) {
            return new BaseResponse<>(INVALID_USERS_STATUS);
        }

        int loginIdx = 1; //todo 로그인 유저 수정
        try{
            PostFollowRes postFollowRes = followService.createFollow(postFollowReq, loginIdx);
            return new BaseResponse<>(postFollowRes);
        }
        catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 팔로우 취소 API
     * [PATCH] /follows/:followIdx
     */
    @ResponseBody
    @PatchMapping("/{followIdx}/status")
    public BaseResponse<String> deleteFollow(@PathVariable int followIdx) {
        try {
            followService.deleteFollow(followIdx);
            String result = "정상적으로 취소되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

}
