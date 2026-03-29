package com.example.jdkproject.controller;

import com.example.jdkproject.domain.Response;
import com.example.jdkproject.dto.JtiInfo;
import com.example.jdkproject.dto.MemberFollowDto;
import com.example.jdkproject.exception.CommonErrorException;
import com.example.jdkproject.exception.ErrorStatus;
import com.example.jdkproject.service.FollowService;
import com.example.jdkproject.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/follow")
@Validated
public class FollowController {
    private final static int SUCCESS = 0;

    private final UserService userService;
    private final FollowService followService;

    @GetMapping(value = "/friends/list")
    public Response<List<MemberFollowDto>> getPostComments(@RequestHeader String token) {
        try {
            JtiInfo jtiInfo = userService.verifyToken(token);
            List<MemberFollowDto> memberFollowerVos = followService.getMemberFollowerList(jtiInfo.getMemberId());
            return new Response<>(memberFollowerVos, HttpStatus.OK, SUCCESS);
        } catch (CommonErrorException e) {
            log.warn("Get follower error : {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.warn("Get follower error : {}", e.getMessage());
            throw new CommonErrorException(ErrorStatus.SERVER_ERROR);
        }
    }

    @PostMapping(value = "")
    public Response<Void> followMember(@RequestHeader String token, @RequestBody MemberFollowDto dto) {
        try {
            JtiInfo jtiInfo = userService.verifyToken(token);
            followService.followMember(jtiInfo.getMemberId(), dto.getFollowMemberId());
            return new Response<>(HttpStatus.OK, SUCCESS);
        } catch (CommonErrorException e) {
            log.warn("Follow error : {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.warn("Follow error : {}", e.getMessage());
            throw new CommonErrorException(ErrorStatus.SERVER_ERROR);
        }
    }

    @PostMapping(value = "/unlink")
    public Response<Void> unfollowMember(@RequestHeader String token, @RequestBody MemberFollowDto dto) {
        try {
            JtiInfo jtiInfo = userService.verifyToken(token);
            followService.unfollowMember(jtiInfo.getMemberId(), dto.getFollowMemberId());
            return new Response<>(HttpStatus.OK, SUCCESS);
        } catch (CommonErrorException e) {
            log.warn("Follow error : {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.warn("Follow error : {}", e.getMessage());
            throw new CommonErrorException(ErrorStatus.SERVER_ERROR);
        }
    }

}
