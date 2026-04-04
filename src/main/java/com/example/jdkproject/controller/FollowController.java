package com.example.jdkproject.controller;

import com.example.jdkproject.annotation.TokenCheck;
import com.example.jdkproject.domain.Response;
import com.example.jdkproject.dto.JtiInfo;
import com.example.jdkproject.dto.MemberFollowDto;
import com.example.jdkproject.service.FollowService;
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
@RequestMapping("/follow")
@Validated
public class FollowController {
    private final static int SUCCESS = 0;

    private final UserService userService;
    private final FollowService followService;

    @TokenCheck
    @GetMapping(value = "/friends/list")
    public Response<List<MemberFollowDto>> getPostComments(HttpServletRequest request) {

        JtiInfo jtiInfo = (JtiInfo) request.getAttribute("jtiInfo");
        List<MemberFollowDto> memberFollowerVos = followService.getMemberFollowerList(jtiInfo.getMemberId());
        return new Response<>(memberFollowerVos, HttpStatus.OK, SUCCESS);
    }

    @TokenCheck
    @PostMapping(value = "")
    public Response<Void> followMember(HttpServletRequest request, @RequestBody MemberFollowDto dto) {

        JtiInfo jtiInfo = (JtiInfo) request.getAttribute("jtiInfo");
        followService.followMember(jtiInfo.getMemberId(), dto.getFollowMemberId());
        return new Response<>(HttpStatus.OK, SUCCESS);
    }

    @TokenCheck
    @PostMapping(value = "/unlink")
    public Response<Void> unfollowMember(HttpServletRequest request, @RequestBody MemberFollowDto dto) {

        JtiInfo jtiInfo = (JtiInfo) request.getAttribute("jtiInfo");
        followService.unfollowMember(jtiInfo.getMemberId(), dto.getFollowMemberId());
        return new Response<>(HttpStatus.OK, SUCCESS);
    }

}
