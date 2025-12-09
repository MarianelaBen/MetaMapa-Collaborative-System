# =========================
# 1) BUILD STAGE
# =========================
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copio SOLO el módulo frontend
COPY pom.xml .
COPY src ./src

# Build del frontend (Spring MVC + Thymeleaf)
RUN mvn -DskipTests clean package


# =========================
# 2) RUN STAGE
# =========================
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
