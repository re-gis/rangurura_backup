FROM openjdk:11-jre-slim

WORKDIR /app

COPY target/RangururaApplication.jar /app/RangururaApplication.jar

CMD ["java", "-jar", "RangururaApplication.jar"]

EXPOSE 8080