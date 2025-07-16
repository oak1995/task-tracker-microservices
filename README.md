# Task Tracker Microservices

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.0-brightgreen)
![Docker](https://img.shields.io/badge/Docker-Compose-blue)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue)
![Redis](https://img.shields.io/badge/Redis-7-red)
![Apache Kafka](https://img.shields.io/badge/Apache%20Kafka-3.4-black)

Микросервисная архитектура для управления задачами, построенная на Spring Boot с использованием современных технологий.

## 🏗️ Архитектура

Проект состоит из 5 микросервисов, каждый с отдельной базой данных:

### Сервисы:
- **🔐 Auth Service** (8081) - Аутентификация и авторизация пользователей
- **📝 Task Service** (8082) - Управление задачами и проектами
- **🚪 Gateway Service** (8080) - API Gateway и маршрутизация
- **📊 Audit Service** (8083) - Аудит действий пользователей
- **🔔 Notification Service** (8084) - Система уведомлений

### Инфраструктура:
- **PostgreSQL** - Базы данных для каждого сервиса
- **Redis** - Кэширование и сессии
- **Apache Kafka** - Асинхронная обработка событий
- **Eureka Server** - Service Discovery

## 🚀 Быстрый старт

### Предварительные требования
- Java 17+
- Maven 3.8+
- Docker и Docker Compose

### Запуск приложения

1. **Клонируйте репозиторий:**
```bash
git clone https://github.com/oak1995/task-tracker-microservices.git
cd task-tracker-microservices
```

2. **Создайте файл окружения:**
```bash
cp docker.env.example docker.env
```

3. **Запустите все сервисы:**
```bash
docker-compose up -d
```

4. **Проверьте состояние сервисов:**
```bash
docker-compose ps
```

### Доступ к приложению

- **Frontend**: [http://localhost:3000](http://localhost:3000)
- **API Gateway**: [http://localhost:8080](http://localhost:8080)
- **Auth Service**: [http://localhost:8081](http://localhost:8081)
- **Task Service**: [http://localhost:8082](http://localhost:8082)
- **Audit Service**: [http://localhost:8083](http://localhost:8083)
- **Notification Service**: [http://localhost:8084](http://localhost:8084)

## 📋 API Endpoints

### Аутентификация
```
POST /auth/register - Регистрация пользователя
POST /auth/login    - Вход в систему
POST /auth/refresh  - Обновление токена
```

### Задачи
```
GET    /tasks       - Получить все задачи
POST   /tasks       - Создать задачу
GET    /tasks/{id}  - Получить задачу по ID
PUT    /tasks/{id}  - Обновить задачу
DELETE /tasks/{id}  - Удалить задачу
```

### Уведомления
```
GET    /notifications       - Получить уведомления
POST   /notifications       - Создать уведомление
PUT    /notifications/{id}  - Пометить как прочитанное
```

## 🔧 Конфигурация

### Переменные окружения (docker.env)
```env
# Database Configuration
POSTGRES_DB=tasktracker
POSTGRES_USER=postgres
POSTGRES_PASSWORD=password

# Redis Configuration
REDIS_HOST=redis
REDIS_PORT=6379

# Kafka Configuration
KAFKA_BOOTSTRAP_SERVERS=kafka:9092

# JWT Configuration
JWT_SECRET=your-secret-key
JWT_EXPIRATION=86400000
```

## 🛠️ Технологии

### Backend
- **Spring Boot** - Основной фреймворк
- **Spring Security** - Безопасность и аутентификация
- **Spring Data JPA** - Работа с базой данных
- **Spring Cloud Gateway** - API Gateway
- **JWT** - Токены для аутентификации

### База данных
- **PostgreSQL** - Основная база данных
- **Redis** - Кэширование
- **Flyway** - Миграции базы данных

### Messaging
- **Apache Kafka** - Асинхронная обработка событий
- **Spring Kafka** - Интеграция с Kafka

### Контейнеризация
- **Docker** - Контейнеризация сервисов
- **Docker Compose** - Оркестрация контейнеров

## 📊 Мониторинг

### Доступные эндпоинты для мониторинга
- `/actuator/health` - Проверка здоровья сервиса
- `/actuator/info` - Информация о сервисе
- `/actuator/metrics` - Метрики сервиса

## 🧪 Тестирование

### Запуск тестов
```bash
# Запуск всех тестов
mvn test

# Запуск тестов для конкретного сервиса
cd auth-service && mvn test
cd task-service && mvn test
cd gateway-service && mvn test
cd audit-service && mvn test
cd notification-service && mvn test
```

### Покрытие тестами
- Unit тесты для всех сервисов
- Integration тесты для критических компонентов
- Тесты безопасности для JWT

## 🔒 Безопасность

### Реализованные меры безопасности
- **JWT Authentication** - Аутентификация на основе токенов
- **CORS Configuration** - Настройка CORS политик
- **Input Validation** - Валидация входных данных
- **SQL Injection Protection** - Защита от SQL инъекций
- **Rate Limiting** - Ограничение запросов

## 📝 Дополнительные возможности

### Аудит
- Логирование всех действий пользователей
- Трекинг изменений в задачах
- Статистика использования

### Уведомления
- Email уведомления
- Push уведомления
- SMS уведомления (настраивается)

### Кэширование
- Redis кэширование часто используемых данных
- Кэширование JWT токенов
- Кэширование пользовательских сессий

## 🤝 Вклад в проект

1. Fork проекта
2. Создайте feature branch (`git checkout -b feature/amazing-feature`)
3. Commit изменения (`git commit -m 'Add amazing feature'`)
4. Push в branch (`git push origin feature/amazing-feature`)
5. Откройте Pull Request

## 📄 Лицензия

Этот проект лицензирован под MIT License - см. файл [LICENSE](LICENSE) для деталей.

## 👨‍💻 Автор

**Orazbakhov Aibek**
- GitHub: [@oak1995](https://github.com/oak1995)
- Email: [orazbakhov@bk.ru](mailto:orazbakhov@bk.ru)

## 🆘 Поддержка

Если у вас есть вопросы или проблемы:
1. Проверьте [Issues](https://github.com/oak1995/task-tracker-microservices/issues)
2. Создайте новый Issue с описанием проблемы
3. Свяжитесь с автором

---

⭐ Если проект был полезен, не забудьте поставить звезду! 