package com.example.jdkproject.repository;

import com.example.jdkproject.entity.MemberVo;
import com.example.jdkproject.entity.OAuthVo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface OAuthRepository extends JpaRepository<OAuthVo, Long> {
    @Query("select i from OAuthVo i where i.idpType = :idpType")
    OAuthVo findIdpOAuth(String idpType);
}
