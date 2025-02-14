# Post WebSocket Service

## Description
This service listens for new posts from Kafka and broadcasts them to connected WebSocket clients in real-time. It ensures scalability by supporting concurrent connections.

## Prerequisites
- Docker & Docker Compose
- Java 21
- Kafka
- Config Server (Spring Boot application)
- Discovery Server (Spring Boot application)

## Setup Instructions

1. **Start Kafka**
   ```sh
   docker-compose up -d
   ```
   This will set up Kafka for the service.

2. **Start Config Server**
   ```sh
   mvn spring-boot:run
   ```
   Ensure the Config Server (a Spring Boot application) is running before starting this service.

3. **Start Discovery Server**
   ```sh
   mvn spring-boot:run
   ```
   The Discovery Server (a Spring Boot application) should be running before launching this service.

4. **Run Post WebSocket Service**
   ```sh
   mvn spring-boot:run
   ```
   The service will start on port **8082**.

## Configuration
Ensure that the following properties are correctly set in your configuration:

- `kafka.topic.posts-stream` → Kafka topic for Mastodon posts.
- `kafka.consumer.group-id` → Consumer group ID for Kafka.
- `server.port` → WebSocket service port (default: 8082).

## How It Works
- The service listens to new posts from the Kafka topic `posts-stream`.
- It validates and broadcasts each post to all connected WebSocket clients.
- WebSocket clients receive real-time updates without polling.
- If a session is closed, it is removed from the active sessions list.