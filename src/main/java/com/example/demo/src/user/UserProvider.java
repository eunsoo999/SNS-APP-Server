package com.example.demo.src.user;


import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

//Provider : Read의 비즈니스 로직 처리
@Service
public class UserProvider {
    private final UserDao userDao;
    private final JwtService jwtService;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public UserProvider(UserDao userDao, JwtService jwtService) {
        this.userDao = userDao;
        this.jwtService = jwtService;
    }

    //전체 유저 List
    public List<GetUserRes> getUsers() throws BaseException{
        try{
            List<GetUserRes> getUserRes = userDao.getUsers();
            return getUserRes;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetSearchUserRes> getUsersByKeyword(String keyword) throws BaseException{
        try{
            List<GetSearchUserRes> getUsersRes = userDao.getUsersByKeyword(keyword);
            return getUsersRes;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetUserRes> getUsersByEmail(String email) throws BaseException{
        try{
            List<GetUserRes> getUsersRes = userDao.getUsersByEmail(email);
            return getUsersRes;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //특정 유저 프로필
    public GetUserRes getUser(int userIdx) throws BaseException {
        if (checkUserIdx(userIdx) == 0) {
            throw new BaseException(USERS_ID_NOT_EXISTS);
        }

        try {
            GetUserRes getUserRes = userDao.getUser(userIdx);
            return getUserRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //함께 아는 친구 List
    public List<FollowUser> retrieveCommonList(int loginIdx, int userIdx) throws BaseException{
        if (checkUserIdx(userIdx) == 0) {
            throw new BaseException(USERS_IDX_NOT_EXISTS);
        }
        try{
            return userDao.getCommonFollow(loginIdx, userIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //특정 유저 팔로잉 List
    public List<FollowUser> retrieveFollowingList(int loginIdx, int userIdx) throws BaseException{
        if (checkUserIdx(userIdx) == 0) {
            throw new BaseException(USERS_IDX_NOT_EXISTS);
        }
        try{
            return userDao.getFollowing(loginIdx, userIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //나의 유저 팔로잉 List
    public List<FollowUser> retrieveMyFollowingList(String sort) throws BaseException{
        try{
            int loginIdx = jwtService.getUserIdx();
            return userDao.getMyFollowing(loginIdx, sort);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //특정 유저 팔로워 List
    public List<FollowUser> retrieveFollowerList(int loginIdx, int userIdx) throws BaseException{
        if (checkUserIdx(userIdx) == 0) {
            throw new BaseException(USERS_IDX_NOT_EXISTS);
        }
        try{
            return userDao.getFollower(loginIdx ,userIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //특정 유저 팔로잉 숫자
    public String getFollowingCount(int userIdx) throws BaseException{
        try{
            return userDao.getFollowingCount(userIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //특정 유저 팔로워 숫자
    public String getFollowerCount(int userIdx) throws BaseException{
        try{
            return userDao.getFollowerCount(userIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //

    //함께 아는 친구 Count
    public String getCommonFollowCount(int loginIdx, int userIdx) throws BaseException{
        try{
            return userDao.getCommonFollowCount(loginIdx, userIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public PostLoginRes logIn(PostLoginReq postLoginReq) throws BaseException{
        // TODO: 유저의 status ex) 비활성화된 유저, 탈퇴한 유저 등을 관리해주고 있다면 해당 부분에 대한 validation 처리도 해주셔야합니다.
        if (userDao.checkUserId(postLoginReq.getUserId()) == 0) {  //ID를 가진 활성 유저가 없다.
            if (userDao.checkDeletedUser(postLoginReq.getUserId()) == 1) { //탈퇴한 회원이라면
                throw new BaseException(USERS_DELETED);
            } else {
                // 활성유저도 아니고 탈퇴한 회원도 아니라면 로그인 X
                throw new BaseException(FAILED_TO_LOGIN);
            }
        }

        UserInfo user = userDao.getPwd(postLoginReq); //암호화된 PW를 DB에서 받아온다.
        String password;
        try {
            password = new AES128(Secret.USER_INFO_PASSWORD_KEY).decrypt(user.getPassword()); //PW 복호화
        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_DECRYPTION_ERROR);
        }

        if(postLoginReq.getPassword().equals(password)){
            //클라이언트가 jwt를 디코딩해서 안에 페이로드값을 볼 수는 있지만 같이 보내주는게 편리하다.
            int userIdx = userDao.getPwd(postLoginReq).getUserIdx();
            String jwt = jwtService.createJwt(userIdx); //useridx를 이용해서 jwt 생성
            return new PostLoginRes(userIdx,jwt);
        }
        else{
            throw new BaseException(FAILED_TO_LOGIN);
        }
    }

    public int checkUserIdx(int userIdx) throws BaseException{
        try{
            return userDao.checkUserIdx(userIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkUserId(String userId) throws BaseException{
        try{
            return userDao.checkUserId(userId);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkEmail(String email) throws BaseException{
        try{
            return userDao.checkEmail(email);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkPhone(String phone) throws BaseException{
        try{
            return userDao.checkPhone(phone);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetRecommendUsersRes> retrieveRecommendUsers(int loginIdx) throws BaseException {
        try{
            return userDao.getRecommendUsers(loginIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }

    }

    public GetFollowUserRes getFollowerUser(int userIdx, int loginIdx) throws BaseException {
        if (checkUserIdx(userIdx) == 0) {
            throw new BaseException(USERS_IDX_NOT_EXISTS);
        }
        try {
            // 유저의 팔로워리스트
            List<FollowUser> getUserRes = userDao.getFollower(loginIdx ,userIdx);
            // 추천 리스트
            List<GetRecommendUsersRes> getRecommendUsersList = userDao.getRecommendUsers(loginIdx);
            // 함께 아는 친구 카운트
            String commonFollowCount = userDao.getCommonFollowCount(loginIdx, userIdx);
            // 유저의 팔로워 수
            String followerCount = userDao.getFollowerCount(userIdx);
            // 유저의 팔로잉 수
            String followingCount = userDao.getFollowingCount(userIdx);

            return new GetFollowUserRes(commonFollowCount, followerCount, followingCount, getUserRes, getRecommendUsersList);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
