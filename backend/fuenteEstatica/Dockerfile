# Etapa 1: Build
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copiamos TODO el repositorio (necesario para que encuentre el pom padre)
COPY . .

# COMANDO CLAVE:
# -pl backend/fuenteEstatica: "Project List", compila solo este módulo.
# -am: "Also Make", compila también las dependencias (el padre MetaMapa).
RUN mvn clean package -DskipTests -pl backend/fuenteEstatica -am

# Etapa 2: Ejecución
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copiamos el JAR generado
COPY --from=build /app/backend/fuenteEstatica/target/*.jar app.jar

EXPOSE 8081

CMD ["java", "-jar", "app.jar"]
