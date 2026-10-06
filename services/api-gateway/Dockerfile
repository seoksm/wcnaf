FROM eclipse-temurin:17-jdk

WORKDIR /app
RUN echo "./gradlew bootRun --no-daemon" > entrypoint.sh
RUN cat entrypoint.sh
ENTRYPOINT ["sh", "./entrypoint.sh"]