# Build stage
FROM gradle:7.6-jdk17 AS build
WORKDIR /app

# Копируем только необходимые для сборки файлы
COPY build.gradle.kts settings.gradle.kts /app/
COPY src /app/src

# Запускаем сборку с кэшированием зависимостей
RUN gradle --no-daemon dependencies
RUN gradle shadowJar --no-daemon --stacktrace --info

# Run stage
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/build/libs/crypto-backend-ktor.jar /app/
COPY .env /app/

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "crypto-backend-ktor.jar"]