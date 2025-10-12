package com.example.jdkproject.service;

import com.example.jdkproject.entity.TestVo;
import com.example.jdkproject.repository.TestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class TestService {
    private final TestRepository testRepository;

    public TestVo findById(String id) {
        TestVo testVo = testRepository.findById(id);
        return testVo;
    }

    public String initTest() {
        return "HELLO";
    }
}
