FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src ./src
RUN mvn -q -B package -DskipTests
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/target/localmart-ai-1.0.0.jar"]
