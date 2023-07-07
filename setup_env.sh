#!/bin/bash
#sudo bash -c "source ./setup_env.sh fabric"
# Check if we are running as root
if [[ $EUID -eq 0 ]]; then
    echo "The script is running with root privileges."
else
    echo "The script is not running with root privileges.  Usage: sudo bash -c \"source ./setup_env.sh <username>\""
    exit 1
fi

# Check if argument 1 were passed
if [ -z "$1" ]
then
  echo "Error: No username provided. Usage: sudo bash -c \"source ./setup_env.sh <username>\""
  exit 1
fi

if [ $1 = "help" ];then
	echo "sudo bash -c \"source ./setup_env.sh <username>\""
	exit 1
fi

chmod +x scripts_setup_env/update.sh
chmod +x scripts_setup_env/prerequisite_software.sh
chmod +x scripts_setup_env/fabric_sample_install.sh
chmod +x scripts_setup_env/api_setup.sh

source ./scripts_setup_env/update.sh

source ./scripts_setup_env/prerequisite_software.sh $1

source ./scripts_setup_env/update.sh

source ./scripts_setup_env/fabric_sample_install.sh $1

source ./scripts_setup_env/update.sh

source ./scripts_setup_env/api_setup.sh

source ./scripts_setup_env/update.sh

source ./scripts_setup_env/paths.sh

source ./scripts_setup_env/update.sh

echo "#######"
echo verify installations:  
echo "Path should contain both -- /usr/local/go/bin -- && -- /usr/local/gradle/bin --"
echo $PATH
echo ""
echo docker --version  
echo docker-compose --version  
echo cat ~/.bashrc
echo go version 
echo java --version
echo gradle -v


