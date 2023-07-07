# Blue City Fabric -- FULL SETUP -- ubuntu-22.04.2-desktop-amd64.iso
  
## 1  
  
chmod 755 setup_env.sh  
RUN:  
sudo bash -c "source ./setup_env.sh username"  
In order to set up the environment  
  
verify installations:  
docker --version  
docker-compose --version  
go version
java --version    
gradle -v  
  
These might not work, just because the PATH wasn't correctly updated.  
To fix this execute the script: sudo ./scripts_setup_env/paths.sh  
Afterwards close the terminal window and open a new one.  
  
## 2
  
At this point everything required is installed, now just need to launch:  
(1) ./fabric_samples_addons/my_shell_scripts/mySetup.sh all  
  
## 3 - ongoing  
  
Go to page: http://localhost:8080  
Login username: exploreradmin  
Login password: exploreradminpw  
  
## 4
  
Install/deploy chaincode:  
(1) ./fabric_samples_addons/my_shell_scripts/chaincodeSetup.sh  
  
## 5
  
Invoke chaincode as follows:  
(1) cd java_client_invoke_sc  
(2) gradle build & gradle run  
  


