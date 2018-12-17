# Full Deployment of SDK Composer

Disclaimer: This guide is still being reviewed and all issues and suggestions regarding possible troubles arisen trying to follow the steps will be welcome for further troubleshooting. Please note that this project and some dependencies are in early development stages and should not be deployed in a real production environment.
This document aims to provide accurate instructions about how to deploy an instance of the 5GCity-SDK-Composer integrated with the 5G Apps and Services Catalogue.

# Requirements
Two physical/virtual machines for **SDK Composer** and **5G Apps and Services Catalogue**. An extra physical/VM machine will be needed in case the 5G Apps and Services Catalogue will be configured with a **MANO Orchestrator (OSM R3)**. 
Network connectivity is requested between ***SDK Composer*** and ***5G Apps and Services Catalogue***, as well as between ***5G Apps and Services Catalogue*** and ***OSM***. 

# SDK Composer

# Prerequisites

A physical or virtual machine running ubuntu 16.04, with the following software:
 - Generic updates
    ```sh
    $ sudo apt update
    $ sudo apt upgrade
    ```

 - Systemd, git 
    ```sh
   $ sudo apt install systemd git
    ```

 - Java 1.8
    ```sh
    $ sudo add-apt-repository ppa:webupd8team/java
    $ sudo apt update
    $ sudo apt install oracle-java8-installer
    ```

    Should be configured a sql user and a database. These parameters need to be applied to the application.properties of the SDK Composer

 - Maven 3.3.9
    ```sh
    $ sudo su
    # wget http://apache-mirror.rbc.ru/pub/apache/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.tar.gz
    # tar -xvzpf apache-maven-3.3.9-bin.tar.gz
    # mkdir -p /opt/maven/3.3.9
    # mv apache-maven-3.3.9/* /opt/maven/3.3.9/
    # ln -s /opt/maven/3.3.9/ /opt/maven/current
    ```

    Add to the ~/.bashrc the following lines
    ```sh
    export MAVEN_HOME=/opt/maven/current
    export PATH=$PATH:$MAVEN_HOME/bin
    ```

    Load the new source
    ```sh
    $ source ~/.bashrc
    ```

 - MySQL (MariaDB 10.1.x or higher)
    ```sh
    $ sudo apt install mariadb-server 
    ```
    [Mariadb] - Create new database in MariaDB

The script [bootstrap-deps.sh] can be run in order to meet the prerequisites.

# SDK Catalogue Installation Guide
 - NFV SOL001 LIBS
    ```sh
    $ git clone https://github.com/nextworks-it/nfv-sol-libs/
    $ cd  nfv-sol-libs
    $ git checkout v1.0
    $ ./install_nfv_sol_libs.sh
    ```

 - SDK Composer
    ```sh
    $ git clone https://github.com/5GCity/5GCity-SDK-Composer
    $ cd 5GCity-SDK-Composer/src
    $ chmod +x bootstrap.sh
    $ ./bootstrap.sh -u user -p password -d database -h host 
    ```

    *The bootstrap script will install the required libraries, will package the sdk composer and create a service ready to be launched. The configuration file (application.properties) of the Composer will contain the data for database connection and catalogue host.*

# 5G Apps and Services Catalogue

*Please refer to installation guide of the [5G Apps and Services Catalogue]*

# OSM Release THREE (OPTIONAL)

*Please refer to the installation guide of the [OSM Rel THREE]*

# Deployment

The configuration file of the SDK Composer module is located at: 

  ```sh
  /home/$USER/configs/application.properties
  ```

You can change any of the following parameters:
```sh
spring.datasource.url=jdbc:mysql://**localhost**/**sdk**
spring.datasource.username=**catalogue**
spring.datasource.password=**password**
catalogue.host=**http://5gcatalogue.5gcity.eu/**
```

Perform a restart of the service in order to apply changes
```sh
$ sudo systemctl restart sdk-composer.service

```

[//]: #
[Mariadb]: https://www.digitalocean.com/community/tutorials/how-to-create-and-manage-databases-in-mysql-and-mariadb-on-a-cloud-server
[5G Apps and Services Catalogue]: https://github.com/nextworks-it/5g-catalogue
[OSM Rel THREE]: https://osm.etsi.org/wikipub/index.php/OSM_Release_THREE#Install_OSM
[bootstrap-deps.sh]: https://github.com/5GCity/5GCity-SDK-Composer/blob/devel/bootstrap-deps.sh
