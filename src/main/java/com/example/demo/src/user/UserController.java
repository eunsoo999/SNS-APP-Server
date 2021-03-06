package com.example.demo.src.user;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.highlight.HighlightProvider;
import com.example.demo.src.highlight.model.GetHighlightRes;
import com.example.demo.src.post.PostProvider;
import com.example.demo.src.post.model.GetPostsFeedRes;
import com.example.demo.src.post.model.GetPostsRes;
import com.example.demo.src.postImage.PostImageProvider;
import com.example.demo.src.story.StoryProvider;
import com.example.demo.src.story.model.GetUserStorysRes;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.*;

@RestController
@RequestMapping("/users")
public class UserController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final UserProvider userProvider;
    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtService jwtService;
    @Autowired
    private final HighlightProvider highlightProvider;
    @Autowired
    private final PostProvider postProvider;
    @Autowired
    private final PostImageProvider postImageProvider;
    @Autowired
    private final StoryProvider storyProvider;

    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService, HighlightProvider highlightProvider, PostProvider postProvider, PostImageProvider postImageProvider, StoryProvider storyProvider) {
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
        this.highlightProvider = highlightProvider;
        this.postProvider = postProvider;
        this.postImageProvider = postImageProvider;
        this.storyProvider = storyProvider;
    }

    /**
     * ????????? ?????? ??????
     * [GET] /check/email?query=
     * @return BaseResponse<String>
     */
    @ResponseBody
    @GetMapping("/check/email")
    public BaseResponse<String> checkUsersEmail(@RequestParam String query) {
        if (query == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        try{
            int checkEmail = userProvider.checkEmail(query);
            if (checkEmail == 1) {
                return new BaseResponse<>(POST_USERS_EXISTS_EMAIL);
            }
            String result = "";
            return new BaseResponse<>(result);
        } catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * ???????????? ?????? ??????
     * [GET] /check/phone?query=
     * @return BaseResponse<String>
     */
    @ResponseBody
    @GetMapping("/check/phone")
    public BaseResponse<String> checkUsersPhone(@RequestParam String query) {
        if (query == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        try{
            int checkEmail = userProvider.checkPhone(query);
            if (checkEmail == 1) {
                return new BaseResponse<>(POST_USERS_EXISTS_PHONE);
            }
            String result = "";
            return new BaseResponse<>(result);
        } catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * ???????????? API
     * [POST] /users
     * @return BaseResponse<PostUserRes>
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostSignUpRes> createUser(@RequestBody PostSignUpReq postUserReq) {
        //?????? ??????
        if(postUserReq.getUserId() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_ID);
        }
        if (postUserReq.getPassword() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_PW);
        }

        //???????????? ????????????
        if (!isRegexPassword(postUserReq.getPassword())){
            return new BaseResponse<>(POST_USERS_INVALID_PASSWORD);
        }

        //????????????, ????????? ?????? ?????? (??? ??? ????????? ???????????? OK)
        if (postUserReq.getPhone() == null && postUserReq.getEmail() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_PHONE_EMAIL);
        }

        //????????? ?????? ??? ????????????
        if (postUserReq.getEmail() != null && !isRegexEmail(postUserReq.getEmail())) {
            //???????????? ????????????????????? ?????????????????? ?????? ?????? ??????
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }

        //???????????? ?????? ??? ????????????
        if (postUserReq.getPhone() != null && !isRegexPhone(postUserReq.getPhone())) {
            //??????????????? ????????????????????? ?????????????????? ?????? ?????? ??????
            return new BaseResponse<>(POST_USERS_INVALID_PHONE);
        }

        try{
            PostSignUpRes postUserRes;
            if (postUserReq.getEmail() != null) {
                postUserRes = userService.createUserByEmail(postUserReq);
            } else {
                postUserRes = userService.createUserByPhone(postUserReq);
            }
            return new BaseResponse<>(postUserRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * ????????? API
     * [POST] /users/logIn
     * @return BaseResponse<PostLoginRes>
     */
    @ResponseBody
    @PostMapping("/logIn")
    public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq){
        try{
            if (postLoginReq.getUserId() == null) {
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }
            if (postLoginReq.getPassword() == null) {
                return new BaseResponse<>(USERS_EMPTY_USER_PW);
            }

            PostLoginRes postLoginRes = userProvider.logIn(postLoginReq);
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * ?????? ????????? ?????? API
     * [GET] /users/:userId
     * @return BaseResponse<GetUserRes>
     */
    @ResponseBody
    @GetMapping("/{userIdx}") // (GET) 127.0.0.1:9000/app/users/:userIdx
    public BaseResponse<GetUsersProfileRes> getUser(@PathVariable("userIdx") int userIdx) {
        try{
            GetUserRes getUserRes = userProvider.getUser(userIdx); //????????? ????????? ??????
            List<GetHighlightRes> getHighlightResList = highlightProvider.getHighlights(userIdx); // ????????? ??????????????? ?????? ??????
            List<GetPostsRes> getPostsResList = postProvider.getPostsByUserIdx(userIdx); //????????? ???????????????
            return new BaseResponse<>(new GetUsersProfileRes(getUserRes, getHighlightResList, getPostsResList));
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * ?????? ??????(??????) API
     * [GET] /users?keyword=
     * @return BaseResponse<List<GetSearchUserRes>>
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetSearchUserRes>> getUsers(@RequestParam String keyword) {
        try{
            if(keyword.isEmpty()){
                return new BaseResponse<>(SEARCH_EMPTY_KEYWORD);
            }
            List<GetSearchUserRes> getUsersRes = userProvider.getUsersByKeyword(keyword);
            return new BaseResponse<>(getUsersRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * ?????? ?????? API
     * [GET] /users/recommend
     */
    @ResponseBody
    @GetMapping("/recommend") // (GET) 127.0.0.1:9000/users/:userIdx/following
    public BaseResponse<List<GetRecommendUsersRes>> getRecommendUsers() {
        try{
            int userIdxByJwt = jwtService.getUserIdx();
            List<GetRecommendUsersRes> result = userProvider.retrieveRecommendUsers(userIdxByJwt);
            return new BaseResponse<>(result);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * ?????? ????????? ?????? API
     * [GET] /users/:userIdx/following
     * @return BaseResponse<GetFollowUserRes>
     */
    @ResponseBody
    @GetMapping("/{userIdx}/following") // (GET) 127.0.0.1:9000/users/:userIdx/following
    public BaseResponse<GetFollowUserRes> getFollowingUser(@PathVariable("userIdx") int userIdx) {
        try{
            int loginIdx = jwtService.getUserIdx();
            List<FollowUser> getUserRes = userProvider.retrieveFollowingList(loginIdx, userIdx);
            List<GetRecommendUsersRes> getRecommendUsersList = userProvider.retrieveRecommendUsers(loginIdx);

            String commonFollowCount = userProvider.getCommonFollowCount(loginIdx, userIdx);
            String followerCount = userProvider.getFollowerCount(userIdx);
            String followingCount = userProvider.getFollowingCount(userIdx);
            GetFollowUserRes result = new GetFollowUserRes(commonFollowCount, followerCount, followingCount, getUserRes, getRecommendUsersList);
            return new BaseResponse<>(result);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * ?????? ????????? ?????? API
     * [GET] /users/:userIdx/follower
     * @return BaseResponse<List<GetFollowUserRes>>
     */
    @ResponseBody
    @GetMapping("/{userIdx}/follower") // (GET) 127.0.0.1:9000/app/users/:userIdx
    public BaseResponse<GetFollowUserRes> getFollowerUser(@PathVariable("userIdx") int userIdx) {
        try{
            int loginIdx = jwtService.getUserIdx();
            GetFollowUserRes result = userProvider.getFollowerUser(userIdx, loginIdx);
            return new BaseResponse<>(result);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * ?????? ?????? ?????? ?????? API
     * [GET] /users/:userIdx/common
     * @return BaseResponse<GetFollowUserRes>
     */
    @ResponseBody
    @GetMapping("/{userIdx}/common") // (GET) 127.0.0.1:9000/users/:userIdx/common
    public BaseResponse<GetCommonUserRes> getCommonUser(@PathVariable("userIdx") int userIdx) {
        try{
            int loginIdx = jwtService.getUserIdx();
            List<FollowUser> getUserRes = userProvider.retrieveCommonList(loginIdx, userIdx);
            List<GetRecommendUsersRes> getRecommendUsersList = userProvider.retrieveRecommendUsers(loginIdx);
            String commonFollowCount = userProvider.getCommonFollowCount(loginIdx, userIdx);
            String followerCount = userProvider.getFollowerCount(userIdx);
            String followingCount = userProvider.getFollowingCount(userIdx);
            GetCommonUserRes result = new GetCommonUserRes(commonFollowCount, followerCount, followingCount, getUserRes, getRecommendUsersList);
            return new BaseResponse<>(result);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * ?????? ????????? ????????? ?????? API
     * [GET] /users/:userIdx/posts/feed
     * @return BaseResponse<List<GetPostRes>>
     */
    @ResponseBody
    @GetMapping("/{userIdx}/posts/feed")
    public BaseResponse<List<GetPostsFeedRes>> getUsersPostsFeed(@PathVariable int userIdx) {
        try {
            int loginIdx = jwtService.getUserIdx();
            List<GetPostsFeedRes> getPostList = postProvider.getPostsFeedByUserIdx(userIdx, loginIdx);
            return new BaseResponse<>(getPostList);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
        //            List<GetPostsFeedRes> getPostList = postProvider.getPostsFeedByUserIdx(userIdx);
//            for (GetPostsFeedRes getPostRes : getPostList) {
//                int postIdx = getPostRes.getPostIdx();
//                List<GetPostImagesRes> getPostImagesResList = postImageProvider.getPostImage(postIdx);
//                getPostRes.setPostImages(getPostImagesResList);
//            }
    }

    /**
     * ?????? ????????? ????????? ?????? API
     * [GET] /users/:userIdx/storys
     * @return BaseResponse<List<GetUserStorysRes>>
     */
    @ResponseBody
    @GetMapping("/{userIdx}/storys")
    public BaseResponse<List<GetUserStorysRes>> getUserStorys(@PathVariable("userIdx") int userIdx) {
        try{
            List<GetUserStorysRes> getUserRes = storyProvider.getUserStorys(userIdx);
            return new BaseResponse<>(getUserRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

//    /**
//     * ?????? ????????? ??????????????? ?????? ?????? API
//     * [GET] /users/:userIdx/highlights
//     * @return BaseResponse<List<GetHighlightRes>>
//     */
//    @ResponseBody
//    @GetMapping("/{userIdx}/highlights")
//    public BaseResponse<List<GetHighlightRes>> getHighlights(@PathVariable("userIdx") int userIdx) {
//        try{
//            List<GetHighlightRes> getHighlightRes = highlightProvider.getHighlights(userIdx);
//            return new BaseResponse<>(getHighlightRes);
//        }
//        catch(BaseException exception){
//            return new BaseResponse<>((exception.getStatus()));
//        }
//    }

    /**
     * ??????????????? ?????? API
     * [PATCH] /users/:userIdx
     * @return BaseResponse<String>
     * - ?????? ????????? ?????? ????????? ????????? ??????????????? ????????????.
     * ?????? jwt????????? ?????? ????????? ??? ????????? ?????? ????????? ??? ??????!
     * ????????? ?????? ????????? ????????????.
     */
    @ResponseBody
    @PatchMapping("/{userIdx}")
    public BaseResponse<String> modifyUserProfile(@PathVariable("userIdx") int userIdx, @RequestBody PatchUserReq patchUserReq){
        try {
            //jwt?????? idx ??????.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx??? ????????? ????????? ????????? ??????
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            //????????? ?????? ??????
            userService.updateUserProfile(patchUserReq, userIdx);

            String result = "";
        return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * ?????? ?????? API
     * [PATCH] /users/:userIdx/status
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/{userIdx}/status")
    public BaseResponse<String> updateUserStatus(@PathVariable("userIdx") int userIdx){
        try {
            //jwt?????? idx ??????.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx??? ????????? ????????? ????????? ??????
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            userService.updateUserStatus(userIdx);

            String result = "";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

//    /**
//     * ?????? ????????? ?????? API
//     * [GET] /users/myfollowing
//     * @return BaseResponse<GetFollowUserRes>
//     */
//    @ResponseBody
//    @GetMapping("/myfollowing") // (GET) 127.0.0.1:9000/users/:userIdx/following
//    public BaseResponse<GetFollowUserRes> getmyFollowingUser(@PathVariable("userIdx") int userIdx, @RequestParam(required = false) String sort) {
//        int loginIdx = 1; //todo ????????? ?????? ??????
//
//        if (sort != null && (!sort.equalsIgnoreCase("followingdate_asc") && !sort.equalsIgnoreCase("followingdate_desc"))) {
//            return new BaseResponse<>(TEST_ERROR);
//        }
//
//        try{
//            List<FollowUser> getUserRes = userProvider.retrieveFollowingList(loginIdx, userIdx, sort);
//            String commonFollowCount = userProvider.getCommonFollowCount(loginIdx, userIdx);
//            String followerCount = userProvider.getFollowerCount(userIdx);
//            String followingCount = userProvider.getFollowingCount(userIdx);
//            GetFollowUserRes result = new GetFollowUserRes(commonFollowCount, followerCount, followingCount, getUserRes);
//            return new BaseResponse<>(result);
//        } catch(BaseException exception){
//            return new BaseResponse<>((exception.getStatus()));
//        }
//    }
}

//    /**
//     * ?????? ????????? ?????? API
//     * [GET] /users/:userIdx/following
//     * @return BaseResponse<GetFollowUserRes>
//     */
//    @ResponseBody
//    @GetMapping("/{userIdx}/following") // (GET) 127.0.0.1:9000/users/:userIdx/following
//    public BaseResponse<GetFollowUserRes> getFollowingUser(@PathVariable("userIdx") int userIdx, @RequestParam(required = false) String sort) {
//        int loginIdx = 1; //todo ????????? ?????? ??????
//
//        if (sort != null && (!sort.equalsIgnoreCase("followingdate_asc") && !sort.equalsIgnoreCase("followingdate_desc"))) {
//            return new BaseResponse<>(TEST_ERROR);
//        }
//
//        try{
//            List<FollowUser> getUserRes;
//            //todo ?????? ?????? userIdx??? ?????????? -> ????????? ?????? ????????????
//            if (userIdx == 1) {
//                getUserRes = userProvider.retrieveMyFollowingList(sort);
//            } else {
//                getUserRes = userProvider.retrieveFollowingList(loginIdx, userIdx);
//            }
//
//            String commonFollowCount = userProvider.getCommonFollowCount(loginIdx, userIdx);
//            String followerCount = userProvider.getFollowerCount(userIdx);
//            String followingCount = userProvider.getFollowingCount(userIdx);
//            GetFollowUserRes result = new GetFollowUserRes(commonFollowCount, followerCount, followingCount, getUserRes);
//            return new BaseResponse<>(result);
//        } catch(BaseException exception){
//            return new BaseResponse<>((exception.getStatus()));
//        }
//

