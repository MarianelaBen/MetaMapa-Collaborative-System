FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# El contexto ya es backend/fuenteEstatica
COPY . .

# Compilamos este módulo y salteamos tests
RUN mvn -DskipTests clean package

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8082
CMD ["java", "-jar", "app.jar"]

