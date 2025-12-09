# Etapa 1: build con Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copiamos TODO el repositorio (dos niveles arriba)
COPY ./../.. .

# Compilamos SOLO fuenteEstatica y salteamos tests
RUN mvn -f backend/fuenteEstatica/pom.xml -DskipTests clean package

# Etapa 2: imagen liviana
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copiamos el jar generado
COPY --from=build /app/backend/fuenteEstatica/target/*.jar app.jar

EXPOSE 8081

CMD ["java", "-jar", "app.jar"]
