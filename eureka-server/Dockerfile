FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

ARG MODULE=eureka-server
ARG PORT=8761

COPY pom.xml ./
COPY . .

RUN mvn clean install -DskipTests -pl ${MODULE} -am

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

ARG MODULE=eureka-server
ARG PORT=8761

COPY --from=build /app/${MODULE}/target/*.jar app.jar

EXPOSE ${PORT}
ENTRYPOINT ["java","-jar","app.jar"]