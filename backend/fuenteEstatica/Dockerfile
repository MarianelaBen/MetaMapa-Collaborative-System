# Etapa 1: build con Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copiamos TODO el backend (y el repo completo)
COPY .. .

# Compilamos SOLO fuenteEstatica y salteamos tests
RUN mvn -f fuenteEstatica/pom.xml -DskipTests clean package

# Etapa 2: imagen liviana solo con el JAR
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copiamos el jar generado en la etapa anterior
COPY --from=build /app/fuenteEstatica/target/*.jar app.jar

EXPOSE 8081

CMD ["java", "-jar", "app.jar"]

