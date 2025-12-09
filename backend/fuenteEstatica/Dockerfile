# ==========================
# Etapa 1: Build con Maven
# ==========================
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copiamos TODO el backend (porque el pom padre está en /backend)
COPY .. .

# Compilamos SOLO fuenteEstatica usando el pom padre MetaMapa
# -pl fuenteEstatica   → build del módulo
# -am                  → compila dependencias necesarias
# -DskipTests          → sin tests
RUN mvn -DskipTests -pl fuenteEstatica -am clean package

# ==========================
# Etapa 2: Imagen liviana
# ==========================
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copiamos el jar generado
COPY --from=build /app/fuenteEstatica/target/*.jar app.jar

EXPOSE 8082

CMD ["java", "-jar", "app.jar"]
# Etapa 1: build con Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copiamos TODO el backend (porque el contexto es /backend)
COPY . .

# Compilamos fuenteEstatica (usando el pom padre MetaMapa)
RUN mvn -DskipTests -pl fuenteEstatica -am clean package

# Etapa 2: imagen liviana solo con el JAR
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copiamos el JAR construido del módulo fuenteEstatica
COPY --from=build /app/fuenteEstatica/target/*.jar app.jar

EXPOSE 8082

CMD ["java", "-jar", "app.jar"]

