#!/bin/bash
# Ticket to Ride London - Build Script

# Set names
JAR_NAME="TicketToRideLondon.jar"
MAIN_CLASS="Main"
SRC_DIR="src"
RES_DIR="resources"
OUT_DIR="target/classes"

echo "Cleaning old build artifacts..."
rm -rf "$OUT_DIR"
rm -f "$JAR_NAME"

echo "Creating build directory..."
mkdir -p "$OUT_DIR"

echo "Compiling source files..."
# Find all Java files and compile them
find "$SRC_DIR" -name "*.java" > sources.txt
javac --release 17 -d "$OUT_DIR" @sources.txt
rm sources.txt

if [ $? -ne 0 ]; then
    echo "Compilation failed!"
    exit 1
fi

echo "Copying resources..."
cp -r "$RES_DIR"/* "$OUT_DIR"/

echo "Creating runnable JAR..."
jar cfe "$JAR_NAME" "$MAIN_CLASS" -C "$OUT_DIR" .

if [ $? -eq 0 ]; then
    echo "Build successful: $JAR_NAME"
    echo "You can run it with: java -jar $JAR_NAME"
else
    echo "Failed to create JAR!"
    exit 1
fi
