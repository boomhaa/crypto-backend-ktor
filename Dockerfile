# Build stage
FROM gradle:7.6-jdk17 AS build
WORKDIR /app
COPY . .
# Добавляем разрешение на запись в папку gradle
USER root
RUN chown -R gradle:gradle /app
USER gradle
# Запускаем сборку с выводом логов
RUN gradle shadowJar --no-daemon --stacktrace --info

# Run stage
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the built jar
COPY --from=build /app/build/libs/crypto-backend-ktor.jar /app/crypto-backend-ktor.jar
# Copy .env file (will be generated during CI/CD)
COPY .env /app/.env

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "crypto-backend-ktor.jar"]