package com.example.demo.src.follow;

import com.example.demo.config.BaseException;
import com.example.demo.src.user.UserProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class FollowProvider {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final FollowDao followDao;

    @Autowired
    public FollowProvider(FollowDao followDao) {
        this.followDao = followDao;
    }

    public int checkFollow(int loginIdx, int followingUserIdx) throws BaseException {
        try{
            return followDao.checkFollow(loginIdx, followingUserIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
