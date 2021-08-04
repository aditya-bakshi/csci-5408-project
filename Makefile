BUILD_DIR  := target
TARGET := $(BUILD_DIR)/app-1.0.0-jar-with-dependencies.jar

all: build run

r: run
b: build
cb: clean build
cr: clean run

$(BUILD_DIR):
	mvn clean package

$(TARGET): $(BUILD_DIR)
	mvn package

build: clean-target $(TARGET)

run: $(TARGET)
	java -jar $(TARGET)

clean-target:
	rm -rf $(TARGET)

clean:
	rm -rf $(BUILD_DIR)

