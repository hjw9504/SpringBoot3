package com.example.jdkproject.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TestDto {
    private String id;
    @NotBlank
    private String name;
    private String regDate;
}
