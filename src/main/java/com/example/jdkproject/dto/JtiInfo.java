package com.example.jdkproject.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class JtiInfo {
    private String memberId;
    private String name;
    private String email;
}
