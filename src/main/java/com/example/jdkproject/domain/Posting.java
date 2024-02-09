package com.example.jdkproject.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Posting {
    private int id;
    private String memberId;
    private String title;
    private String body;
    private String registerTime;
    private String modTime;
}
