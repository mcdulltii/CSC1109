FROM maven:latest
COPY atm /home/app/atm

WORKDIR /home/app/atm/src/main/java/com/atm
RUN sed -i "s/localhost:3306/mysql:3306/g" SQLQueries.java
RUN sed -i "s/atm\/res/res/g" SQLQueries.java

WORKDIR /home/app/atm

COPY ./docker/wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh

ENTRYPOINT ["/wait-for-it.sh", "mysql:3306", "-t", "30", "--", "mvn", "compile", "exec:java"]