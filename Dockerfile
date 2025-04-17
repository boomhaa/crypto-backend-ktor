# Используем образ с JDK 17
FROM eclipse-temurin:17-jdk-jammy as builder

# Рабочая директория
WORKDIR /app

# Копируем Gradle файлы для кэширования
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY src src

# Запускаем сборку проекта с созданием shadow JAR
RUN chmod +x gradlew && ./gradlew shadowJar

# Финальный образ
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Копируем собранный JAR из builder
COPY --from=builder /app/build/libs/crypto-backend-ktor.jar .
COPY .env .

# Команда для запуска приложения
CMD ["java", "-jar", "crypto-backend-ktor.jar"]