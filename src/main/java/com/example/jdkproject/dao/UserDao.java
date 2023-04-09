package com.example.jdkproject.dao;

import com.example.jdkproject.domain.Member;
import com.example.jdkproject.domain.MemberSecureInfo;
import com.example.jdkproject.dto.UserDto;
import com.example.jdkproject.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class UserDao {
    private final UserMapper userMapper;

    public UserDao(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public Member getUser(String userId) {
        return userMapper.getUser(userId);
    }
    
    public MemberSecureInfo getUserSecure(String memberId) {
        return userMapper.getUserSecure(memberId);
    }

    public int insertUserSecure(String memberId, String publicKey, String privateKey) {
        return userMapper.insertMemberSecure(memberId, publicKey, privateKey);
    }

    public int insertMember(UserDto userDto) {
        return userMapper.insertMember(userDto.getMemberId(), userDto.getName(), userDto.getEmail(), userDto.getUserId(), userDto.getUserPw(), userDto.getPhone(), userDto.getNickName());
    }

    public int updateLoginTime(String userId) {
        return userMapper.updateLoginTime(userId);
    }
}
