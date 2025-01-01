package com.example.jdkproject.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TestDto {
    private String id;
    private String name;
    private String regDate;
}
