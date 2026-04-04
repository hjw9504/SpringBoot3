package com.example.jdkproject.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD) // 메소드에만 붙일 수 있게 설정
@Retention(RetentionPolicy.RUNTIME) // 실행 중(Runtime)에도 정보가 유지되도록 설정
@Documented
public @interface TokenCheck {
    String value() default "Default Action"; // 속성 추가 가능
}