package com.example.jdkproject.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "member_secure")
public class MemberSecureVo {
    @Id
    @Column(name = "member_id")
    private String memberId;
    private String privateKey;
    private String publicKey;

    @Builder
    public MemberSecureVo(String memberId, String privateKey, String publicKey) {
        this.memberId = memberId;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }
}
