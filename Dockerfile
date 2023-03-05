FROM maven:3.6.3-jdk-11-slim
#COPY entrypoint.sh /usr/local/bin/entrypoint.sh
#RUN apt-get update && apt-get install dos2unix && dos2unix /usr/local/bin/entrypoint.sh && chmod +x /usr/local/bin/entrypoint.sh
ENV APP_HOME=/usr/
#Start application
WORKDIR $APP_HOME

COPY src/main/resources/application.yml application.yml
COPY target/*.jar app.jar
CMD ["java","-jar","app.jar"]