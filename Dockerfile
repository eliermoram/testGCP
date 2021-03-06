FROM maven:3.6-jdk-8-alpine as builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn package -DskipTests


FROM adoptopenjdk/openjdk11:alpine-jre
COPY --from=builder /app/target/demo-*.jar /demo.jar
CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/demo.jar"]