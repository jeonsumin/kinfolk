FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml ./
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn -DskipTests package && cp target/swc.jar ./application.jar

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/application.jar ./application.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "application.jar"]
