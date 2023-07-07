#!/bin/bash
# 

CURRENT_PATH=$(pwd)

SRC_DIR="$CURRENT_PATH/../networkConfiguration/organizations/cryptogen"
TARGET_DIR="$CURRENT_PATH/../../fabric-samples/test-network/organizations/cryptogen"

if [ $1 = "reset" ];then
	SRC_DIR="$CURRENT_PATH/../networkConfiguration/organizationsDefault/cryptogen"
fi

rm -r "$TARGET_DIR"/*
find "$SRC_DIR" -name '*.yaml' -exec cp {} "$TARGET_DIR" \;



SRC_DIR="$CURRENT_PATH/../networkConfiguration/configtx"
TARGET_DIR="$CURRENT_PATH/../../fabric-samples/test-network/configtx"

if [ $1 = "reset" ];then
	SRC_DIR="$CURRENT_PATH/../networkConfiguration/configtxDefault"
fi

rm -r "$TARGET_DIR"/*
cp -r "$SRC_DIR"/* "$TARGET_DIR"
