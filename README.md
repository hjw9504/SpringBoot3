# Auth Server - Spring Boot 3 인증 서버

Spring Boot 3 기반의 인증/소셜 기능 백엔드 서버입니다. JWT 인증, OAuth2/OIDC(카카오) 연동, 게시글·댓글·좋아요·팔로우·실시간 채팅·AI 통합 기능을 제공합니다.

---

## 기술 스택

| 분류 | 기술 |
|------|------|
| Language | Java 21 |
| Framework | Spring Boot 3.1.0 |
| ORM | Spring Data JPA (Hibernate) |
| Database | MySQL |
| Cache | Redis (Lettuce) |
| Message Queue | Apache Kafka |
| HTTP Client | OpenFeign + Apache HttpClient |
| Auth | JWT (JJWT 0.12.3), RSA-2048, RS512 |
| OAuth | Kakao OAuth2 / OIDC 1.0 |
| Real-time | WebSocket (STOMP + SockJS) |
| AI | Google Gemini API |
| Build | Gradle |
| Container | Docker (멀티 스테이지 빌드) |
| CI/CD | GitHub Actions + AWS EC2 |

---

## 프로젝트 구조

```
src/main/java/com/example/jdkproject/
├── JdkTestApplication.java          # 메인 진입점
├── client/                          # Feign HTTP 클라이언트
│   ├── GeminiClient.java            # Gemini AI 연동
│   ├── KakaoAuthClient.java         # 카카오 OAuth 인증
│   ├── KakaoOidcClient.java         # 카카오 OIDC 사용자 정보
│   └── OAuth2Client.java            # 범용 OAuth2 클라이언트
├── config/                          # 설정 클래스
│   ├── RedisConfig.java             # Redis/Lettuce 설정
│   ├── SecurityConfig.java          # Spring Security 설정
│   └── WebSocketConfig.java         # WebSocket STOMP 설정
├── controller/                      # REST API 컨트롤러
│   ├── UserController.java          # 사용자/인증 API
│   ├── OAuthController.java         # OAuth API
│   ├── PostController.java          # 게시글 API
│   ├── CommentController.java       # 댓글 API
│   ├── FollowController.java        # 팔로우 API
│   ├── ChattingController.java      # 채팅 REST + WebSocket API
│   ├── AiController.java            # AI API
│   └── ControllerAdvice.java        # 전역 예외 처리
├── domain/                          # 도메인 모델 (응답용 객체)
│   ├── Member.java                  # 사용자 정보
│   ├── Posting.java                 # 게시글 정보
│   ├── Response.java                # 공통 응답 래퍼
│   ├── KakaoOAuthRequest/Response   # 카카오 OAuth 모델
│   └── KakaoOIDCResponse.java       # 카카오 OIDC 모델
├── dto/                             # Data Transfer Object
├── entity/                          # JPA 엔티티 (Vo 접미사)
├── enums/                           # 열거형
├── exception/                       # 예외 처리 (ErrorStatus)
├── repository/                      # 데이터 접근 계층
└── service/                         # 비즈니스 로직
```

---

## 환경 설정

### 애플리케이션 포트
- **dev**: 8081
- **prod**: 8080

### 필수 인프라

| 서비스 | 개발 환경 | 운영 환경 |
|--------|----------|----------|
| MySQL | localhost:3306 / DB: TEST | EC2:3306 / DB: USER |
| Redis | 127.0.0.1:6379 | 127.0.0.1:6379 |
| Kafka | localhost:9092 | localhost:9092 |

### 프로파일 구성
```
application.properties               # 공통 설정
application-dev.properties           # 개발 환경
application-prod.properties          # 운영 환경
application-secret-dev.properties    # 개발 시크릿 (버전 관리 제외)
application-secret-prod.properties   # 운영 시크릿 (버전 관리 제외)
```

### 프로파일 전환
```bash
# 개발 환경
java -jar auth-server.jar --spring.profiles.active=dev

# 운영 환경
java -jar auth-server.jar --spring.profiles.active=prod
```

---

## API 명세

### 공통 응답 포맷

모든 REST 응답은 아래 포맷을 사용합니다. 필드명은 snake_case로 직렬화됩니다.

```json
{
  "result_data": {},
  "status": "OK",
  "error_code": 0,
  "message": null
}
```

- `result_data`: 응답 데이터 (null이면 필드 생략)
- `status`: HTTP 상태 (`"OK"`, `"BAD_REQUEST"`, `"NOT_FOUND"` 등)
- `error_code`: 0 = 성공, 그 외 = 에러 코드
- `message`: 에러 메시지 (성공 시 필드 생략)

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
| POST | `/update/nickname` | O | 닉네임 변경 (1일 1회 제한) |

#### 회원가입 요청
```http
POST /user/register
Content-Type: application/json

{
  "userId": "user1",
  "userPw": "password123",
  "name": "홍길동",
  "email": "user@example.com",
  "phone": "01012345678",
  "nickname": "길동이"
}
```

#### 회원가입 응답
```json
{
  "result_data": "success",
  "status": "OK",
  "error_code": 0
}
```

#### 로그인 요청
```http
POST /user/login
Content-Type: application/json

{
  "userId": "user1",
  "userPw": "password123"
}
```

#### 로그인 응답
```json
{
  "result_data": {
    "memberId": "550e8400-e29b-41d4-a716-446655440000",
    "userId": "user1",
    "name": "홍길동",
    "nickName": "길동이",
    "email": "암호화된_이메일",
    "phone": "암호화된_전화번호",
    "role": "USER",
    "token": "<JWT_TOKEN>"
  },
  "status": "OK",
  "error_code": 0
}
```

#### 사용자 정보 조회 요청
```http
GET /user/info?id=user1&member_id=550e8400-e29b-41d4-a716-446655440000
token: <JWT_TOKEN>
```

#### 사용자 정보 조회 응답
```json
{
  "result_data": [
    {
      "memberId": "550e8400-e29b-41d4-a716-446655440000",
      "userId": "user1",
      "name": "홍길동",
      "nickName": "길동이",
      "role": "USER",
      "profileImage": "https://example.com/image.jpg",
      "registerTime": "2024-01-01T00:00:00",
      "recentLoginTime": "2024-06-01T12:00:00"
    }
  ],
  "status": "OK",
  "error_code": 0
}
```

#### 아이디 중복 확인 응답
```json
{
  "result_data": false,
  "status": "OK",
  "error_code": 0
}
```
> `false` = 사용 가능, `true` = 이미 존재

#### IDP 로그인 요청
```http
POST /user/idp/login
Content-Type: application/json

{
  "accessToken": "<IDP_ACCESS_TOKEN>",
  "idpType": "kakao"
}
```

#### 비밀번호 초기화 요청
```http
POST /reset/password
Content-Type: application/json

{
  "userId": "user1",
  "newUserPw": "newPassword123"
}
```

#### 닉네임 변경 요청
```http
POST /update/nickname
token: <JWT_TOKEN>
Content-Type: application/json

{
  "memberId": "550e8400-e29b-41d4-a716-446655440000",
  "nickName": "새닉네임"
}
```

---

### OAuth API

| Method | URL | 인증 | 설명 |
|--------|-----|------|------|
| GET | `/oauth/{idpType}` | - | OAuth 인증 URL 리다이렉트 |
| GET | `/oauth/callback/{idpType}` | - | OAuth 콜백 처리 후 프론트로 리다이렉트 |
| POST | `/oauth/token/verify/{idpType}` | - | IDP 액세스 토큰 검증 |
| POST | `/oauth/check/idp/register` | - | IDP 사용자 가입 여부 확인 |

- `idpType`: `kakao`, `google`, `oauth2`

#### IDP 토큰 검증 요청
```http
POST /oauth/token/verify/kakao
Content-Type: application/json

{
  "accessToken": "<KAKAO_ACCESS_TOKEN>",
  "idpType": "kakao"
}
```

#### IDP 토큰 검증 응답
```json
{
  "result_data": {
    "idpUserId": "kakao_12345678",
    "idpType": "kakao"
  },
  "status": "OK",
  "error_code": 0
}
```

#### IDP 가입 여부 확인 응답
```json
{
  "result_data": true,
  "status": "OK",
  "error_code": 0
}
```
> `true` = 이미 가입된 IDP 사용자

---

### 게시글 API

| Method | URL | 인증 | 설명 |
|--------|-----|------|------|
| GET | `/posting/all` | O | 전체 게시글 목록 조회 (좋아요 여부 포함) |
| GET | `/posting/list` | O | 내 게시글 목록 조회 |
| GET | `/posting/detail/{postingId}` | O | 게시글 상세 조회 |
| POST | `/posting/register` | O | 게시글 등록 (`id=0`) / 수정 (`id!=0`) |
| DELETE | `/posting/{postingId}` | O | 게시글 삭제 |
| GET | `/posting/likes/{postingId}` | O | 게시글 좋아요 목록 조회 |
| POST | `/posting/likes/{type}/{postingId}` | O | 좋아요 추가(`i`) / 삭제(`d`) |

#### 전체 게시글 목록 조회 응답
```json
{
  "result_data": [
    {
      "id": 1,
      "title": "제목",
      "body": "본문 내용",
      "likes": 5,
      "registerTime": "2024-06-01 12:00:00",
      "commentCount": 3,
      "member": {
        "memberId": "550e8400-e29b-41d4-a716-446655440000",
        "name": "홍길동",
        "profileImage": "https://example.com/image.jpg",
        "isLikeTrue": true
      }
    }
  ],
  "status": "OK",
  "error_code": 0
}
```

#### 게시글 상세 조회 응답
```json
{
  "result_data": {
    "id": 1,
    "memberId": "550e8400-e29b-41d4-a716-446655440000",
    "title": "제목",
    "body": "본문 내용",
    "profileImage": "https://example.com/image.jpg",
    "registerTime": "2024-06-01 12:00:00",
    "modTime": "2024-06-02 10:00:00",
    "name": "홍길동",
    "likes": 5
  },
  "status": "OK",
  "error_code": 0
}
```

#### 게시글 등록/수정 요청
```http
POST /posting/register
token: <JWT_TOKEN>
Content-Type: application/json

{
  "id": 0,
  "memberId": "550e8400-e29b-41d4-a716-446655440000",
  "title": "새 게시글 제목",
  "body": "게시글 내용입니다."
}
```
> `id=0` → 신규 등록, `id!=0` → 수정

#### 좋아요 목록 조회 응답
```json
{
  "result_data": [
    {
      "memberId": "550e8400-e29b-41d4-a716-446655440000",
      "postingId": 1,
      "userId": "user1",
      "profileImage": "https://example.com/image.jpg"
    }
  ],
  "status": "OK",
  "error_code": 0
}
```

---

### 댓글 API

| Method | URL | 인증 | 설명 |
|--------|-----|------|------|
| GET | `/comment/{postingId}` | O | 게시글 댓글 전체 조회 |
| POST | `/comment/{postingId}` | O | 댓글 등록 |

#### 댓글 조회 응답
```json
{
  "result_data": {
    "posting_id": 1,
    "comments": [
      {
        "member_id": "550e8400-e29b-41d4-a716-446655440000",
        "comment": "댓글 내용입니다.",
        "register_time": "2024-06-01 13:00:00",
        "profileImage": "https://example.com/image.jpg"
      }
    ]
  },
  "status": "OK",
  "error_code": 0
}
```

#### 댓글 등록 요청
```http
POST /comment/1
token: <JWT_TOKEN>
Content-Type: application/json

{
  "comment": "댓글 내용입니다."
}
```

---

### 팔로우 API

| Method | URL | 인증 | 설명 |
|--------|-----|------|------|
| GET | `/follow/friends/list` | O | 팔로워 목록 조회 |
| POST | `/follow` | O | 팔로우 |
| POST | `/follow/unlink` | O | 언팔로우 |

#### 팔로워 목록 조회 응답
```json
{
  "result_data": [
    {
      "member_id": "550e8400-e29b-41d4-a716-446655440000",
      "follow_member_id": "660f9500-f30c-52e5-b827-557766551111",
      "user_id": "user1",
      "follow_user_id": "user2",
      "register_time": "2024-01-01T00:00:00",
      "followed_time": "2024-06-01T12:00:00",
      "profile_image": "https://example.com/image1.jpg",
      "follow_profile_image": "https://example.com/image2.jpg"
    }
  ],
  "status": "OK",
  "error_code": 0
}
```

#### 팔로우/언팔로우 요청
```http
POST /follow
token: <JWT_TOKEN>
Content-Type: application/json

{
  "follow_member_id": "660f9500-f30c-52e5-b827-557766551111"
}
```

---

### AI API

| Method | URL | 인증 | 설명 |
|--------|-----|------|------|
| POST | `/ai/chat` | O | Gemini AI 응답 요청 |

#### AI 채팅 요청
```http
POST /ai/chat
token: <JWT_TOKEN>
Content-Type: application/json

{
  "message": "스프링 부트가 뭔가요?"
}
```

#### AI 채팅 응답
```json
{
  "result_data": "스프링 부트는 스프링 프레임워크 기반의 ...",
  "status": "OK",
  "error_code": 0
}
```

---

### 채팅 REST API

| Method | URL | 인증 | 설명 |
|--------|-----|------|------|
| GET | `/chat/room` | O | 채팅방 목록 조회 |
| POST | `/chat/room` | O | 채팅방 생성 |
| GET | `/chat/message` | O | 채팅 메시지 조회 |

#### 채팅방 목록 조회 응답
```json
{
  "result_data": [
    {
      "room_id": 1,
      "name": "채팅방 이름",
      "register_time": "2024-06-01T12:00:00"
    }
  ],
  "status": "OK",
  "error_code": 0
}
```

#### 채팅방 생성 요청
```http
POST /chat/room
token: <JWT_TOKEN>
Content-Type: application/json

{
  "room_name": "새 채팅방",
  "member_id": "550e8400-e29b-41d4-a716-446655440000"
}
```

#### 채팅 메시지 조회 요청/응답
```http
GET /chat/message?room_id=1
token: <JWT_TOKEN>
```
```json
{
  "result_data": [
    {
      "roomId": 1,
      "memberId": "550e8400-e29b-41d4-a716-446655440000",
      "message": "안녕하세요!",
      "registerTime": "2024-06-01T12:00:00",
      "userId": "user1",
      "sender": "홍길동"
    }
  ],
  "status": "OK",
  "error_code": 0
}
```

---

### 채팅 WebSocket API

STOMP 프로토콜 기반 실시간 채팅입니다.

#### 연결
```
ws://localhost:8080/ws-chat
```
> SockJS 지원: `http://localhost:8080/ws-chat` (SockJS 클라이언트 사용 시)

#### 메시지 발행 (클라이언트 → 서버)
```
SEND destination:/pub/message
```

#### 메시지 구독 (서버 → 클라이언트)
```
SUBSCRIBE /sub/chat/room/{roomId}
```

#### WebSocket 메시지 포맷
```json
{
  "type": "ENTER",
  "room_id": 1,
  "room_name": "채팅방 이름",
  "member_id": "550e8400-e29b-41d4-a716-446655440000",
  "token": "<JWT_TOKEN>",
  "sender": "홍길동",
  "message": "홍길동님이 입장하셨습니다."
}
```

| type | 설명 |
|------|------|
| `ENTER` | 채팅방 입장 |
| `TALK` | 메시지 전송 |
| `LEAVE` | 채팅방 퇴장 |

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
         ← OIDC로 사용자 정보 조회 후 프론트로 리다이렉트
               (access_token, idp_type 쿼리 파라미터 포함)
프론트   → POST /user/idp/login (access_token으로 JWT 발급)
```

---

## 보안

- **비밀번호**: SHA-256 해싱
- **개인정보(이메일, 전화번호)**: 사용자별 RSA-2048 공개키로 암호화
- **JWT**: RS512 서명, 유효시간 60분, Redis JTI 검증
- **RSA 키**: 사용자별 키 쌍을 `member_secure` 테이블에 저장

---

## 데이터베이스 테이블

| 테이블 | 설명 |
|--------|------|
| `member` | 사용자 계정 |
| `member_secure` | 사용자별 RSA 키 쌍 |
| `member_channel` | IDP 계정 연결 정보 |
| `member_follower` | 팔로우 관계 |
| `posting` | 게시글 |
| `posting_comment` | 댓글 |
| `posting_likes_log` | 좋아요 이력 |
| `idp_oauth` | OAuth 공급자 설정 |
| `tb_chat_room` | 채팅방 |
| `tb_chat_room_member` | 채팅방 멤버 |
| `chat_log` | 채팅 메시지 이력 |

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
| 400 | 108 | 잘못된 요청 |
| 400 | 110 | 등록되지 않은 IDP 사용자 |
| 400 | 201 | 토큰 검증 실패 |
| 400 | 202 | 토큰 파싱 오류 |
| 400 | 301 | 게시글 등록 실패 |
| 400 | 310 | 이미 팔로우한 사용자 |
| 500 | 501 | 서버 오류 |

#### 에러 응답 예시
```json
{
  "status": "BAD_REQUEST",
  "error_code": 201,
  "message": "TOKEN VERIFY FAIL"
}
```

---

## Kafka 메시지

로그인 성공 시 `auth_login` 토픽으로 아래 정보를 발행합니다.

```json
{
  "memberId": "550e8400-e29b-41d4-a716-446655440000",
  "name": "홍길동"
}
```

- Consumer Group: `auth_group`
- Auto Offset Reset: `earliest`

---

## 빌드 및 실행

```bash
# 빌드 (테스트 제외)
./gradlew clean build -x test

# 실행 (dev)
java -jar build/libs/auth-server.jar --spring.profiles.active=dev

# 실행 (prod)
java -jar build/libs/auth-server.jar --spring.profiles.active=prod
```

---

## Docker

멀티 스테이지 빌드를 사용합니다.

```bash
# 이미지 빌드
docker build -t auth-server .

# 컨테이너 실행
docker run -p 8080:8080 auth-server
```

**JVM 메모리 설정**: `-Xms512m -Xmx512m`

---

## CI/CD

GitHub Actions를 통해 `develop` 브랜치 푸시 시 AWS EC2에 자동 배포됩니다.

**파이프라인 단계:**
1. JDK 21 설정
2. Gradle 빌드 (`./gradlew clean build -x test`)
3. SSH로 EC2 접속 및 배포 스크립트 실행

**필요한 GitHub Secrets:**
- `EC2_HOST` - EC2 퍼블릭 DNS
- `EC2_USERNAME` - SSH 사용자명
- `EC2_SSH_KEY` - SSH 개인키
