#!/bin/bash

# 변수 설정
DEPLOY_SERVER_IP="${DEPLOY_SERVER_IP}"
IMAGE_NAME="${IMAGE_NAME}"
IMAGE_VERSION="${IMAGE_VERSION}"
DEPLOY_PATH="${DEPLOY_PATH}"
APP_USERNAME="${APP_USERNAME}"
APP_PASSWORD="${APP_PASSWORD}"


# 현재 실행 중인 컨테이너 확인
CURRENT_CONTAINER=$(docker ps --format '{{.Names}}' | grep -E 'blue|green')

if [[ "$CURRENT_CONTAINER" == *"blue"* ]]; then
  NEW_CONTAINER="green"
  NEW_PORT=8082
  OLD_PORT=8081
elif [[ "$CURRENT_CONTAINER" == *"green"* ]]; then
  NEW_CONTAINER="blue"
  NEW_PORT=8081
  OLD_PORT=8082
else
  echo "No running container found. Defaulting to blue."
  NEW_CONTAINER="blue"
  NEW_PORT=8081
  OLD_PORT=8082
fi

echo "Current container: $CURRENT_CONTAINER"
echo "New container: $NEW_CONTAINER"
echo "New port: $NEW_PORT"
echo "Old port: $OLD_PORT"

# 새로운 컨테이너 실행
echo "Running new container: $NEW_CONTAINER"
docker run -d --name $NEW_CONTAINER -e PROFILE=$NEW_CONTAINER -e PORT=8080 -p $NEW_PORT:8080 $IMAGE_NAME:$IMAGE_VERSION || { echo "Docker run failed"; exit 1; }

# 새로운 서버의 헬스 체크
echo "Waiting for new server to be up..."
sleep 60

# 새로운 컨테이너의 로그 출력
echo "Fetching logs for new container: $NEW_CONTAINER"
docker logs $NEW_CONTAINER

# 헬스 체크 URL
HEALTH_CHECK_URL="http://$DEPLOY_SERVER_IP:$NEW_PORT/actuator/health"

echo "Performing health check on $HEALTH_CHECK_URL"
HEALTH_CHECK_STATUS=$(curl -s -o /dev/null -w "%{http_code}" -H "username: $APP_USERNAME" $HEALTH_CHECK_URL)

if [ "$HEALTH_CHECK_STATUS" -ne 200 ]; then
    echo "New server health check failed with status code $HEALTH_CHECK_STATUS"
    echo "Rolling back to previous state..."

    # 새로운 컨테이너 중지 및 제거
    docker stop $NEW_CONTAINER || true
    docker rm $NEW_CONTAINER || true

    echo "Rollback completed. Previous container is still running."
    exit 1
fi

# Nginx 설정 업데이트
NGINX_CONFIG_FILE="/etc/nginx/sites-enabled/default"
if [ "$NEW_CONTAINER" == "blue" ]; then
    sudo sed -i 's/proxy_pass http:\/\/green;/proxy_pass http:\/\/blue;/' $NGINX_CONFIG_FILE
    sudo sed -i -E '/location \/api\/v1\/notifications\/subscribe/,/}/ s#(proxy_pass)[[:space:]]+http://green;#\1 http://blue;#' $NGINX_CONFIG_FILE
    sudo sed -i 's/proxy_pass http:\/\/green\/actuator\/health;/proxy_pass http:\/\/blue\/actuator\/health;/' $NGINX_CONFIG_FILE
else
    sudo sed -i 's/proxy_pass http:\/\/blue;/proxy_pass http:\/\/green;/' $NGINX_CONFIG_FILE
    sudo sed -i -E '/location \/api\/v1\/notifications\/subscribe/,/}/ s#(proxy_pass)[[:space:]]+http://blue;#\1 http://green;#' $NGINX_CONFIG_FILE
    sudo sed -i 's/proxy_pass http:\/\/blue\/actuator\/health;/proxy_pass http:\/\/green\/actuator\/health;/' $NGINX_CONFIG_FILE
fi

echo "Reloading Nginx"
sudo nginx -s reload || { echo "Failed to reload Nginx"; exit 1; }

# 기존 컨테이너 종료
if [ -n "$CURRENT_CONTAINER" ]; then
    echo "Stopping old container: $CURRENT_CONTAINER"
    docker stop $CURRENT_CONTAINER || true

    echo "Removing old container: $CURRENT_CONTAINER"
    docker rm $CURRENT_CONTAINER || true
fi

echo "Deployment to $NEW_CONTAINER completed."
