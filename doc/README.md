# Full Deployment of 5GCity SDK

Disclaimer: This guide is still being reviewed and all issues and suggestions regarding possible troubles arisen trying to follow the steps will be welcome for further troubleshooting. Please note that this project and some dependencies are in early development stages and should not be deployed in a real production environment.
This document aims to provide accurate instructions about how to deploy an instance of the 5GCity SDK integrated with the 5G App & Service Catalogue.

# Requirements
Two physical/virtual machines for **5GCity SDK** and **5G App & Service Catalogue**. 
An extra physical/VM machine will be needed in case the 5G App & Service Catalogue will be configured with a **MANO Orchestrator (OSM R5)**.
Network connectivity is requested between **5GCity SDK** and **5G App & Service Catalogue**, as well as between **5G App & Service Catalogue** and **OSM**. 

# 5GCity SDK
## Prerequisites
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
    $ sudo add-apt-repository ppa:openjdk-r/ppa
    $ sudo apt update
    $ sudo apt install openjdk-8-jdk 
    
    ```
    
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

 - Postgres (10.10 or higher)
    ```sh
    $ sudo apt install postgresql postgresql-contrib 
    
    ```
 - NFV SOL001 LIBS
    ```sh
    $ git clone https://github.com/nextworks-it/nfv-sol-libs/
    $ cd  nfv-sol-libs
    $ git checkout v2.0
    $ chmod +x install_nfv_sol_libs.sh
    $ ./install_nfv_sol_libs.sh
    
    ```
The script [bootstrap-deps.sh] can be run in order to meet the prerequisites.

## Installation Guide
 - 5GCity SDK
    ```sh
    $ git clone https://github.com/5GCity/5GCity-SDK
    $ git checkout v0.8
    $ cd 5GCity-SDK/scripts/config/
    $ chmod +x bootstrap.sh
    $ ./bootstrap.sh -h catalogue_host 
    
    ```

    *The bootstrap script will install the required libraries, will package the 5GCity SDK and create a service ready to be launched. The configuration file (application.properties) of the 5GCity SDK will contain the data for database connection and catalogue host.*

# 5G App & Service Catalogue
*Please refer to installation guide of the [5G App & Service Catalogue]*

# OSM Release FIVE (OPTIONAL)
*Please refer to the installation guide of the [OSM Rel FIVE]*

# Deployment
The configuration file of the 5GCity SDK module is located at: 

  ```sh
  /home/$USER/5GCity-SDK/application.properties
  
  ```
Users have the possibility to change few configuration parameteres via configuration file.

Perform a restart of the service in order to apply changes

  ```sh
  $ sudo systemctl restart 5gcity-sdk.service

  ```

# Authentication and Authorization
In the 5GCity Platform, authentication and authorization are handled by other sub-module.
However, authentication and authorization might be enabled directly in the 5GCity SDK modifing the application.properties configuration file:
  
  ```
  ### KEYCLOAK ###
  keycloak.enabled=true
  keycloak.realm=realm_name
  keycloak.auth-server-url=http://<keycloak_ip>:8080/auth/
  keycloak.resource=sdk_client_on_keycloak
  keycloak.public-client=false
  keycloak.credentials.secret=client_secret
  keycloak.principal-attribute=preferred_username
  ## ADMIN USER ###
  admin.user.name=admin_username
  admin.password=admin_password
  
  ```
*Please refer to [5GCity-SDK-Information] for more information* 

[//]: #
[5G App & Service Catalogue]: https://github.com/nextworks-it/5g-catalogue
[OSM Rel FIVE]: https://osm.etsi.org/wikipub/index.php/OSM_Release_FIVE#Install_OSM
[bootstrap-deps.sh]: ../scripts/config/bootstrap-deps.sh
[5GCity-SDK-Information]: 5GCity-SDK-Information.txt
