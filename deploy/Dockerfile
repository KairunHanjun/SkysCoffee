FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --chown=10002:10002 app.jar /app/app.jar
USER 10002:10002
ENV SPRING_PROFILES_ACTIVE=demo SERVER_ADDRESS=0.0.0.0 SERVER_PORT=8080 SERVER_FORWARD_HEADERS_STRATEGY=framework SERVER_SERVLET_SESSION_COOKIE_SECURE=true
ENTRYPOINT ["java","-Xms32m","-Xmx96m","-XX:MaxMetaspaceSize=96m","-XX:ReservedCodeCacheSize=32m","-XX:MaxDirectMemorySize=16m","-XX:+UseSerialGC","-XX:ActiveProcessorCount=1","-XX:+ExitOnOutOfMemoryError","-jar","/app/app.jar"]
