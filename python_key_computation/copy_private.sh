#!/bin/bash


current_path=$(pwd)
from_path_1="${current_path}/../fabric-samples/test-network/organizations/peerOrganizations/org1.example.com/users/User1@org1.example.com/msp/keystore/priv_sk"
from_path_2="${current_path}/../fabric-samples/test-network/organizations/peerOrganizations/org2.example.com/users/User1@org2.example.com/msp/keystore/priv_sk"
to_path_1="${current_path}/org1"
to_path_2="${current_path}/org2"

# Ensure destination directories exist
mkdir -p "$to_path_1"
mkdir -p "$to_path_2"

rm "${current_path}/org1/priv_sk"
rm "${current_path}/org2/priv_sk"

cp "$from_path_1" "$to_path_1"
cp "$from_path_2" "$to_path_2"
