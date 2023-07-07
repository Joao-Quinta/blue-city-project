#!/bin/bash

# Paths to be added
gradle_path="/usr/local/gradle/bin"
go_path="/usr/local/go/bin"

# Check if the paths are in the PATH
if [[ ":$PATH:" != *":$gradle_path:"* ]]; then
    echo 'export PATH=$PATH:/usr/local/gradle/bin' >> ~/.bashrc
fi

if [[ ":$PATH:" != *":$go_path:"* ]]; then
    echo 'export PATH=$PATH:/usr/local/go/bin' >> ~/.bashrc
fi






