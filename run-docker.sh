#!/bin/bash

# Build only if image doesn't exist or -b flag is provided
if [ "$1" = "-b" ] || ! docker image inspect durak:v1 &> /dev/null; then
  echo "Building Docker image..."
  docker build -t durak:v1 .
fi

echo "Choose mode: [1] GUI  [2] TUI"
read -p "Choice: " choice

if [ "$choice" = "1" ]; then
  xhost +local:docker
  docker run -it --rm -e DISPLAY=$DISPLAY -v /tmp/.X11-unix:/tmp/.X11-unix --network host durak:v1
else
  docker run -it --rm durak:v1
fi
