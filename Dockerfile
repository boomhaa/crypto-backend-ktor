FROM gradle:8.5-jdk17 AS build

WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew clean shadowJar --no-daemon

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar crypto-backend-ktor.jar
COPY .env .env
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "crypto-backend-ktor.jar"]