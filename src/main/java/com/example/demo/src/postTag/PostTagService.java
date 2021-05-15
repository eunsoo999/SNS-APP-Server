package com.example.demo.src.postTag;

import com.example.demo.config.BaseException;
import com.example.demo.src.post.PostDao;
import com.example.demo.src.tag.TagDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class PostTagService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PostTagDao postTagDao;;
    private final PostDao postDao;
    private final TagDao tagDao;

    @Autowired
    public PostTagService(PostTagDao postTagDao, PostDao postDao, TagDao tagDao) {
        this.postTagDao = postTagDao;
        this.postDao = postDao;
        this.tagDao = tagDao;
    }

    public int createPostTag(int postIdx, int tagIdx) throws BaseException {
        // 포스트가 있는지 확인
        if (postDao.checkPostIdx(postIdx) == 0) {
            throw new BaseException(POSTS_NOT_EXISTS);
        }
        // 태그가 있는지 확인
        if (tagDao.checkTagByIdx(tagIdx) == 0) {
            throw new BaseException(TAGS_NOT_EXISTS);
        }
        try {
            return postTagDao.createPostTag(postIdx, tagIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
