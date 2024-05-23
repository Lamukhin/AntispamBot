FROM gradle:jdk21 as build
USER root
WORKDIR /workspace/app

COPY gradlew .
COPY .gradle .gradle
COPY build.gradle .
COPY src src

#RUN chmod a+x gradlew && gradle wrapper && ./gradlew build -x test
RUN gradle build -x test
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

FROM eclipse-temurin:17-jdk-alpine
#VOLUME /tmp
ARG DEPENDENCY=/workspace/app/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

EXPOSE 8080
ENTRYPOINT ["java","-cp","app:app/lib/*", "com.lamukhin.AntispamBot.AntispamBotApplication"]