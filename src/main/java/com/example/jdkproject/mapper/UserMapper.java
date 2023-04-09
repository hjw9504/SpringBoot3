package com.example.jdkproject.mapper;

import com.example.jdkproject.domain.Member;
import com.example.jdkproject.domain.MemberSecureInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    Member getUser(String userId);

    MemberSecureInfo getUserSecure(String memberId);

    int insertMemberSecure(String memberId, String publicKey, String privateKey);

    int insertMember(String memberId, String name, String email, String userId, String userPw, String phone, String nickname);

    int updateLoginTime(String id);
}
