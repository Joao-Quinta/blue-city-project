#!/bin/bash

# install docker curl and git
sudo apt-get install git curl docker-compose -y
sudo systemctl start docker
sudo usermod -a -G docker $1

# install go and add to path
sudo wget https://go.dev/dl/go1.20.5.linux-amd64.tar.gz
sudo rm -rf /usr/local/go && tar -C /usr/local -xzf go1.20.5.linux-amd64.tar.gz
sudo rm -rf go1.20.5.linux-amd64.tar.gz

# install jq
sudo apt-get install jq



