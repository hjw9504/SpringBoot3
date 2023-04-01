package com.example.test.dao;

import com.example.test.dto.TestDto;
import com.example.test.mapper.TestMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

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

    public int registInfo(String id, String name) {
        return testMapper.insertInfo(id, name);
    }
}
