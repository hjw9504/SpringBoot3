package com.example.test.controller;

import com.example.test.dao.TestDao;
import com.example.test.dto.TestDto;
import com.example.test.exception.CommonErrorException;
import com.example.test.exception.ErrorStatus;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.parser.MediaType;
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

    @GetMapping(value = "/test/info")
    public TestDto getInfo(@RequestParam String id) {
        log.info("Test Info: {}", id);

        if (StringUtils.isEmpty(id)) {
            throw new CommonErrorException(ErrorStatus.PARAMETER_NOT_FOUND);
        }

        TestDto result = testDao.getInfo(id);
        if (result == null) {
            throw new CommonErrorException(ErrorStatus.NOT_FOUND);
        }

        return result;
    }

    @PostMapping(value = "/test/info", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public String registInfo(@Valid @RequestBody TestDto testDto) {
        TestDto existInfo = testDao.getInfo(testDto.getId());

        if (existInfo != null) {
            log.warn("Already Exist User: {}", testDto.getId());
            throw new CommonErrorException(ErrorStatus.ALREADY_EXIST);
        }

        int result = testDao.registInfo(testDto.getId(), testDto.getName());

        if (result > 0) {
            return "Success";
        } else {
            log.error("Server Error");
            throw new CommonErrorException(ErrorStatus.SERVER_ERROR);
        }
    }
}
