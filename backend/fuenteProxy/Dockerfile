# Etapa 1: build con Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copiamos TODO el backend (incluye todos los módulos, como en agregador)
COPY ..

# Compilamos SOLO fuenteProxy y salteamos tests
RUN mvn -f fuenteProxy/pom.xml -DskipTests clean package

# Etapa 2: imagen liviana solo con el JAR
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copiamos el jar generado en la etapa anterior
COPY --from=build /app/fuenteProxy/target/*.jar app.jar

# fuenteProxy escucha en 8082 (ya está en application.properties)
EXPOSE 8082

CMD ["java", "-jar", "app.jar"]
