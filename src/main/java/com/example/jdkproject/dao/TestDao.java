package com.example.jdkproject.dao;

import com.example.jdkproject.dto.TestDto;
import com.example.jdkproject.mapper.TestMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Slf4j
@Repository
public class TestDao {
    private final TestMapper testMapper;

    public TestDao(TestMapper testMapper) {
        this.testMapper = testMapper;
    }

    public TestDto getInfo(String id) {
        return testMapper.getInfo(id);
    }

    public int registInfo(String name) {
        //create user id
        String id = UUID.randomUUID().toString();

        return testMapper.insertInfo(id, name);
    }
}
