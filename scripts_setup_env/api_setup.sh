#!/bin/bash

sudo apt install default-jdk -y

sudo rm -f gradle-8.1.1-bin.zip
sudo rm -rf gradle-8.1.1
sudo rm -rf /usr/local/gradle
sudo wget https://services.gradle.org/distributions/gradle-8.1.1-bin.zip

sudo unzip gradle-8.1.1-bin.zip && mv gradle-8.1.1 /usr/local/gradle
sudo rm -f gradle-8.1.1-bin.zip



