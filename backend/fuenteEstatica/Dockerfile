# Etapa 1: Build
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# IMPORTANTE: Usamos un solo punto (.)
# Como tu Root Directory es "/backend", este punto YA SIGNIFICA "todo el backend".
COPY .. .

# Compilamos usando la lista de proyectos (-pl) para ubicar la carpeta hija
RUN mvn clean package -DskipTests -pl fuenteEstatica -am

# Etapa 2: Run
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copiamos el jar desde la ruta compilada
COPY --from=build /app/fuenteEstatica/target/*.jar app.jar

EXPOSE 8081

CMD ["java", "-jar", "app.jar"]
