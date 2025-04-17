FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build/libs/crypto-backend-ktor.jar crypto-backend-ktor.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "crypto-backend-ktor.jar"]
