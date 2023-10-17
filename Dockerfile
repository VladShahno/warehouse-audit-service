FROM maven:3.6.3-openjdk-17 AS build
WORKDIR /home/app
COPY src src
COPY pom.xml .
RUN mvn clean package -DskipTests

FROM openjdk:17
COPY --from=build /home/app/target/*.jar /usr/app/product-management-service.jar
EXPOSE 8081
CMD java -jar -DskipTests /usr/app/product-management-service.jar
