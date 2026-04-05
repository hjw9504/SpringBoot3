package com.example.jdkproject.controller;

import com.example.jdkproject.annotation.TokenCheck;
import com.example.jdkproject.domain.Posting;
import com.example.jdkproject.domain.Response;
import com.example.jdkproject.dto.JtiInfo;
import com.example.jdkproject.dto.PostingDto;
import com.example.jdkproject.dto.PostingLikesDto;
import com.example.jdkproject.entity.PostingResultProjection;
import com.example.jdkproject.service.PostingService;
import com.example.jdkproject.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@Validated
public class PostController {
    private final static int SUCCESS = 0;

    private final PostingService postingService;
    private final UserService userService;

    public PostController(PostingService postingService, UserService userService) {
        this.postingService = postingService;
        this.userService = userService;
    }

    @TokenCheck
    @GetMapping(value = "/posting/all")
    public Response<List<PostingDto>> getAllPost(HttpServletRequest request,
                                                 @RequestParam(required = false, value = "member_id") String memberId) {

        List<PostingDto> postingVos = postingService.getAllPost(memberId);
        return new Response<>(postingVos, HttpStatus.OK, SUCCESS);
    }

    @TokenCheck
    @GetMapping(value = "/posting/list")
    public Response<List<PostingResultProjection>> getPostByMemberId(HttpServletRequest request) {

        JtiInfo jtiInfo = (JtiInfo) request.getAttribute("jtiInfo");
        List<PostingResultProjection> postingVos = postingService.getPostByMemberId(jtiInfo.getMemberId());

        return new Response<>(postingVos, HttpStatus.OK, SUCCESS);
    }

    @TokenCheck
    @GetMapping(value = "/posting/detail/{postingId}")
    public Response<PostingResultProjection> getPostByPostingId(@Valid @PathVariable int postingId) {

        PostingResultProjection postingDetails = postingService.getPostByPostingId(postingId);
        return new Response<>(postingDetails, HttpStatus.OK, SUCCESS);
    }

    @TokenCheck
    @PostMapping(value = "/posting/register")
    @ResponseBody
    public Response<String> saveNewPosting(@Valid @RequestBody Posting posting) {
        if (posting.getId() == 0) {
            // register
            postingService.saveNewPost(posting);
        } else {
            postingService.updatePost(posting);
        }

        return new Response("success", HttpStatus.OK, SUCCESS);
    }

    @TokenCheck
    @GetMapping("/posting/likes/{postingId}")
    public Response<List<PostingLikesDto>> getPostLikesWithMemberId(HttpServletRequest request,
                                                                    @PathVariable int postingId) {

        JtiInfo info = (JtiInfo) request.getAttribute("jtiInfo");
        return new Response<>(postingService.getPostLikes(postingId, info.getMemberId()), HttpStatus.OK, SUCCESS);
    }

    @TokenCheck
    @PostMapping("/posting/likes/{type}/{postingId}")
    public Response<Void> updatePostingLikes(HttpServletRequest request,
                                             @PathVariable String type,
                                             @PathVariable int postingId) {

        JtiInfo info = (JtiInfo) request.getAttribute("jtiInfo");
        postingService.updatePostLikes(info.getMemberId(), postingId, type);
        return new Response<>(HttpStatus.OK, SUCCESS);
    }

    @TokenCheck
    @DeleteMapping("/posting/{postingId}")
    public Response<Void> deletePostByPostingId(HttpServletRequest request,
                                                @PathVariable int postingId) {

        JtiInfo info = (JtiInfo) request.getAttribute("jtiInfo");
        postingService.deletePost(info.getMemberId(), postingId);
        return new Response<>(HttpStatus.OK, SUCCESS);
    }
}
