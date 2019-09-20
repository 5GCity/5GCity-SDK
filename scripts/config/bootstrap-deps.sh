#!/bin/bash

cd /home/$USER/

## Update source lists
sudo apt update

echo "Install git and systemd"
sudo apt install systemd git -y

## Installing Open JDK
echo -e "Installing Open JDK 1.8"
sudo add-apt-repository ppa:openjdk-r/ppa -y
sudo apt update
sudo apt-get install openjdk-8-jdk -y

## Installing maven
echo -e "Installing Maven 3.3.9"
wget http://apache-mirror.rbc.ru/pub/apache/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.tar.gz
tar -xvzpf apache-maven-3.3.9-bin.tar.gz
sudo mkdir -p /opt/maven/3.3.9
sudo mv apache-maven-3.3.9/* /opt/maven/3.3.9/
sudo ln -s /opt/maven/3.3.9/ /opt/maven/current
echo 'export MAVEN_HOME=/opt/maven/current' >> ~/.bashrc
echo 'export PATH=$PATH:$MAVEN_HOME/bin' >> ~/.bashrc
export MAVEN_HOME=/opt/maven/current
export PATH=$PATH:$MAVEN_HOME/bin

## installing postgres
echo -e "Installing Postgres Server"
sudo apt install postgresql postgresql-contrib -y

## installing nfv-sol-libs
git clone https://github.com/nextworks-it/nfv-sol-libs/
cd  nfv-sol-libs
git checkout v2.0
chmod +x install_nfv_sol_libs.sh
./install_nfv_sol_libs.sh

cd -

exit 0
