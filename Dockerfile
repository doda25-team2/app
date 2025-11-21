FROM maven:3.9-eclipse-temurin-25 AS build

WORKDIR /workspace

COPY pom.xml .
COPY src ./src

# The settings.xml required to access the GitHub Package registry
# is mounted as a build secret
RUN --mount=type=secret,id=maven-settings,target=/root/.m2/settings.xml \
    mvn --settings /root/.m2/settings.xml -B -DskipTests package

FROM eclipse-temurin:25-jre-jammy
WORKDIR /app

COPY --from=build /workspace/target/*.jar app.jar


# metadata: the container listens on 8080 by default
EXPOSE 8080

# default environment values (can be overridden by docker run / compose)
ENV APP_PORT=8080
ENV MODEL_SERVICE_URL=http://model-service:8081

ENTRYPOINT ["java","-jar","app.jar"]
