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
        // todo jwt
        int loginIdx = 1;
        try{
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
        UserInfo user = userDao.getPwd(postLoginReq);
        String password;
        try {
            password = new AES128(Secret.USER_INFO_PASSWORD_KEY).decrypt(user.getPassword());
        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_DECRYPTION_ERROR);
        }

        if(postLoginReq.getPassword().equals(password)){
            int userIdx = userDao.getPwd(postLoginReq).getUserIdx();
            String jwt = jwtService.createJwt(userIdx);
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
}
