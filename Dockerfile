FROM openjdk:11-jdk

RUN mkdir app
WORKDIR app
ADD . .
RUN ./gradlew bootWar

EXPOSE 9080

CMD java -jar build/libs/*.war
