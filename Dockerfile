FROM openjdk:11-jdk-slim

WORKDIR /opt/bardybot

COPY build/libs/BardyBot-*.jar BardyBot.jar
COPY src/main/resources/application.yml application.yml

ENTRYPOINT ["java", "-jar", "BardyBot.jar"]