package com.example.test.mapper;

import com.example.test.dto.TestDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TestMapper {
    TestDto getInfo(String id);

    int insertInfo(String id, String name);
}
