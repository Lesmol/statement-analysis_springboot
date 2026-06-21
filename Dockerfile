FROM amazoncorretto:25-al2023 AS build
WORKDIR /app

COPY target/statement-analysis_springboot-*.jar app.jar

EXPOSE 8080
ENV PORT=8080

ENTRYPOINT ["java", "-Xmx512m", "-jar", "app.jar"]