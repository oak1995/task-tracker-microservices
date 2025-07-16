# Dockerfile для Gateway Service
# Многоступенчатая сборка для оптимизации размера контейнера

# Этап 1: Сборка приложения
FROM maven:3.9.8-eclipse-temurin-17 AS build

# Установка рабочей директории
WORKDIR /app

# Копирование файлов проекта
COPY pom.xml .
COPY src ./src

# Сборка приложения (пропуск тестов для быстроты)
RUN mvn clean package -DskipTests

# Этап 2: Создание runtime контейнера
FROM eclipse-temurin:17-jre-alpine

# Установка дополнительных пакетов
RUN apk add --no-cache \
    curl \
    tzdata \
    && cp /usr/share/zoneinfo/Asia/Almaty /etc/localtime \
    && echo "Asia/Almaty" > /etc/timezone

# Создание пользователя для безопасности
RUN addgroup -g 1001 -S gateway && \
    adduser -S gateway -u 1001 -G gateway

# Установка рабочей директории
WORKDIR /app

# Копирование JAR файла из этапа сборки
COPY --from=build /app/target/gateway-service-*.jar app.jar

# Создание директории для логов
RUN mkdir -p /app/logs && \
    chown -R gateway:gateway /app

# Переключение на пользователя gateway
USER gateway

# Экспорт портов
EXPOSE 8080

# Настройка JVM параметров
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Настройка Spring профилей
ENV SPRING_PROFILES_ACTIVE=docker

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Команда запуска
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

# Метаданные
LABEL maintainer="orazb@tasktracker.com"
LABEL version="1.0.0"
LABEL description="Gateway Service для Task Tracker системы"
LABEL service="gateway-service" 