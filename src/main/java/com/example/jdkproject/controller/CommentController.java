package com.example.jdkproject.controller;

import com.example.jdkproject.annotation.TokenCheck;
import com.example.jdkproject.domain.Response;
import com.example.jdkproject.dto.CommentDto;
import com.example.jdkproject.dto.JtiInfo;
import com.example.jdkproject.exception.CommonErrorException;
import com.example.jdkproject.exception.ErrorStatus;
import com.example.jdkproject.service.CommentService;
import com.example.jdkproject.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
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

    @TokenCheck
    @GetMapping(value = "/{postingId}")
    public Response<CommentDto> getPostComments(@PathVariable int postingId) {
        CommentDto commentDto = commentService.getPostComment(postingId);
        return new Response<>(commentDto, HttpStatus.OK, SUCCESS);
    }

    @TokenCheck
    @PostMapping(value = "/{postingId}")
    public Response<Void> registerPostComments(HttpServletRequest request,
                                               @PathVariable int postingId,
                                               @RequestBody CommentDto.CommentItem commentItem) {

        JtiInfo jtiInfo = (JtiInfo) request.getAttribute("jtiInfo");
        commentService.registerPostComment(postingId, jtiInfo.getMemberId(), commentItem.getComment());
        return new Response<>(HttpStatus.OK, SUCCESS);
    }
}
