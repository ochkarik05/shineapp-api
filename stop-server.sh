#!/bin/bash

# Find the PIDs and their associated command lines of Netty servers
PIDS=$(pgrep -f netty)
PIDS_INFO=()

if [ -z "$PIDS" ]; then
    echo "Netty server is not running."
else
    # Populate the PIDS_INFO array
    for PID in $PIDS; do
        CMDLINE=$(ps -p $PID -o comm=)
        PIDS_INFO+=("PID: $PID, Command: $CMDLINE")
    done

    # Display information about each PID and prompt the user to choose one
    echo "Multiple Netty servers running. Choose a PID to stop:"
    select INFO in "${PIDS_INFO[@]}"; do
        if [ -z "$INFO" ]; then
            echo "Invalid choice. Please select a valid PID."
        else
            # Extract the PID from the selected INFO
            PID=$(echo "$INFO" | awk '{print $2}' | sed 's/,//')
            echo "Stopping Netty server with PID $PID..."
            # Try graceful shutdown first, then forceful if necessary
            kill -TERM $PID
            sleep 5 # Wait for 5 seconds for graceful shutdown
            if ps -p $PID > /dev/null; then
                echo "Graceful shutdown failed, forcing termination..."
                kill -KILL $PID
            else
                echo "Netty server with PID $PID stopped gracefully."
            fi
            break
        fi
    done
fi
