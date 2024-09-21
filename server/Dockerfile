#
# Build stage
#
FROM maven:3.8.4-openjdk-21 AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean -DskipTests=true package

#
# Package stage
#
FROM openjdk:21
COPY --from=build /home/cowbot/app/target/multi-db-system-0.0.1-SNAPSHOT.jar /usr/local/lib/multi-db-system.jar
ENTRYPOINT ["java", "-jar", "/usr/local/lib/multi-db-system.jar"]
