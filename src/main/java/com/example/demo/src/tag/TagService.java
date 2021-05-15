package com.example.demo.src.tag;

import com.example.demo.config.BaseException;
import com.example.demo.src.tag.model.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class TagService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final TagDao tagDao;

    @Autowired
    public TagService(TagDao tagDao) {
        this.tagDao = tagDao;
    }

    public int createTags(String tagName) throws BaseException {

        try {
            if (tagDao.checkTagByTagName(tagName) == 0) {
                return tagDao.createTags(tagName);//테이블에 해당 태그가 없으면 태그 저장
            } else {
                Tag tag = tagDao.getTagsByTagName(tagName);
                return tag.getIdx();
            }
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
