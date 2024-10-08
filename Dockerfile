FROM openjdk:17-jdk-slim as builder

RUN apt-get update && apt-get install -y python3

ENV APP_HOME=/apps
WORKDIR $APP_HOME

COPY gradlew $APP_HOME
COPY gradle $APP_HOME/gradle
COPY src $APP_HOME/src
COPY build.gradle $APP_HOME
COPY settings.gradle $APP_HOME

ARG JAR_FILE_PATH=./build/libs/dpide-0.0.1-SNAPSHOT.jar
VOLUME /user_files
COPY ${JAR_FILE_PATH} app.jar

ENV TZ=Asia/Seoul

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]