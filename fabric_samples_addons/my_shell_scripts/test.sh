#!/bin/bash
# modes: help - off - on

echo ${PWD}


cd ..

echo ${PWD}




peer version
peer lifecycle chaincode package simple.tar.gz --path ../chaincode/ --lang golang --label simple_1.0

#set as org 1 and install
./my_shell_scripts/org1Variables.sh
peer lifecycle chaincode install simple.tar.gz

#set as org 2 and install
./my_shell_scripts/org2Variables.sh
peer lifecycle chaincode install simple.tar.gz

CC_PACKAGE_ID=$(peer lifecycle chaincode queryinstalled)
echo $CC_PACKAGE_ID



#set as org 1 and install
source ./my_shell_scripts/org1Variables.sh
peer lifecycle chaincode install simple.tar.gz

#set as org 2 and install
source ./my_shell_scripts/org2Variables.sh
peer lifecycle chaincode install simple.tar.gz
