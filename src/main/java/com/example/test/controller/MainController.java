package com.example.test.controller;

import com.example.test.dao.TestDao;
import com.example.test.dto.TestDto;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.parser.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@CrossOrigin("*")
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

    @GetMapping(value = "/test/info")
    public TestDto getInfo(@RequestParam String id) {
        log.info("Test Info: TEST");

        if (StringUtils.isEmpty(id)) {
            throw new NullPointerException();
        }

        return testDao.getInfo(id);
    }

    @PostMapping(value = "/test/info", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public String registInfo(@RequestBody TestDto testDto) {
        TestDto existInfo = testDao.getInfo(testDto.getId());

        if (existInfo != null) {
            throw new RuntimeException("Exist Info");
        }

        int result = testDao.registInfo(testDto.getId(), testDto.getName());

        if (result > 0) {
            return "Success";
        } else {
            return "Error";
        }
    }
}
