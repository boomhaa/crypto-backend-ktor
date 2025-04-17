# Используем образ с JDK 17 в качестве базового
FROM eclipse-temurin:17-jdk-jammy

# Создаем рабочую директорию
WORKDIR /app

# Копируем собранный JAR-файл в контейнер
COPY crypto-backend-ktor.jar /app/crypto-backend-ktor.jar

# Копируем .env файл (если нужно)
COPY .env /app/.env

# Открываем порт, на котором работает приложение
EXPOSE 8080

# Команда для запуска приложения
ENTRYPOINT ["java", "-jar", "crypto-backend-ktor.jar"]