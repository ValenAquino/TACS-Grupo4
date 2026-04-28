# --- Etapa de build ---
# Usa Maven con JDK 17 para compilar el proyecto
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copiamos el pom, la configuración de SpotBugs, la configuracion de lombok y el código fuente
COPY pom.xml .
COPY spotbugs-exclude.xml .
COPY lombok.config .
COPY src ./src

# Compilamos, corremos tests y validaciones, y generamos el JAR
RUN mvn verify

# --- Etapa de runtime ---
# Se usa JRE Alpine para ejecutar — imagen mas liviana que la estandar
# forzamos la plataforma linux/amd64 porque eclipse-temurin:alpine no tiene soporte ARM64
FROM --platform=linux/amd64 eclipse-temurin:17-jre-alpine
WORKDIR /app

# Creamos un usuario sin privilegios para correr la app
# Si alguien explota la app, no tiene acceso root al contenedor
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copiamos el JAR
COPY --from=build /app/target/*.jar app.jar

# Cambiamos al usuario sin privilegios
USER appuser

# Exponemos el puerto
EXPOSE 8080

# Ejecutamos la app
ENTRYPOINT ["java", "-jar", "app.jar"]
