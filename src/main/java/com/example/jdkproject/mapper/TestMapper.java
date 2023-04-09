package com.example.jdkproject.mapper;

import com.example.jdkproject.dto.TestDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TestMapper {
    TestDto getInfo(String id);

    int insertInfo(String id, String name);
}
