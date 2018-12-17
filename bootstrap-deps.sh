#!/bin/bash


## Update source lists
sudo apt update

echo "Install git and systemd"
sudo apt install systemd git

## Installing JDK
echo -e "Installing JDK 1.8"
sudo add-apt-repository ppa:webupd8team/java -y
sudo apt update
echo "oracle-java8-installer shared/accepted-oracle-license-v1-1 select true" | sudo debconf-set-selections
sudo apt install oracle-java8-installer -y



## Installing maven
echo -e "Installing Maven 3.3.9"
wget http://apache-mirror.rbc.ru/pub/apache/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.tar.gz
tar -xvzpf apache-maven-3.3.9-bin.tar.gz
sudo mkdir -p /opt/maven/3.3.9
sudo mv apache-maven-3.3.9/* /opt/maven/3.3.9/
sudo ln -s /opt/maven/3.3.9/ /opt/maven/current
echo 'export MAVEN_HOME=/opt/maven/current' >> ~/.bashrc
echo 'export PATH=$PATH:$MAVEN_HOME/bin' >> ~/.bashrc


## installing mariadb
echo -e "Installing MariaDB Server"
export DEBIAN_FRONTEND=noninteractive
sudo apt-get install -y mariadb-server

