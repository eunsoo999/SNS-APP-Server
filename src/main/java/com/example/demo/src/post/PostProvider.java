package com.example.demo.src.post;

import com.example.demo.config.BaseException;
import com.example.demo.src.PostLike.model.GetPostLikes;
import com.example.demo.src.post.model.GetPostsFeedRes;
import com.example.demo.src.post.model.GetPostsMainFeedRes;
import com.example.demo.src.post.model.GetPostsRes;
import com.example.demo.src.postImage.PostImageDao;
import com.example.demo.src.postImage.model.GetPostImagesRes;
import com.example.demo.src.story.StoryDao;
import com.example.demo.src.story.model.GetStorysRes;
import com.example.demo.src.user.UserProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
@Transactional
public class PostProvider {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PostDao postDao;
    private final PostImageDao postImageDao;
    private final UserProvider userProvider;
    private final StoryDao storyDao;

    @Autowired
    public PostProvider(PostDao postDao, UserProvider userProvider, PostImageDao postImageDao, StoryDao storyDao) {
        this.postDao = postDao;
        this.userProvider = userProvider;
        this.postImageDao = postImageDao;
        this.storyDao = storyDao;
    }

    //게시물미리보기
    public List<GetPostsRes> getPostsByUserIdx(int userIdx) throws BaseException {
        if (userProvider.checkUserIdx(userIdx) == 0) {
            throw new BaseException(USERS_IDX_NOT_EXISTS);
        }
        try {
            List<GetPostsRes> getPostRes = postDao.getPostsByUserIdx(userIdx);
            return getPostRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetPostsFeedRes> getPosts(int loginIdx) throws BaseException {
        try {
            List<GetPostsFeedRes> getPostRes = postDao.getPostsFeed(loginIdx);
            return getPostRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetPostsFeedRes> getPostsFeedByUserIdx(int userIdx, int loginIdx) throws BaseException {

        if (userProvider.checkUserIdx(userIdx) == 0) {
            throw new BaseException(USERS_IDX_NOT_EXISTS);
        }
        try {
            List<GetPostsFeedRes> getPostList = postDao.getPostsFeedByUserIdx(userIdx, loginIdx);

            for (GetPostsFeedRes getPostsFeedRes : getPostList) {
                int postIdx = getPostsFeedRes.getPostIdx();
                List<GetPostImagesRes> getPostImagesResList = postImageDao.getPostImage(postIdx);
                getPostsFeedRes.setPostImages(getPostImagesResList);
            }

            return getPostList;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkPostIdx(int postIdx) {
        return postDao.checkPostIdx(postIdx);
    }

    public List<GetPostLikes> retrievePostLikedUsers(int postIdx, int loginIdx) throws BaseException {
        if (checkPostIdx(postIdx) == 0) {
            throw new BaseException(POSTS_NOT_EXISTS);
        }
        try {
            List<GetPostLikes> getPostLikes = postDao.retrievePostLikedUsers(postIdx,loginIdx);
            return getPostLikes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetPostsMainFeedRes getPostsMainFeed(int userIdxByJwt) throws BaseException {
        System.out.println("서비스");
        try {
            // 메인 - 팔로우한 유저들의 활성화 스토리
            List<GetStorysRes> getStorysResList = storyDao.getStorys(userIdxByJwt);
            // 메인 - 팔로우한 유저 && 나의 게시글
            List<GetPostsFeedRes> getPostList = postDao.getPostsFeed(userIdxByJwt);

            // 포스트 이미지
            for (GetPostsFeedRes getPostRes : getPostList) {
                int postIdx = getPostRes.getPostIdx();
                List<GetPostImagesRes> getPostImagesResList = postImageDao.getPostImage(postIdx);
                getPostRes.setPostImages(getPostImagesResList);
            }
            return new GetPostsMainFeedRes(getStorysResList, getPostList);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
