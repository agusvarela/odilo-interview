FROM openjdk:17
COPY /target/interview-0.0.1-SNAPSHOT.jar odilo-interview.jar

EXPOSE 8080
CMD ["java", "-jar", "odilo-interview.jar"]
