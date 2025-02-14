
FROM openjdk:21-jdk-slim

RUN apt-get update && apt-get install -y maven

WORKDIR /app

COPY pom.xml /app/
RUN mvn dependency:go-offline

COPY src /app/src

RUN mvn clean package -DskipTests

EXPOSE 8082

CMD ["java", "-jar", "target/post-gateway-0.0.1-SNAPSHOT.jar"]