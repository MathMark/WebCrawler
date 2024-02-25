FROM openjdk:17-jdk-slim

COPY target/webcrawler-0.0.1-SNAPSHOT.jar webcrawler-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "webcrawler-0.0.1-SNAPSHOT.jar"]