package com.example.demo.src.comment;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.comment.model.PostCommentsReq;
import com.example.demo.src.comment.model.PostCommentsRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
public class CommentController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final CommentProvider commentProvider;
    @Autowired
    private final CommentService commentService;
    @Autowired
    private final JwtService jwtService;

    public CommentController(CommentProvider commentProvider, CommentService commentService, JwtService jwtService) {
        this.commentProvider = commentProvider;
        this.commentService = commentService;
        this.jwtService = jwtService;
    }

    /**
     * 댓글 추가 API
     * [POST] /comments
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostCommentsRes> postComments(@RequestBody PostCommentsReq postCommentsReq) {
        try {
            int userIdxByJwt = jwtService.getUserIdx();
            PostCommentsRes result = commentService.createComments(postCommentsReq, userIdxByJwt);
            return new BaseResponse<>(result);
        } catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 댓글 삭제 API
     * [PATCH] /comments/:commentIdx/status
     */
    @ResponseBody
    @PatchMapping("/{commentIdx}/status")
    public BaseResponse<String> patchCommentsStatus(@PathVariable int commentIdx) {
        try {
            int loginIdx = jwtService.getUserIdx();
            int updatedIdx = commentService.updateCommentStatus(commentIdx, loginIdx);
            String result = "";
            return new BaseResponse<>(result);
        } catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
