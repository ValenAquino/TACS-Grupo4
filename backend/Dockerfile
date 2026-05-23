# --- Etapa de dependencias ---
# Descarga todas las dependencias y plugins sin compilar
# Lo usamos como base en dev
FROM maven:3.9-eclipse-temurin-17 AS dependencies
WORKDIR /app
COPY pom.xml .
COPY spotbugs-exclude.xml .
COPY lombok.config .
RUN mvn dependency:go-offline

# --- Etapa de desarrollo ---
# Agrega el código fuente sin correr tests ni validaciones
# Lo usamos en docker compose watch para dev
FROM dependencies AS dev
COPY src ./src

# --- Etapa de build ---
# Por defecto corre tests + SpotBugs + JaCoCo. En CI se puede pasar
# MAVEN_ARGS="package -DskipTests" porque los tests ya corrieron en un step previo.
FROM dev AS build
ARG MAVEN_ARGS=verify
RUN mvn $MAVEN_ARGS

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
