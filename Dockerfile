FROM openjdk:8-jdk-alpine
COPY target/project-0.0.1-SNAPSHOT.jar ./
EXPOSE 8080
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
