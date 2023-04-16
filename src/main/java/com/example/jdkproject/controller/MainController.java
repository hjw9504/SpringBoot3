package com.example.jdkproject.controller;

import com.example.jdkproject.dao.TestDao;
import com.example.jdkproject.domain.Member;
import com.example.jdkproject.dto.LoginDto;
import com.example.jdkproject.dto.TestDto;
import com.example.jdkproject.dto.UserDto;
import com.example.jdkproject.exception.CommonErrorException;
import com.example.jdkproject.exception.ErrorStatus;
import com.example.jdkproject.service.KafkaProducer;
import com.example.jdkproject.service.UserService;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@Validated
public class MainController {
    private final UserService userService;
    private final TestDao testDao;
    private final KafkaProducer kafkaProducer;

    public MainController(UserService userService, TestDao testDao, KafkaProducer kafkaProducer) {
        this.userService = userService;
        this.testDao = testDao;
        this.kafkaProducer = kafkaProducer;
    }

    @GetMapping(value = "/")
    public String mainPage() {
        log.info("Init");
        return "Hello";
    }

    @GetMapping(value = "/test")
    public String init() {
        log.info("TEST");
        return "Test";
    }

    @GetMapping(value = "/user/info")
    public ResponseEntity<TestDto> getInfo(@RequestParam String id) {
        log.info("User Info: {}", id);

        if (StringUtils.isEmpty(id)) {
            throw new CommonErrorException(ErrorStatus.PARAMETER_NOT_FOUND);
        }

        TestDto result = testDao.getInfo(id);
        if (result == null) {
            throw new CommonErrorException(ErrorStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping(value = "/user/info", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> registInfo(@Valid @RequestBody UserDto userDto) throws NoSuchPaddingException, IllegalBlockSizeException, UnsupportedEncodingException, NoSuchAlgorithmException, BadPaddingException, InvalidKeySpecException, InvalidKeyException {
        userService.register(userDto);

        return new ResponseEntity("success", HttpStatus.OK);
    }

    @PostMapping(value = "/user/login", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Member> getMemberInfo(@Valid @RequestBody LoginDto loginDto) throws NoSuchPaddingException, IllegalBlockSizeException, UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException {
        log.info("User Login");
        Member member = userService.login(loginDto.getUserId(), loginDto.getUserPw());

        //login message
        kafkaProducer.sendMessage(member.getMemberId());

        return new ResponseEntity<>(member, HttpStatus.OK);
    }

    @PostMapping(value = "/user/token/verify", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity verifyToken(@Valid @RequestHeader String token, @RequestBody Member member) throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (member.getMemberId() == null) {
            throw new CommonErrorException(ErrorStatus.PARAMETER_NOT_FOUND);
        }

        userService.verifyToken(member.getMemberId(), token);

        return new ResponseEntity("success", HttpStatus.OK);
    }
}
