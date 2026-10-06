#!/bin/sh

# Check if java is installed
if ! command -v java >/dev/null 2>&1; then
  echo
  echo "[ERROR] Java is not installed or not in PATH."
  echo -n "Press Enter to exit..."
  read dummy
  exit 1
fi

# Check Java version
JAVA_VERSION=$(java -XshowSettings:properties -version 2>&1 | grep 'java.version' | awk -F'=' '{print $2}' | tr -d '[:space:]"')

MAJOR_VERSION=$(echo "$JAVA_VERSION" | cut -d. -f1)

if [ "$MAJOR_VERSION" = "1" ]; then
  MAJOR_VERSION=$(echo "$JAVA_VERSION" | cut -d. -f2)
fi

if [ "$MAJOR_VERSION" -lt 11 ]; then
  echo
  echo "[ERROR] Java 11 or higher is required. Found version $JAVA_VERSION."
  echo -n "Press Enter to exit..."
  read dummy
  exit 1
fi

echo "Running service-creator.jar..."

cd service-creator/program/ || exit 1

java -jar service-creator.jar
