#!/bin/bash
sudo git clone https://github.com/vadymmerezhko/SeleniumAWS.git
cd SeleniumAWS
sudo mvn -f rmi-pom.xml install
sudo java -jar target/SeleniumAWSRmiServer-1.0-SNAPSHOT.jar
