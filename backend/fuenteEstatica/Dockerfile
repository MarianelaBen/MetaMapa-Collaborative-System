# ==========================
# Etapa 1: Build con Maven
# ==========================
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copiamos TODO el módulo fuenteEstatica
# (el contexto ya es /backend/fuenteEstatica gracias al Root Directory)
COPY . .

# Compilamos el proyecto y salteamos los tests
RUN mvn -DskipTests clean package

# ==========================
# Etapa 2: Imagen liviana
# ==========================
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copiamos el JAR generado en la etapa anterior
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8082

CMD ["java", "-jar", "app.jar"]

