# Mastodon Streaming Platform

## Description
This platform consists of two microservices that enable real-time processing and broadcasting of Mastodon posts using Kafka and WebSockets. The services work together to fetch posts, stream them via Kafka, and deliver them to WebSocket clients in real-time.

## Microservices

### 1. **Mastodon Stream Service** (Port: 8081)
- Fetches public posts from Mastodon.
- Stores the last processed offset in PostgreSQL.
- Publishes posts to Kafka.

### 2. **Post WebSocket Service** (Port: 8082)
- Listens for new posts from Kafka.
- Broadcasts posts to WebSocket clients.
- Manages concurrent WebSocket connections.

## Prerequisites
- Docker & Docker Compose
- Java 21
- Kafka
- PostgreSQL
- Config Server (Spring Boot application)
- Discovery Server (Spring Boot application)

## Setup Instructions

1. **Start Kafka and PostgreSQL**
   ```sh
   docker-compose up -d
   ```
   This sets up Kafka and PostgreSQL.

2. **Start Config Server**
   ```sh
   mvn spring-boot:run
   ```

3. **Start Discovery Server**
   ```sh
   mvn spring-boot:run
   ```

4. **Start Mastodon Stream Service** (Port 8081)
   ```sh
   mvn spring-boot:run
   ```

5. **Start Post WebSocket Service** (Port 8082)
   ```sh
   mvn spring-boot:run
   ```

## Connecting to WebSockets
WebSocket clients can connect to receive real-time updates using the following endpoint:
   ```
   ws://localhost:8082/posts
   ```

## Architecture Overview
1. The **Mastodon Stream Service** retrieves new posts and publishes them to Kafka.
2. The **Post WebSocket Service** consumes the posts from Kafka and sends them to connected WebSocket clients.
3. The **Config Server** provides configuration management.
4. The **Discovery Server** handles service registration and discovery.

## Technologies Used
- **Spring Boot** (Microservices framework)
- **Spring Cloud** (Service discovery and configuration management)
- **Kafka** (Message streaming)
- **Spring WebSocket** (Real-time communication)
- **PostgreSQL** (Offset storage)
- **Docker** (Containerization)

## Logs & Monitoring
- Logs are recorded using SLF4J with `@Slf4j`.
- Proper log rotation should be configured for production.

## Future Enhancements
- Implement authentication for WebSocket connections.
- Introduce monitoring and alerting for Kafka consumers.
- Optimize WebSocket scalability with load balancing.

