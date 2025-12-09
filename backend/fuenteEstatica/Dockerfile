# Etapa 1: Build
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# 1. Copiamos TODO el repositorio (Contexto Raíz)
# Esto soluciona el problema de "COPY .. .". 
# Al copiar ".", estamos copiando todo lo que Railway ve en el "Root Directory".
COPY . .

# 2. Compilamos apuntando a la ruta específica del pom
# Asumiendo que la estructura es /backend/fuenteEstatica según tu captura
RUN mvn -f backend/fuenteEstatica/pom.xml -DskipTests clean package

# Etapa 2: Run
FROM eclipse-temurin:17-jre

WORKDIR /app

# 3. Buscamos el JAR en la ruta completa generada en la etapa anterior
COPY --from=build /app/backend/fuenteEstatica/target/*.jar app.jar

EXPOSE 8081

CMD ["java", "-jar", "app.jar"]
