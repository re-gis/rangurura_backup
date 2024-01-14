FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY target\rangurura-0.0.1-SNAPSHOT.jar /app/app.jar


CMD ["java", "-jar", "app.jar"]

EXPOSE 5000

