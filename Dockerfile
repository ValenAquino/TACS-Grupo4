# --- Etapa de build ---
# Usa Maven con JDK 17 para compilar el proyecto
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copiamos el pom y el código fuente
COPY pom.xml .
COPY src ./src

# Compilamos y generamos el JAR sin correr tests
RUN mvn clean package -DskipTests

# --- Etapa de runtime ---
# Se usa JRE para ejecutar
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copiamos el JAR
COPY --from=build /app/target/*.jar app.jar

# Exponemos el puerto
EXPOSE 8080

# Ejecutamos la app
ENTRYPOINT ["java", "-jar", "app.jar"]