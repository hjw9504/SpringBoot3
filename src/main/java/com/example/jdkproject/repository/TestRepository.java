package com.example.jdkproject.repository;

import com.example.jdkproject.dto.TestDto;
import com.example.jdkproject.entity.MemberVo;
import com.example.jdkproject.entity.TestVo;
import com.example.jdkproject.mapper.TestMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TestRepository extends JpaRepository<TestVo, Long> {
    public TestVo findById(String id);
}
