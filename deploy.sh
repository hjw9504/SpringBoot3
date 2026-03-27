#!/bin/bash

# --- 설정 변수 ---
PEM_PATH="~/Desktop/aws-key.pem"
EC2_USER="ubuntu"
EC2_IP="54.180.225.237"
SERVER_DIR="/home/ubuntu"
JAR_FILE="build/libs/auth-server.jar" # Gradle 빌드 결과물 위치

echo "[1/5] 로컬 빌드 시작 (./gradlew clean build)..."
./gradlew clean build -x test

if [ $? -ne 0 ]; then
    echo "빌드 실패! 스크립트를 종료합니다."
    exit 1
fi

echo "[2/5] JAR 파일을 서버로 전송 중..."
scp -i $PEM_PATH $JAR_FILE $EC2_USER@$EC2_IP:$SERVER_DIR/auth-server.jar

echo "[3/5] 기존 서버 프로세스 종료 중..."
ssh -i $PEM_PATH $EC2_USER@$EC2_IP "pgrep -f *.jar | xargs -r kill -9"

echo "[4/5] 새 버전 서버 실행 (nohup)..."
ssh -i $PEM_PATH $EC2_USER@$EC2_IP "nohup java -Dspring.profiles.active=prod -jar $SERVER_DIR/auth-server.jar > $SERVER_DIR/nohup.out 2>&1 &"

echo "[5/5] 배포가 완료되었습니다!"
echo "실시간 로그 확인: ssh -i $PEM_PATH $EC2_USER@$EC2_IP 'tail -f $SERVER_DIR/nohup.out'"
