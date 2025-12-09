# ============================================================
# 1) BUILD STAGE — Compila el proyecto con cacheo de dependencias
# ============================================================
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Directorio de trabajo dentro de la imagen
WORKDIR /app

# Copiamos solo el pom para poder cachear dependencias
COPY pom.xml .

# Descarga dependencias SIN compilar (se cachea en Docker)
RUN mvn dependency:go-offline -B

# Ahora copiamos el código fuente del frontend
COPY src ./src

# Compila el Jar del frontend
RUN mvn -DskipTests clean package


# ============================================================
# 2) RUNTIME STAGE — Imagen final liviana
# ============================================================
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copiamos el jar generado en el stage anterior
COPY --from=build /app/target/*.jar app.jar

# Railway asigna el puerto mediante env PORT, pero dejamos 8080 por defecto
EXPOSE 8080

# Entry point
ENTRYPOINT ["java", "-jar", "app.jar"]
