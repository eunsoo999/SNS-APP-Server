package com.example.demo.src.follow;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.follow.model.PostFollowReq;
import com.example.demo.src.follow.model.PostFollowRes;
import com.example.demo.utils.JwtService;
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
    @Autowired
    private final JwtService jwtService;

    public FollowController(FollowService followService, FollowProvider followProvider, JwtService jwtService){
        this.followService = followService;
        this.followProvider = followProvider;
        this.jwtService = jwtService;
    }

    /**
     * 팔로우 신청 API
     * [POST] /follows
     * @return
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostFollowRes> postFollows(@RequestBody PostFollowReq postFollowReq) {
        //계정 공개 상태 Request 검증
        if (!postFollowReq.getFollowingUserStatus().equals("PUBLIC")
                && !postFollowReq.getFollowingUserStatus().equals("PRIVATE")) {
            return new BaseResponse<>(INVALID_USERS_STATUS);
        }

        try{
            int userIdxByJwt = jwtService.getUserIdx();
            PostFollowRes postFollowRes = followService.createFollow(postFollowReq, userIdxByJwt);
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
            int userIdxByJwt = jwtService.getUserIdx();
            followService.deleteFollow(followIdx, userIdxByJwt);
            String result = "정상적으로 취소되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

}
