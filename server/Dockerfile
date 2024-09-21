#
# Build stage
#
FROM maven:3.9.9-sapmachine-21 AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean -DskipTests=true package

#
# Package stage
#
FROM openjdk:21
COPY --from=build /home/app/target/multi-db-system-0.0.1-SNAPSHOT.jar /usr/local/lib/multi-db-system.jar
ENTRYPOINT ["java", "-jar", "/usr/local/lib/multi-db-system.jar"]
