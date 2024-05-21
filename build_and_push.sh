#!/bin/bash

# Step 1: Build the project with Maven
./mvnw clean package

# Step 2: Build the Docker image
docker build -t mawulib/sns-image:latest .

# Step 3: Push the Docker image to Docker Hub
docker push mawulib/sns-image:latest