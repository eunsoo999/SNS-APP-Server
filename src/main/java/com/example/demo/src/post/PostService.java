package com.example.demo.src.post;

import com.example.demo.config.BaseException;
import com.example.demo.src.post.model.PatchPostsReq;
import com.example.demo.src.post.model.PostPostsReq;
import com.example.demo.src.post.model.PostPostsRes;
import com.example.demo.src.postImage.PostImageDao;
import com.example.demo.src.postImage.model.PostPostImageReq;
import com.example.demo.src.postTag.PostTagDao;
import com.example.demo.src.tag.TagDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.*;

@Service
@Transactional(rollbackOn = BaseException.class) //rollbackOn : 롤백시킬 Exception
public class PostService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PostDao postDao;
    private final PostProvider postProvider;
    private final PostImageDao postImageDao;
    private final TagDao tagDao;
    private final PostTagDao postTagDao;

    @Autowired
    public PostService(PostDao postDao, PostProvider postProvider, PostImageDao postImageDao, TagDao tagDao, PostTagDao postTagDao) {
        this.postDao = postDao;
        this.postProvider = postProvider;
        this.postImageDao = postImageDao;
        this.tagDao = tagDao;
        this.postTagDao = postTagDao;
    }

    public PostPostsRes createPost(PostPostsReq postPostReq, int loginIdx) throws BaseException {
        try {
            int postIdx = postDao.createPost(postPostReq, loginIdx);
            for (PostPostImageReq postImageReq : postPostReq.getPostImage()) {
                postImageDao.createPostImage(postImageReq, postIdx);
            }
            List<String> tags = getTagsInContents(postPostReq.getContents()); //작성글에서 해시태그 추출
            // Tag, PostTag 저장
            for (int i = 0; i < tags.size(); i++) {
                int tagIdx = tagDao.createTags(tags.get(i)); //생성된 태그면 생성된 태그의 Idx, 기존에 있던 태그면 그 태그의 Idx를 반환
                postTagDao.createPostTag(postIdx, tagIdx);
            }
            return new PostPostsRes(postIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int updatePost(PatchPostsReq patchPostsReq, int postIdx, int loginIdx) throws BaseException {
        if (postDao.getPosts(postIdx).getUserIdx() != loginIdx) {
            throw new BaseException(INVALID_USER_JWT);
        }

        if (postProvider.checkPostIdx(postIdx) == 0) {
            throw new BaseException(DELETE_POSTS_NOT_EXISTS);
        }

        try {
            //포스트-태그 삭제
            postTagDao.patchStatusPostTags(postIdx);

            // Tag, PostTag 저장
            List<String> tags = getTagsInContents(patchPostsReq.getContents()); //수정한 작성글에서 해시태그 추출
            for (int i = 0; i < tags.size(); i++) {
                int tagIdx = tagDao.createTags(tags.get(i)); //생성된 태그면 생성된 태그의 Idx, 기존에 있던 태그면 그 태그의 Idx를 반환
                postTagDao.createPostTag(postIdx, tagIdx);
            }
            return postDao.patchPost(patchPostsReq, postIdx); //포스트 테이블 수정
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }

    }

    public void updatePostsStatus(int postIdx, int loginIdx) throws BaseException {
        // 해당 게시글과 유저가 맞는지 확인
        if (postDao.getPosts(postIdx).getUserIdx() != loginIdx) {
            throw new BaseException(INVALID_USER_JWT);
        }
        // 게시글이 존재하는지 확인
        if (postProvider.checkPostIdx(postIdx) == 0) {
            throw new BaseException(DELETE_POSTS_NOT_EXISTS);
        }
        try {
            postTagDao.patchStatusPostTags(postIdx);
            postDao.patchPostsStatus(postIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    private List<String> getTagsInContents(String c) {
        StringBuilder sb = new StringBuilder();
        List<String> tags = new ArrayList<>();

        boolean flag = false;
        for (int i = 0; i < c.length(); i++) {
            if (c.charAt(i) == ' ') {
                if (flag) {
                    if (sb.length() >= 1) {
                        tags.add(sb.toString());
                        sb.setLength(0);
                        flag = false;
                    } else {
                        sb.setLength(0);
                        flag = false; //"# "인 경우 해시태그flag해제
                    }
                } else {
                    flag = false;
                }
            } else if (c.charAt(i) == '#') {
                if (flag) {
                    if (sb.length() >= 1) {
                        tags.add(sb.toString());
                        sb.setLength(0);
                        flag = true; // #피자#곱창 인 상황이므로 True
                    }
                } else {
                    flag = true;
                }
            } else {
                if (flag) {
                    if (isRegexSpecial(String.valueOf(c.charAt(i)))) {
                        if (sb.length() >= 1) {
                            tags.add(sb.toString());
                        }
                        sb.setLength(0);
                        flag = false;
                    } else {
                        sb.append(c.charAt(i));
                    }
                }
            }
        }
        if (sb.length() >= 1) {
            tags.add(sb.toString());
        }
        return tags;
    }
}
