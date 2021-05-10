package com.example.demo.src.follow;

import com.example.demo.config.BaseException;
import com.example.demo.src.follow.model.PostFollowReq;
import com.example.demo.src.follow.model.PostFollowRes;
import com.example.demo.src.user.UserProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class FollowService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final FollowDao followDao;
    private final UserProvider userProvider;
    private final FollowProvider followProvider;

    @Autowired
    public FollowService(FollowDao followDao, UserProvider userProvider, FollowProvider followProvider) {
        this.followDao = followDao;
        this.userProvider = userProvider;
        this.followProvider = followProvider;
    }

    public PostFollowRes createFollow(PostFollowReq postFollowReq, int loginIdx) throws BaseException{
        // 유저 확인
        if (userProvider.checkUserIdx(postFollowReq.getFollowingUserIdx()) == 0) {
            throw new BaseException(USERS_IDX_NOT_EXISTS);
        }

        // 이미 팔로우 상태인지 확인
        if (followProvider.checkFollow(loginIdx, postFollowReq.getFollowingUserIdx()) == 1) {
            throw new BaseException(DUPLICATED_FOLLOWS);
        }

        try{
            int followIdx = followDao.createFollow(postFollowReq, loginIdx);
            return new PostFollowRes(followIdx);
        }
        catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void deleteFollow(int followIdx) throws BaseException {
        if (followDao.checkFollowsIdx(followIdx) == 0) {
            throw new BaseException(FOLLOWS_NOT_EXISTS);
        }
        try {
            int deletedCount = followDao.deleteFollow(followIdx);
            if (deletedCount != 1) {
                throw new BaseException(PATCH_STATUS_FAIL);
            }
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
