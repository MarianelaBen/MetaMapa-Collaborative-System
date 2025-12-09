# Etapa 1 - Build
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copiamos TODO el proyecto (Railway construye desde la raíz)
COPY . .

# Compilamos SOLO el módulo frontend
RUN mvn -f frontend/pom.xml -DskipTests clean package


# Etapa 2 - Runtime
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY --from=build /app/frontend/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
