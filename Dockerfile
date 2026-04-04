# 1. 빌드 스테이지: Gradle을 사용하여 JAR 파일 생성
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Gradle 래퍼와 설정 파일들을 먼저 복사 (라이브러리 캐싱 활용)
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# 종속성 라이브러리 미리 다운로드
RUN ./gradlew dependencies --no-daemon

# 전체 소스 복사 및 빌드 (테스트는 제외하여 빌드 속도 향상)
COPY src src
RUN ./gradlew clean bootJar -x test --no-daemon

# 2. 실행 스테이지: 가벼운 JRE 환경에서 실행
FROM eclipse-temurin:21-jre
WORKDIR /app

# 빌드 스테이지에서 생성된 실행 가능한 JAR 파일만 복사
COPY --from=build /app/build/libs/auth-server.jar app.jar

# 컨테이너 실행 명령 (메모리 옵션은 서버 사양에 맞춰 조절)
ENTRYPOINT ["java", "-Xms512m", "-Xmx512m", "-jar", "app.jar"]