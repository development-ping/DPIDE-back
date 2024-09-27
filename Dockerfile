FROM bellsoft/liberica-openjdk-alpine:17

ENV APP_HOME=/apps
WORKDIR $APP_HOME

COPY gradlew $APP_HOME
COPY gradle $APP_HOME/gradle
COPY src $APP_HOME/src
COPY build.gradle $APP_HOME
COPY settings.gradle $APP_HOME

ARG JAR_FILE_PATH=./build/libs/dpide-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE_PATH} app.jar

ARG DEBIAN_FRONTEND=noninteractive
ENV TZ=Asia/Seoul

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]