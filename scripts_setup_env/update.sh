#!/bin/bash

# Update the package lists for upgrades and new package installations
sudo apt-get update

# Upgrade all the installed packages
sudo apt-get upgrade -y

# Remove unused packages
sudo apt-get autoremove -y

