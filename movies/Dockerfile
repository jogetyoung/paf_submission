FROM openjdk:23-jdk-oracle as builder

ARG COMPILED_DIR=/compiledir

WORKDIR ${COMPILED_DIR}

COPY mvnw .
COPY mvnw.cmd .
COPY pom.xml .
COPY .mvn .mvn
COPY src src

#RUN ./mvnw package -Dmvn.test.skip=true

RUN ./mvnw clean package -DskipTests

FROM openjdk:23-jdk-oracle

ARG WORK_DIR=/app

WORKDIR ${WORK_DIR}

COPY --from=builder /compiledir/target/movies-0.0.1-SNAPSHOT.jar app.jar

# run the application
# define railway environment variable
# Define environment variables (matching `application.properties`)
# MongoDB
ENV SPRING_DATA_MONGODB_DATABASE=""
ENV SPRING_DATA_MONGODB_URI=""

# MySQL
ENV SPRING_DATASOURCE_URL=""
ENV SPRING_DATASOURCE_USERNAME=""
ENV SPRING_DATASOURCE_PASSWORD=""

ENV PORT=8080

EXPOSE ${PORT}

# Use JSON-formatted ENTRYPOINT for better signal handling
ENTRYPOINT ["java", "-jar", "/app/app.jar", "--server.port=${PORT}"]