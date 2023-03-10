FROM maven:latest AS build
COPY atm /home/app/atm

WORKDIR /home/app/atm/src/main/java/com/atm
RUN sed -i "s/atm\/res/res/g" SQLQueries.java

WORKDIR /home/app/atm
ENTRYPOINT ["mvn", "compile", "exec:java"]