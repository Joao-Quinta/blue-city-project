#!/bin/bash
# modes: help - off - on

FOLDER_NAME=$1

./prepChaincode.sh $1
cd ../../fabric-samples/test-network
export PATH=${PWD}/../bin:$PATH
export FABRIC_CFG_PATH=$PWD/../config/
peer version


rm -f "${FOLDER_NAME}.tar.gz"
peer lifecycle chaincode package "${FOLDER_NAME}.tar.gz" --path "../../fabric_samples_addons/chaincode/${FOLDER_NAME}" --lang golang --label "${FOLDER_NAME}_1.0"

#set as org 1 and install
source ./../../fabric_samples_addons/my_shell_scripts/org1Variables.sh
peer lifecycle chaincode install "${FOLDER_NAME}.tar.gz"

#set as org 2 and install
source ./../../fabric_samples_addons/my_shell_scripts/org2Variables.sh
peer lifecycle chaincode install "${FOLDER_NAME}.tar.gz"


CC_PACKAGE_ID=$(peer lifecycle chaincode queryinstalled | grep -oP 'Package ID: \K[^,]*')


#set as org 1 and approve
source ./../../fabric_samples_addons/my_shell_scripts/org1Variables.sh
peer lifecycle chaincode approveformyorg -o localhost:7050 --ordererTLSHostnameOverride orderer.example.com --channelID mychannel --name $FOLDER_NAME --version 1.0 --package-id $CC_PACKAGE_ID --sequence 1 --tls --cafile "${PWD}/organizations/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem"

#set as org 2 and approve
source ./../../fabric_samples_addons/my_shell_scripts/org2Variables.sh
peer lifecycle chaincode approveformyorg -o localhost:7050 --ordererTLSHostnameOverride orderer.example.com --channelID mychannel --name $FOLDER_NAME --version 1.0 --package-id $CC_PACKAGE_ID --sequence 1 --tls --cafile "${PWD}/organizations/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem"

#check 
peer lifecycle chaincode checkcommitreadiness --channelID mychannel --name $FOLDER_NAME --version 1.0 --sequence 1 --tls --cafile "${PWD}/organizations/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem" --output json

#set as org 1 and commit
source ./../../fabric_samples_addons/my_shell_scripts/org1Variables.sh
peer lifecycle chaincode commit -o localhost:7050 --ordererTLSHostnameOverride orderer.example.com --channelID mychannel --name $FOLDER_NAME --version 1.0 --sequence 1 --tls --cafile "${PWD}/organizations/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem" --peerAddresses localhost:7051 --tlsRootCertFiles "${PWD}/organizations/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt" --peerAddresses localhost:9051 --tlsRootCertFiles "${PWD}/organizations/peerOrganizations/org2.example.com/peers/peer0.org2.example.com/tls/ca.crt"


#check if commited
peer lifecycle chaincode querycommitted --channelID mychannel --name $FOLDER_NAME
