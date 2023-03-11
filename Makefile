DIR_ATM=atm
ATM_JAR=atm/target/atm-1.0.jar

clean:
	make -C $(DIR_ATM) clean

build:
	make -C $(DIR_ATM) all

run:
	java -jar $(ATM_JAR)
