FROM java:8-jre-alpine

ENV TZ=Europe/Oslo
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

RUN apk update && apk upgrade

ADD ./target /home/application/

WORKDIR /home/application/

EXPOSE 8080

CMD java -jar $JAVA_OPTS -Dspring.profiles.active=docker xmlToRdfServer-1.0-SNAPSHOT.jar $SPRING_DEBUG