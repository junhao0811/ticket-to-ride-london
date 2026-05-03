# Ticket to Ride London - Makefile

JAR_NAME = TicketToRideLondon.jar
MAIN_CLASS = Main
SRC_DIR = src
RES_DIR = resources
OUT_DIR = target/classes

all: clean compile jar

clean:
	rm -rf $(OUT_DIR)
	rm -f $(JAR_NAME)

$(OUT_DIR):
	mkdir -p $(OUT_DIR)

compile: $(OUT_DIR)
	find $(SRC_DIR) -name "*.java" > sources.txt
	javac -d $(OUT_DIR) @sources.txt
	rm sources.txt

jar:
	cp -r $(RES_DIR)/* $(OUT_DIR)/
	jar cfe $(JAR_NAME) $(MAIN_CLASS) -C $(OUT_DIR) .

run:
	java -jar $(JAR_NAME)

.PHONY: all clean compile jar run
