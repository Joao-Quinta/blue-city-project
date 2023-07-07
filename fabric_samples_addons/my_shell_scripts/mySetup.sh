#!/bin/bash
# WORKS --> clunky to turn off 
# modes: help - off - on

if [ $1 = "help" ];then
	echo "modes:"
	echo "off --> turns off network"
	echo "on --> turns on network and creates channel"
	echo "all --> turns on network and creates cahnnel and set up fabric explorer"
fi


#mode = off
if [ $1 = "off" ];then
	./../../fabric-samples/test-network/network.sh down
	cd ../../fabric-explorer
	docker-compose down
fi

#mode = on
if [ $1 = "on" ];then
	./../../fabric-samples/test-network/network.sh down
	./../../fabric-samples/test-network/network.sh up
	./../../fabric-samples/test-network/network.sh createChannel
fi

#mode = all
if [ $1 = "all" ];then
	./../../fabric-samples/test-network/network.sh down
	./../../fabric-samples/test-network/network.sh up
	./../../fabric-samples/test-network/network.sh createChannel
	cd ../../fabric-explorer
	docker-compose down
	docker system prune -f
	docker volume prune -f
	docker container prune -f
	rm -rf organizations
	cp -r ../fabric-samples/test-network/organizations/ .
	docker-compose up
fi
	


