FROM openjdk:11-jdk-slim

WORKDIR /app

COPY build/libs/*.jar /app/app.jar

CMD ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]