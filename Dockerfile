# JDK17 이미지 사용
FROM openjdk:17-jdk

VOLUME /tmp

# 로컬 JAR 파일 경로와 일치하도록 수정
COPY app.jar /app

# 빌드된 이미지가 run될 때 실행할 명령어 -> 프로파일을 인자로 받아 실행 시 설정
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT} -Dspring.profiles.active=${PROFILE} -Duser.timezone=Asia/Seoul -jar /app/youth-talk-talk-0.0.1-SNAPSHOT.jar"]
