# Build stage
FROM gradle:7.6-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle shadowJar --no-daemon

# Run stage
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the built jar
COPY --from=build /app/build/libs/crypto-backend-ktor.jar /app/crypto-backend-ktor.jar
# Copy .env file (will be generated during CI/CD)
COPY .env /app/.env

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "crypto-backend-ktor.jar"]