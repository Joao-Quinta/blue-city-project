#!/bin/bash
# modes: help - off - on

FOLDER_NAME=$1

cd "../chaincode/${FOLDER_NAME}"

rm go.mod
rm go.sum
rm -rf vendor
go mod init fabric-chaincode
go mod tidy
GO111MODULE=on go mod vendor
