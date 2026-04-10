FROM maven:3.9.9-eclipse-temurin-17

WORKDIR /app

COPY . .

RUN mvn clean verify

EXPOSE 8080
CMD ["java", "-jar", "target/TACS-G4.jar"]