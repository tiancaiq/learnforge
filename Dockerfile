FROM openjdk:11.0-jre-buster
LABEL maintainer="LearnForge contributors"
ENV JAVA_OPTS=""
# Set Time Zone
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

WORKDIR /app
ADD app.jar /app/app.jar

ENTRYPOINT ["sh","-c","java  -jar $JAVA_OPTS /app/app.jar"]
