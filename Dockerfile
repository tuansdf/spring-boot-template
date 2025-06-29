FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
RUN --mount=type=cache,id=gradle,target=/root/.gradle ./gradlew clean build --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/main.jar main.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/main.jar"]