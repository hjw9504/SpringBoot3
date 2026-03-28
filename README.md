# Auth Server - Spring Boot 3 인증 서버

Spring Boot 3 기반의 인증/소셜 기능 백엔드 서버입니다. JWT 인증, OAuth2/OIDC(카카오) 연동, 게시글·댓글·좋아요 기능을 제공합니다.

---

## 기술 스택

| 분류 | 기술 |
|------|------|
| Language | Java 21 |
| Framework | Spring Boot 3.1.0 |
| ORM | Spring Data JPA |
| Database | MySQL |
| Cache | Redis (Lettuce) |
| Message Queue | Apache Kafka |
| HTTP Client | OpenFeign |
| Auth | JWT (JJWT 0.12.3), RSA-2048 |
| OAuth | Kakao OAuth2 / OIDC |
| Build | Gradle |

---

## 프로젝트 구조

```
src/main/java/com/example/jdkproject/
├── JdkTestApplication.java      # 메인 진입점
├── client/                      # Feign HTTP 클라이언트
├── config/                      # Redis, Security 설정
├── controller/                  # REST API 컨트롤러
├── domain/                      # 도메인 모델
├── dto/                         # Data Transfer Object
├── entity/                      # JPA 엔티티
├── enums/                       # 열거형
├── exception/                   # 예외 처리
├── repository/                  # 데이터 접근 계층
└── service/                     # 비즈니스 로직
```

---

## 환경 설정

### 애플리케이션 포트
- **dev**: 8081
- **prod**: 8080

### 필수 인프라
| 서비스 | 기본 설정 |
|--------|----------|
| MySQL | localhost:3306 / DB: TEST |
| Redis | 127.0.0.1:6379 |
| Kafka | localhost:9092 |

### 프로파일 전환
```bash
# 개발 환경
java -jar auth-server.jar --spring.profiles.active=dev

# 운영 환경
java -jar auth-server.jar --spring.profiles.active=prod
```

---

## API 명세

> 모든 응답은 아래 공통 포맷을 사용합니다.
>
> ```json
> {
>   "resultData": {},
>   "status": "OK",
>   "errorCode": 0,
>   "message": null
> }
> ```

### 인증 헤더
보호된 엔드포인트는 요청 헤더에 JWT 토큰이 필요합니다.
```
token: <JWT_TOKEN>
```

---

### 사용자 API

| Method | URL | 인증 | 설명 |
|--------|-----|------|------|
| GET | `/` | - | 헬스체크 |
| POST | `/user/register` | - | 회원가입 |
| POST | `/user/login` | - | 로그인 (JWT 발급) |
| POST | `/user/idp/register` | - | IDP(카카오) 회원가입 |
| POST | `/user/idp/login` | - | IDP(카카오) 로그인 |
| GET | `/user/info` | O | 사용자 정보 조회 |
| GET | `/check/userId` | - | 아이디 중복 확인 |
| POST | `/user/token/verify` | O | JWT 토큰 검증 |
| POST | `/reset/password` | - | 비밀번호 초기화 |
| POST | `/update/nickname` | O | 닉네임 변경 (1일 1회) |

#### 회원가입 요청 예시
```json
POST /user/register
{
  "userId": "user1",
  "userPw": "password123",
  "name": "홍길동",
  "email": "user@example.com",
  "phone": "01012345678",
  "nickname": "길동이"
}
```

#### 로그인 응답 예시
```json
{
  "resultData": {
    "memberId": 1,
    "userId": "user1",
    "name": "홍길동",
    "token": "<JWT_TOKEN>"
  },
  "status": "OK",
  "errorCode": 0,
  "message": null
}
```

---

### OAuth API

| Method | URL | 인증 | 설명 |
|--------|-----|------|------|
| GET | `/oauth/{idpType}` | - | OAuth 인증 URL 리다이렉트 |
| GET | `/oauth/callback/{idpType}` | - | OAuth 콜백 처리 |
| POST | `/oauth/token/verify/{idpType}` | - | IDP 토큰 검증 |
| POST | `/oauth/check/idp/register` | - | IDP 사용자 가입 여부 확인 |

- `idpType`: `kakao`, `google`, `oauth2`

---

### 게시글 API

| Method | URL | 인증 | 설명 |
|--------|-----|------|------|
| GET | `/posting/all` | O | 전체 게시글 목록 조회 (좋아요 여부 포함) |
| GET | `/posting/list` | O | 특정 사용자의 게시글 목록 조회 |
| GET | `/posting/detail/{postingId}` | O | 게시글 상세 조회 |
| POST | `/posting/register` | O | 게시글 등록/수정 |
| GET | `/posting/likes/{postingId}` | O | 게시글 좋아요 목록 조회 |
| POST | `/posting/likes/{type}/{postingId}` | O | 좋아요 추가(`i`) / 삭제(`d`) |

---

### 댓글 API

| Method | URL | 인증 | 설명 |
|--------|-----|------|------|
| GET | `/comment/{postingId}` | O | 게시글 댓글 전체 조회 |
| POST | `/comment/{postingId}` | O | 댓글 등록 |

---

## 인증 흐름

### 일반 로그인
```
클라이언트 → POST /user/login
         ← JWT 토큰 (RS512, 유효시간 60분)
         → 이후 요청에 token 헤더 첨부
```

### OAuth (카카오) 로그인
```
클라이언트 → GET /oauth/kakao
         ← 카카오 인증 페이지로 리다이렉트
카카오   → GET /oauth/callback/kakao?code=...
         ← OIDC로 사용자 정보 조회 및 JWT 발급
```

---

## 보안

- **비밀번호**: SHA-256 해싱
- **개인정보(이메일, 전화번호)**: 사용자별 RSA-2048 공개키로 암호화
- **JWT**: RS512 서명, 유효시간 60분, Redis JTI 검증

---

## 데이터베이스 테이블

| 테이블 | 설명 |
|--------|------|
| `member` | 사용자 계정 |
| `member_secure` | 사용자별 RSA 키 |
| `member_channel` | IDP 사용자 연결 정보 |
| `posting` | 게시글 |
| `posting_comment` | 댓글 |
| `posting_likes_log` | 좋아요 이력 |
| `idp_oauth` | OAuth 공급자 설정 |

---

## 에러 코드

| HTTP | 에러코드 | 설명 |
|------|----------|------|
| 404 | 101 | 리소스를 찾을 수 없음 |
| 400 | 102 | 이미 존재하는 리소스 |
| 400 | 103 | 필수 파라미터 누락 |
| 400 | 105 | 잘못된 비밀번호 |
| 400 | 106 | 닉네임 변경 불가 (1일 1회 제한) |
| 400 | 107 | 동일한 비밀번호 |
| 400 | 110 | 등록되지 않은 IDP 사용자 |
| 400 | 201 | 토큰 검증 실패 |
| 400 | 202 | 토큰 파싱 오류 |
| 400 | 301 | 게시글 등록 실패 |
| 500 | 501 | 서버 오류 |

---

## 빌드 및 실행

```bash
# 빌드
./gradlew build

# 실행 (dev)
java -jar build/libs/auth-server.jar --spring.profiles.active=dev

# 실행 (prod)
java -jar build/libs/auth-server.jar --spring.profiles.active=prod
```

---

## Kafka 메시지

로그인 성공 시 `auth_login` 토픽으로 아래 정보를 발행합니다.

```json
{
  "memberId": 1,
  "name": "홍길동"
}
```
