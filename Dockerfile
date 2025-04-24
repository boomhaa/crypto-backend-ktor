FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY crypto-backend-ktor.jar /app/crypto-backend-ktor.jar

COPY .env /app/.env

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "crypto-backend-ktor.jar"]