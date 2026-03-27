package com.example.jdkproject.controller;

import com.example.jdkproject.domain.Response;
import com.example.jdkproject.dto.CommentDto;
import com.example.jdkproject.dto.PostingDto;
import com.example.jdkproject.exception.CommonErrorException;
import com.example.jdkproject.exception.ErrorStatus;
import com.example.jdkproject.service.CommentService;
import com.example.jdkproject.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/comment")
@Validated
public class CommentController {
    private final static int SUCCESS = 0;

    private final UserService userService;
    private final CommentService commentService;

    @GetMapping(value = "/")
    public Response<List<PostingDto>> getPostComments(@Valid @RequestHeader String token,
                                                      @RequestParam(required = false) String memberId) {
//        try {
//            // verify token
//            userService.verifyToken(token);
//
//            List<PostingDto> postingVos = postingService.getAllPost(memberId);
//            return new Response<>(postingVos, HttpStatus.OK, SUCCESS);
//        } catch(CommonErrorException e) {
//            log.warn("Get posting error : {}", e.getMessage());
//            throw e;
//        } catch(Exception e) {
//            log.warn("Get posting error : {}", e.getMessage());
//            throw new CommonErrorException(ErrorStatus.TOKEN_VERIFY_FAIL);
//        }
        return null;
    }

    @PostMapping(value = "/{postingId}")
    public Response<Void> registerPostComments(@RequestHeader String token, @PathVariable int postingId,
                                               @RequestBody CommentDto commentDto) {
        try {
            userService.verifyToken(token);
            commentService.registerPostComment(postingId, commentDto.getMemberId(), commentDto.getComment());
            return new Response<>(HttpStatus.OK, SUCCESS);
        } catch (CommonErrorException e) {
            throw e;
        } catch (Exception e) {
            throw new CommonErrorException(ErrorStatus.SERVER_ERROR);
        }
    }
}
