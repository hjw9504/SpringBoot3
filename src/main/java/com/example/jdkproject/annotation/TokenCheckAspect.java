package com.example.jdkproject.annotation;

import com.example.jdkproject.dto.JtiInfo;
import com.example.jdkproject.exception.CommonErrorException;
import com.example.jdkproject.exception.ErrorStatus;
import com.example.jdkproject.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RequiredArgsConstructor
@Aspect
@Component
@Slf4j
public class TokenCheckAspect {

    private final UserService userService;

    // @MyLog 어노테이션이 붙은 메소드 실행 전후에 동작
    @Around("@annotation(tokenCheck)")
    public Object tokenCheck(ProceedingJoinPoint joinPoint, TokenCheck tokenCheck) throws Throwable {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                    .currentRequestAttributes()).getRequest();
            String token = request.getHeader("access_token");

            JtiInfo jtiInfo = userService.verifyToken(token);
            request.setAttribute("jtiInfo", jtiInfo);
            return joinPoint.proceed();
        } catch (CommonErrorException e) {
            throw e;
        } catch (Exception e) {
            throw new CommonErrorException(ErrorStatus.SERVER_ERROR);
        }
    }
}