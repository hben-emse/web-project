#OpenJDK runtime as a parent image
FROM openjdk:17-jdk

# Set the maintainer
LABEL maintainer="hben.emse@gmail.com"

# Set a volume pointing to /tmp
VOLUME /tmp

# Make port configurable as an environment variable
ARG PORT=8080
ENV SERVER_PORT ${PORT}
EXPOSE ${SERVER_PORT}

# The application's jar file
ARG JAR_FILE=target/spring-boot-jpa-h2-0.0.1-SNAPSHOT.jar

# Add the application's jar to the container
ADD ${JAR_FILE} app.jar

# Run the jar file
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]