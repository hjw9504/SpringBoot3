package com.example.jdkproject.service;

import com.example.jdkproject.entity.TestVo;
import com.example.jdkproject.repository.TestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TestService {
    private final TestRepository testRepository;

    public TestService(TestRepository testRepository) {
        this.testRepository = testRepository;
    }

    public TestVo findById(String id) {
        TestVo testVo = testRepository.findById(id);
        return testVo;
    }
}
