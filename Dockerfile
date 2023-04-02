FROM ghcr.io/graalvm/jdk:ol8-java17-22.3.1
COPY target/test-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]