package com.example.jdkproject.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity(name = "tbInfo")
public class TestVo {
    @Id
    private String id;
    private String name;
    private String regDate;
}
