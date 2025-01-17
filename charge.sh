#!/bin/bash
# A simple script to create load by sending multiple concurrent requests to the server.

# Define the number of requests
REQUESTS=300

# The endpoint to test
URL="http://localhost:8004/users/"

# Loop to send concurrent requests
for i in $(seq 1 $REQUESTS)
do
   curl -s "$URL" &  # The ampersand at the end sends the request in the background
done

wait # Wait for all background jobs to finish
echo "All requests completed."
