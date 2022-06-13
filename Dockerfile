# syntax=docker/dockerfile:1
FROM eclipse-temurin:17-jdk

RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        maven \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY pom.xml ./
COPY settings.xml ./

COPY src ./src
COPY data ./data
COPY docker ./docker
#COPY shell ./shell

RUN mkdir results

RUN mvn package --settings settings.xml

RUN mv ./target/ts_runtime-jar-with-dependencies.jar ./target/ts_runtime.jar
RUN mv ./target/solver_runtime-jar-with-dependencies.jar ./target/solver_runtime.jar
RUN mv ./target/wipeoutr_fm-jar-with-dependencies.jar ./target/wipeoutr_fm.jar
RUN mv ./target/wipeoutr_t-jar-with-dependencies.jar ./target/wipeoutr_t.jar

RUN chmod +x ./docker/run_docker.sh
RUN ./docker/run_docker.sh

#RUN java -jar ./target/ts_runtime.jar -cfg ./conf/docker/ts_runtime/ts_runtime_10_0.cfg
#RUN java -jar ./target/ts_runtime.jar -cfg ./conf/docker/ts_runtime/ts_runtime_10_50.cfg