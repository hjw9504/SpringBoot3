package com.example.jdkproject.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ChattingDto {
    public enum MessageType { ENTER, TALK, LEAVE }

    @NotNull
    private MessageType type;

    private long roomId;

    @NotBlank
    private String roomName;

    @NotBlank
    private String memberId;

    @NotBlank
    private String token;

    @NotBlank
    private String sender;

    private String message;
}