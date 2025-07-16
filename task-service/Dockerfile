# Используем официальный образ Maven для сборки приложения
FROM maven:3.9.4-eclipse-temurin-17 AS build

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем pom.xml для загрузки зависимостей
COPY pom.xml .

# Загружаем зависимости (кэшируется если pom.xml не изменился)
RUN mvn dependency:go-offline -B

# Копируем исходный код
COPY src ./src

# Собираем приложение
RUN mvn clean package -DskipTests

# Используем JRE образ для запуска приложения
FROM eclipse-temurin:17-jre-jammy

# Создаем пользователя для запуска приложения (безопасность)
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Создаем директорию для приложения
WORKDIR /app

# Копируем JAR файл из стадии сборки
COPY --from=build /app/target/task-service-1.0.0.jar app.jar

# Изменяем владельца файлов
RUN chown -R appuser:appuser /app

# Переключаемся на пользователя appuser
USER appuser

# Открываем порт 8082
EXPOSE 8082

# Переменные окружения
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV SPRING_PROFILES_ACTIVE=docker

# Команда запуска приложения
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"] 