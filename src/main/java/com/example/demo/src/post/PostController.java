package com.example.demo.src.post;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.PostLike.model.GetPostLikes;
import com.example.demo.src.comment.CommentProvider;
import com.example.demo.src.comment.model.GetCommentsRes;
import com.example.demo.src.post.model.*;
import com.example.demo.src.postImage.PostImageProvider;
import com.example.demo.src.postImage.PostImageService;
import com.example.demo.src.postImage.model.GetPostImagesRes;
import com.example.demo.src.postImage.model.PostPostImageReq;
import com.example.demo.src.story.StoryProvider;
import com.example.demo.src.story.model.GetStorysRes;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.ValidationRegex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/posts")
public class PostController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final PostProvider postProvider;
    @Autowired
    private final PostService postService;
    @Autowired
    private final JwtService jwtService;
    @Autowired
    private final CommentProvider commentProvider;

    public PostController(PostProvider postProvider, PostService postService, JwtService jwtService, CommentProvider commentProvider) {
        this.postProvider = postProvider;
        this.postService = postService;
        this.jwtService = jwtService;
        this.commentProvider = commentProvider;
    }

    /**
     * 메인 게시글 조회 API
     * [GET] /posts
     * @return BaseResponse<List<GetPostRes>>
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<GetPostsMainFeedRes> getPosts() {
        try{
            int userIdxByJwt = jwtService.getUserIdx(); // idx 추출
            GetPostsMainFeedRes result = postProvider.getPostsMainFeed(userIdxByJwt);
            return new BaseResponse<>(result);
        } catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 게시글 작성 API
     * [POST] /posts
     * @return BaseResponse<PostPostRes>
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostPostsRes> postPosts(@RequestBody PostPostsReq postPostReq) {
        if (postPostReq.getPostImage().isEmpty()) { return new BaseResponse<>(POST_POSTS_EMPTY_IMAGE); }
        for (PostPostImageReq postImageReq : postPostReq.getPostImage()) {
            if (!ValidationRegex.isRegexImage(postImageReq.getImageUrl())) {
                return new BaseResponse<>(INVALID_IMAGE_URL);
            }
        }
        try {
            int userIdxByJwt = jwtService.getUserIdx();
            PostPostsRes postPostRes = postService.createPost(postPostReq, userIdxByJwt);
            return new BaseResponse<>(postPostRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 게시글 수정 API
     * [PATCH] /posts/:postIdx
     * @return BaseResponse<PatchPostsRes>
     */
    @ResponseBody
    @PatchMapping("/{postIdx}")
    public BaseResponse<PostPostsRes> postPosts(@RequestBody PatchPostsReq postPostReq, @PathVariable int postIdx) {
        try {
            int userIdxByJwt = jwtService.getUserIdx();
            int updatedIdx = postService.updatePost(postPostReq, postIdx,userIdxByJwt);
            return new BaseResponse<>(new PostPostsRes(updatedIdx));
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 게시글 삭제 API
     * [PATCH] /posts/:postIdx/status
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/{postIdx}/status")
    public BaseResponse<String> patchPostsStatus(@PathVariable("postIdx") int postIdx) {
        try {
            int userIdxByJwt = jwtService.getUserIdx();
            postService.updatePostsStatus(postIdx, userIdxByJwt);
            String result = ""; //todo 삭제 리턴값 고치기
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 게시글 좋아요누른 유저 조회(+검색) API
     * [GET] /posts/:postIdx/liked
     */
    @ResponseBody
    @GetMapping("/{postIdx}/liked/users")
    public BaseResponse<List<GetPostLikes>> getPostLikedUsers(@PathVariable("postIdx") int postIdx) {
        try {
            int loginIdx = jwtService.getUserIdx();
            List<GetPostLikes> getPostLikes = postProvider.retrievePostLikedUsers(postIdx, loginIdx);
            return new BaseResponse<>(getPostLikes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 게시글 댓글 조회 API
     * [GET] /posts/:postIdx/comments
     */
    @ResponseBody
    @GetMapping("/{postIdx}/comments")
    public BaseResponse<List<GetCommentsRes>> getComments(@PathVariable int postIdx) {
        try {
            int loginIdx = jwtService.getUserIdx();
            List<GetCommentsRes> getCommentsRes = commentProvider.retrieveComments(postIdx, loginIdx);
            return new BaseResponse<>(getCommentsRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
}