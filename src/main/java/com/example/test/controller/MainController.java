package com.example.test.controller;

import com.example.test.dao.TestDao;
import com.example.test.dto.TestDto;
import com.example.test.exception.CommonErrorException;
import com.example.test.exception.ErrorStatus;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.parser.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@Validated
public class MainController {
    private final TestDao testDao;
    public MainController(TestDao testDao) {
        this.testDao = testDao;
    }

    @GetMapping(value = "/")
    public String mainPage() {
        return "Hello";
    }

    @GetMapping(value = "/test")
    public String init() {
        log.info("TEST");
        return "Test";
    }

    @GetMapping(value = "/user/info")
    public ResponseEntity<TestDto> getInfo(@RequestParam String id) {
        log.info("Test Info: {}", id);

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
    public ResponseEntity<String> registInfo(@Valid @RequestBody TestDto testDto) {
        int result = testDao.registInfo(testDto.getName());

        if (result > 0) {
            return new ResponseEntity<>("Success", HttpStatus.OK);
        } else {
            log.error("Server Error");
            throw new CommonErrorException(ErrorStatus.SERVER_ERROR);
        }
    }
}
