FROM maven:3.9.0-jdk-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src ./src
RUN mvn -q -B package -DskipTests
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=builder /app/target/localmart-ai-1.0.0.jar ./app.jar
EXPOSE 8080
ENTRYPOINT ["sh","-c","java -jar app.jar --server.port=${PORT:-8080}"]
