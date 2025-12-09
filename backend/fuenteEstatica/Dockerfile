# Etapa 1: build con Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Como el Root Directory en Railway es /backend/fuenteEstatica,
# acá copiamos TODO el módulo fuenteEstatica
COPY . .

# Compilamos ESTE módulo y salteamos tests
RUN mvn -DskipTests clean package

# Etapa 2: imagen liviana solo con el JAR
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copiamos el jar generado en la etapa anterior
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8081

CMD ["java", "-jar", "app.jar"]


