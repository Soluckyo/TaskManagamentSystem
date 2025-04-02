#FROM openjdk:17-jdk-slim AS build
# Устанавливаем Maven 3.9.8 т.к оф. образа на DockerHub нет
#RUN apt-get update && apt-get install -y \
 #   curl \
 #   tar \
 #   && rm -rf /var/lib/apt/lists/*
#RUN curl -o /tmp/apache-maven-3.9.8-bin.tar.gz https://downloads.apache.org/maven/maven-3/3.9.8/binaries/apache-maven-3.9.8-bin.tar.gz \
#    && tar -xvzf /tmp/apache-maven-3.9.8-bin.tar.gz -C /opt/ \
#    && rm /tmp/apache-maven-3.9.8-bin.tar.gz
# Настроим переменные среды для Maven
#ENV MAVEN_HOME /opt/apache-maven-3.9.8
#ENV PATH $MAVEN_HOME/bin:$PATH
#----------------

FROM maven:latest AS build

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src /app/src
RUN mvn clean package

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build target/*.jar /app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]