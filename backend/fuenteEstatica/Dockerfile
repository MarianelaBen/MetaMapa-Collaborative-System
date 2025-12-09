# Etapa 1: build con Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copiamos TODO el backend (incluye agregador, fuenteEstatica, etc)
# OJO: este Dockerfile está en backend/fuenteEstatica,
# por eso ".." apunta a la carpeta backend completa.
COPY .. .

# Compilamos SOLO fuenteEstatica y salteamos tests
RUN mvn -f fuenteEstatica/pom.xml -DskipTests clean package

# Etapa 2: imagen liviana solo con el JAR
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copiamos el jar generado en la etapa anterior
COPY --from=build /app/fuenteEstatica/target/*.jar app.jar

# Puerto interno del servicio (distinto al de agregador)
EXPOSE 8082

# Comando de arranque
CMD ["java", "-jar", "app.jar"]

