# Mastodon Stream Service

## Description
This service fetches public posts from Mastodon in real-time and publishes them to a Kafka topic. The service ensures no posts are missed by storing the latest processed offset in PostgreSQL.

## Prerequisites
- Docker & Docker Compose
- Java 17+
- Kafka
- PostgreSQL
- Config Server (Spring Boot application)
- Discovery Server (Spring Boot application)

## Setup Instructions

1. **Start Kafka and PostgreSQL**
   ```sh
   docker-compose up -d
   ```
   This will set up Kafka and PostgreSQL for the service.

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

4. **Run Mastodon Stream Service**
   ```sh
   mvn spring-boot:run
   ```
   The service will start on port **8081**.

## How It Works
- The service fetches new public posts from Mastodon every **10 seconds**.
- It uses the latest stored offset in PostgreSQL to avoid reprocessing old posts.
- Posts are serialized into JSON and sent to Kafka asynchronously.
- The latest processed offset is updated in the database.
- If an error occurs, it is logged, and processing continues.
