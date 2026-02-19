# Multi-stage build for PayrollTax Pro API

# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml first for better caching
COPY pom.xml .

# Download dependencies (this will cache if pom.xml doesn't change)
RUN mvn dependency:go-offline -B --no-transfer-progress || true

# Copy source
COPY src ./src

# Build application with retry
RUN mvn clean package -DskipTests -B --no-transfer-progress || \
    mvn clean package -DskipTests -B --no-transfer-progress || \
    mvn clean package -DskipTests -B --no-transfer-progress

# Stage 2: Run
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Install curl for health checks
RUN apk add --no-cache curl

# Copy jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/api/health || exit 1

# Run application
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
