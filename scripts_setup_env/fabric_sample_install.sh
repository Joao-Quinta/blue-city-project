#!/bin/bash
sudo rm -f install-fabric.sh
sudo rm -rf fabric-samples
curl -sSLO https://raw.githubusercontent.com/hyperledger/fabric/main/scripts/install-fabric.sh && chmod +x install-fabric.sh
./install-fabric.sh d s b

sudo rm -f install-fabric.sh
sudo chown -R $1: fabric-samples
