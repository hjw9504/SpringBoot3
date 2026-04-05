#!/bin/bash

PROFILE=${1:-prod}

# --- 설정 변수 ---
PEM_PATH="~/Desktop/aws-key.pem"
EC2_USER="ubuntu"
EC2_IP="54.180.225.237"
SERVER_DIR="/home/ubuntu"
JAR_FILE="build/libs/auth-server.jar" # Gradle 빌드 결과물 위치
echo ">>> 현재 배포 환경: ${PROFILE}"

echo "[1/2] 새 버전 서버 실행 (nohup)..."
ssh -i $PEM_PATH $EC2_USER@$EC2_IP "./deploy-spring.sh"

echo "[2/2] 배포가 완료되었습니다!"
echo "실시간 로그 확인: ssh -i $PEM_PATH $EC2_USER@$EC2_IP 'tail -f $SERVER_DIR/nohup.out'"
