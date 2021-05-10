package com.example.demo.src.user;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.highlight.HighlightProvider;
import com.example.demo.src.highlight.model.GetHighlightRes;
import com.example.demo.src.post.PostProvider;
import com.example.demo.src.post.model.GetPostsFeedRes;
import com.example.demo.src.post.model.GetPostsRes;
import com.example.demo.src.postImage.PostImageProvider;
import com.example.demo.src.postImage.model.GetPostImagesRes;
import com.example.demo.src.story.StoryProvider;
import com.example.demo.src.story.model.GetStorysRes;
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
     * 이메일 중복 확인
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
     * 전화번호 중복 확인
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
     * 회원가입 API
     * [POST] /users
     * @return BaseResponse<PostUserRes>
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostUserRes> createUser(@RequestBody PostUserReq postUserReq) {
        //입력 확인
        if(postUserReq.getUserId() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_ID);
        }
        if (postUserReq.getPassword() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_PW);
        }

        //패스워드 정규표현
        if (!isRegexPassword(postUserReq.getPassword())){
            return new BaseResponse<>(POST_USERS_INVALID_PASSWORD);
        }

        //전화번호, 이메일 입력 확인 (둘 중 하나만 입력하면 OK)
        if (postUserReq.getPhone() == null && postUserReq.getEmail() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_PHONE_EMAIL);
        }

        //이메일 입력 시 정규표현
        if (postUserReq.getEmail() != null && !isRegexEmail(postUserReq.getEmail())) {
            //이메일이 입력되어있지만 정규표현식에 맞지 않을 경우
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }

        //전화번호 입력 시 정규표현
        if (postUserReq.getPhone() != null && !isRegexPhone(postUserReq.getPhone())) {
            //전화번호가 입력되어있지만 정규표현식에 맞지 않을 경우
            return new BaseResponse<>(POST_USERS_INVALID_PHONE);
        }

        try{
            PostUserRes postUserRes;
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
     * 로그인 API
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
     * 유저 프로필 조회 API
     * [GET] /users/:userId
     * @return BaseResponse<GetUserRes>
     */
    @ResponseBody
    @GetMapping("/{userIdx}") // (GET) 127.0.0.1:9000/app/users/:userIdx
    public BaseResponse<GetUsersProfileRes> getUser(@PathVariable("userIdx") int userIdx) {
        try{
            GetUserRes getUserRes = userProvider.getUser(userIdx); //유저의 프로필 조회
            List<GetPostsRes> getPostsResList = postProvider.getPostsByUserIdx(userIdx); //유저의 게시물조회
            List<GetHighlightRes> getHighlightResList = highlightProvider.getHighlights(userIdx); // 유저의 하이라이트 폴더 조회
            return new BaseResponse<>(new GetUsersProfileRes(getUserRes, getHighlightResList, getPostsResList));
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 유저 조회(검색) API
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
     * 유저 추천 API
     * [GET] /users/recommend
     */
    @ResponseBody
    @GetMapping("/recommend") // (GET) 127.0.0.1:9000/users/:userIdx/following
    public BaseResponse<List<GetRecommendUsersRes>> getRecommendUsers() {
        int loginIdx = 1; //todo 로그인 유저 수정

        try{
            List<GetRecommendUsersRes> result = userProvider.retrieveRecommendUsers(loginIdx);
            return new BaseResponse<>(result);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /**
     * 유저 팔로잉 조회 API
     * [GET] /users/:userIdx/following
     * @return BaseResponse<GetFollowUserRes>
     */
    @ResponseBody
    @GetMapping("/{userIdx}/following") // (GET) 127.0.0.1:9000/users/:userIdx/following
    public BaseResponse<GetFollowUserRes> getFollowingUser(@PathVariable("userIdx") int userIdx) {
        int loginIdx = 1; //todo 로그인 유저 수정

        try{
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
     * 유저 팔로워 조회 API
     * [GET] /users/:userIdx/follower
     * @return BaseResponse<List<GetFollowUserRes>>
     */
    @ResponseBody
    @GetMapping("/{userIdx}/follower") // (GET) 127.0.0.1:9000/app/users/:userIdx
    public BaseResponse<GetFollowUserRes> getFollowerUser(@PathVariable("userIdx") int userIdx) {
        int loginIdx = 1; //로그인 유저idx
        //todo 로그인 유저 수정
        try{
            List<FollowUser> getUserRes = userProvider.retrieveFollowerList(loginIdx, userIdx);
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
     * 함께 아는 친구 조회 API
     * [GET] /users/:userIdx/common
     * @return BaseResponse<GetFollowUserRes>
     */
    @ResponseBody
    @GetMapping("/{userIdx}/common") // (GET) 127.0.0.1:9000/users/:userIdx/common
    public BaseResponse<GetCommonUserRes> getCommonUser(@PathVariable("userIdx") int userIdx) {
        int loginIdx = 1; //로그인 유저idx
        //todo 로그인 유저 수정
        try{
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
     * 특정 유저의 게시글 조회 API
     * [GET] /users/:userIdx/posts/feed
     * @return BaseResponse<List<GetPostRes>>
     */
    @ResponseBody
    @GetMapping("/{userIdx}/posts/feed")
    public BaseResponse<List<GetPostsFeedRes>> getUsersPostsFeed(@PathVariable int userIdx) {
        try {
            List<GetPostsFeedRes> getPostList = postProvider.getPostsFeedByUserIdx(userIdx);
            for (GetPostsFeedRes getPostRes : getPostList) {
                int postIdx = getPostRes.getPostIdx();
                List<GetPostImagesRes> getPostImagesResList = postImageProvider.getPostImage(postIdx);
                getPostRes.setPostImages(getPostImagesResList);
            }
            return new BaseResponse<>(getPostList);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

//    /**
//     * 특정 유저의 게시글 미리보기 API
//     * [GET] /users/:userIdx/posts
//     * @return BaseResponse<List<GetPostRes>>
//     */
//    @ResponseBody
//    @GetMapping("/{userIdx}/posts")
//    public BaseResponse<List<GetPostsRes>> getUsersPosts(@PathVariable int userIdx) {
//        try {
//            List<GetPostsRes> getPostList = postProvider.getPostsByUserIdx(userIdx);
//            return new BaseResponse<>(getPostList);
//        } catch (BaseException exception) {
//            return new BaseResponse<>((exception.getStatus()));
//        }
//    }

    /**
     * 특정 유저의 스토리 조회 API
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
//     * 특정 유저의 하이라이트 폴더 조회 API
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
     * 유저정보변경 API
     * [PATCH] /users/:userIdx
     * @return BaseResponse<String>
     * - 유저 정보는 오직 자신만 변경이 가능하도록 해야한다.
     * 만약 jwt탈취를 해서 실행할 수 있도록 하면 보안상 큰 문제!
     * 이것에 대한 검증이 필요하다.
     */
    @ResponseBody
    @PatchMapping("/{userIdx}")
    public BaseResponse<String> modifyUserName(@PathVariable("userIdx") int userIdx, @RequestBody User user){
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            //같다면 유저네임 변경
            PatchUserReq patchUserReq = new PatchUserReq(userIdx,user.getName());
            userService.modifyUserName(patchUserReq);

            String result = "";
        return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

//    /**
//     * 나의 팔로잉 조회 API
//     * [GET] /users/myfollowing
//     * @return BaseResponse<GetFollowUserRes>
//     */
//    @ResponseBody
//    @GetMapping("/myfollowing") // (GET) 127.0.0.1:9000/users/:userIdx/following
//    public BaseResponse<GetFollowUserRes> getmyFollowingUser(@PathVariable("userIdx") int userIdx, @RequestParam(required = false) String sort) {
//        int loginIdx = 1; //todo 로그인 유저 수정
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
//     * 유저 팔로잉 조회 API
//     * [GET] /users/:userIdx/following
//     * @return BaseResponse<GetFollowUserRes>
//     */
//    @ResponseBody
//    @GetMapping("/{userIdx}/following") // (GET) 127.0.0.1:9000/users/:userIdx/following
//    public BaseResponse<GetFollowUserRes> getFollowingUser(@PathVariable("userIdx") int userIdx, @RequestParam(required = false) String sort) {
//        int loginIdx = 1; //todo 로그인 유저 수정
//
//        if (sort != null && (!sort.equalsIgnoreCase("followingdate_asc") && !sort.equalsIgnoreCase("followingdate_desc"))) {
//            return new BaseResponse<>(TEST_ERROR);
//        }
//
//        try{
//            List<FollowUser> getUserRes;
//            //todo 만약 해당 userIdx가 나라면? -> 정렬이 되는 메소드로
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

