
FROM maven:3.9.13-eclipse-temurin-21 AS build
WORKDIR /workspace

ARG SERVICE_NAME

COPY pom.xml ./pom.xml
COPY shared/pom.xml ./shared/pom.xml
COPY player-service/pom.xml ./player-service/pom.xml
COPY rewards-service/pom.xml ./rewards-service/pom.xml
COPY retention-service/pom.xml ./retention-service/pom.xml
COPY communication-service/pom.xml ./communication-service/pom.xml

COPY shared/src ./shared/src
COPY player-service/src ./player-service/src
COPY rewards-service/src ./rewards-service/src
COPY retention-service/src ./retention-service/src
COPY communication-service/src ./communication-service/src

RUN test -n "${SERVICE_NAME}" && \
    mvn -B -ntp -pl "${SERVICE_NAME}" -am package -DskipTests

FROM eclipse-temurin:21-jre-ubi10-minimal AS runtime
WORKDIR /app

ARG SERVICE_NAME
COPY --from=build /workspace/${SERVICE_NAME}/target/${SERVICE_NAME}-1.0.0.jar /app/app.jar

RUN mkdir -p /app/data /app/logs && chown -R 1000:1000 /app
USER 1000

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "/app/app.jar"]
