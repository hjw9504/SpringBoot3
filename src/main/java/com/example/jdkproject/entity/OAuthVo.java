package com.example.jdkproject.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "idp_oauth")
public class OAuthVo {
    @Id
    @Column(name = "id")
    private int id;
    private String idpType;
    private String clientId;
    private String clientSecret;
}
