#!/bin/bash

echo "🔍 Запуск тестов Ktor..."

# Убедимся, что зависимости загружены и тесты проходят
./gradlew test --no-daemon
