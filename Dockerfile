FROM gradle:jdk21 as build
USER root
WORKDIR /workspace/app

COPY build.gradle settings.gradle gradlew ./
COPY gradle/ gradle/
RUN chmod +x gradle --version

COPY . .
RUN chmod +x gradle && gradle build -x test

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /workspace/app
COPY --from=build /workspace/app/build/libs/*.jar app.jar

EXPOSE 8080
CMD ["java","-jar","app.jar"]