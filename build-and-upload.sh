#!/bin/bash

# Exit immediately if any command returns a non-zero status
set -e

# Function to handle errors and exit gracefully
handle_error() {
    echo "An error occurred. Script aborted." >&2
    exit 1
}

# Trap errors and execute the error handling function
trap 'handle_error' ERR

# Read the server address from the file
server_address=$(cat keys/server-address.txt)

# Run Gradle task to build the fat JAR
echo "Building fat JAR..."
./gradlew :main-app:buildFatJar

echo "Build successful. Uploading JAR to server..."

remote_path="shineapp-api"

# Upload JAR to server using SCP and key authentication, renaming to shineapp-api.jar
scp -i /keys/shineapp main-app/build/libs/main-app-all.jar "$server_address:$remote_path/shineapp-api.jar"

echo "JAR uploaded successfully."

# Restart the shineapp-api.service on the remote machine
echo "Restarting shineapp-api.service on the remote machine..."
ssh -i keys/shineapp  "$server_address" "sudo systemctl restart shineapp-api.service"

echo "Service restarted."
