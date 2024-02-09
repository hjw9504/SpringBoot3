package com.example.jdkproject.controller;

import com.example.jdkproject.domain.Response;
import com.example.jdkproject.entity.PostingResultProjection;
import com.example.jdkproject.exception.CommonErrorException;
import com.example.jdkproject.exception.ErrorStatus;
import com.example.jdkproject.service.PostingService;
import com.example.jdkproject.service.UserService;
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
    private final static int SUCCESS = 200;

    private final PostingService postingService;
    private final UserService userService;

    public PostController(PostingService postingService, UserService userService) {
        this.postingService = postingService;
        this.userService = userService;
    }

    @GetMapping(value = "/posting/list")
    public Response<List<PostingResultProjection>> getAllPost(@Valid @RequestParam String memberId, @Valid @RequestHeader String token) {
        try {
            // verify token
            userService.verifyToken(memberId, token);
        } catch(Exception e) {
            throw new CommonErrorException(ErrorStatus.TOKEN_VERIFY_FAIL);
        }

        List<PostingResultProjection> postingVos = postingService.getAllPost();
        return new Response<>(postingVos, HttpStatus.OK, SUCCESS);
    }

    @GetMapping(value = "/posting/detail/{postingId}")
    public Response<PostingResultProjection> getPostByMemberId(@Valid @RequestParam String memberId,
                                                               @Valid @PathVariable int postingId,
                                                               @Valid @RequestHeader String token) {
        try {
            // verify token
            userService.verifyToken(memberId, token);
        } catch(Exception e) {
            throw new CommonErrorException(ErrorStatus.TOKEN_VERIFY_FAIL);
        }

        PostingResultProjection postingDetails = postingService.getPostByMemberId(postingId);
        return new Response<>(postingDetails, HttpStatus.OK, SUCCESS);
    }
}
