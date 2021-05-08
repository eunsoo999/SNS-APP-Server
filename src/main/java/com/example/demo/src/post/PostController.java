package com.example.demo.src.post;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.post.model.GetPostsFeedRes;
import com.example.demo.src.post.model.PostPostReq;
import com.example.demo.src.post.model.PostPostRes;
import com.example.demo.src.postImage.PostImageProvider;
import com.example.demo.src.postImage.PostImageService;
import com.example.demo.src.postImage.model.GetPostImagesRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.POST_POSTS_EMPTY_IMAGE;

@RestController
@RequestMapping("/posts")
public class PostController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final PostProvider postProvider;
    @Autowired
    private final PostService postService;
    @Autowired
    private final PostImageService postImageService;
    @Autowired
    private final PostImageProvider postImageProvider;

    public PostController(PostProvider postProvider, PostService postService, PostImageService postImageService, PostImageProvider postImageProvider) {
        this.postProvider = postProvider;
        this.postService = postService;
        this.postImageService = postImageService;
        this.postImageProvider = postImageProvider;
    }

    /**
     * 메인 게시글 조회 API
     * [GET] /posts
     * @return BaseResponse<List<GetPostRes>>
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetPostsFeedRes>> getPosts() {
        int loginIdx = 1; //todo 로그인 유저 수정하기

        try{
            List<GetPostsFeedRes> getPostList = postProvider.getPosts(loginIdx);
            for (GetPostsFeedRes getPostRes : getPostList) {
                int postIdx = getPostRes.getPostIdx();
                List<GetPostImagesRes> getPostImagesResList = postImageProvider.getPostImage(postIdx);
                getPostRes.setPostImages(getPostImagesResList);
            }
            return new BaseResponse<>(getPostList);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 게시글 작성 API
     * [POST] /posts
     * @return BaseResponse<PostPostRes>
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostPostRes> postPosts(@RequestBody PostPostReq postPostReq) {
        if (postPostReq.getPostImage().isEmpty()) {
            return new BaseResponse<>(POST_POSTS_EMPTY_IMAGE);
        }
        int loginIdx = 3; //todo 로그인 유저 수정하기

        try {
            PostPostRes postPostRes = postService.createPost(postPostReq, loginIdx);
            return new BaseResponse<>(postPostRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 게시글 삭제 API
     * [DELETE] /posts/:postIdx
     * @return BaseResponse<String>
     */
    @ResponseBody
    @DeleteMapping("/{postIdx}")
    public BaseResponse<String> deletePosts(@PathVariable("postIdx") int postIdx) {
        try {
            postService.deletePosts(postIdx);
            String result = "";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}