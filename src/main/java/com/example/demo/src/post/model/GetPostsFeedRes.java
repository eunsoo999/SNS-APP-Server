package com.example.demo.src.post.model;

import com.example.demo.src.postImage.model.GetPostImagesRes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetPostsFeedRes {
    private int postIdx;
    private int userIdx;
    private String placeTitle;
    private String userId;
    private String userProfileUrl;
    private int postImgCount;
    private int postLikeCount;
    private boolean likeCheck;
    private String postContents;
    private String commentCount;
    private String postTimeStamp;
    private List<GetPostImagesRes> postImages = new ArrayList<>();

    public GetPostsFeedRes(int postIdx, int userIdx, String placeTitle, String userId, String userProfileUrl, int postImgCount, int postLikeCount, boolean likeCheck, String postContents, String commentCount, String postTimeStamp) {
        this.postIdx = postIdx;
        this.userIdx = userIdx;
        this.placeTitle = placeTitle;
        this.userId = userId;
        this.userProfileUrl = userProfileUrl;
        this.postImgCount = postImgCount;
        this.postLikeCount = postLikeCount;
        this.likeCheck = likeCheck;
        this.postContents = postContents;
        this.commentCount = commentCount;
        this.postTimeStamp = postTimeStamp;
    }
}

