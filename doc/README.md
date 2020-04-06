# Full Deployment of 5GCity SDK

This document aims to provide accurate instructions about how to deploy an instance of the 5GCity SDK integrated with the 5G Apps & Services Catalogue.

# Requirements
Two physical/virtual machines for **5GCity SDK** and **5G Apps & Services Catalogue**.
Network connectivity is requested between **5GCity SDK** and **5G Apps & Services Catalogue**.

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
    $ chmod +x install_nfv_sol_libs.sh
    $ ./install_nfv_sol_libs.sh
    ```
The script [bootstrap-deps.sh] can be run in order to meet the prerequisites.

## Installation Guide
 - 5GCity SDK
    ```sh
    $ git clone https://github.com/5GCity/5GCity-SDK
    $ cd 5GCity-SDK
    $ cd scripts/config/
    $ chmod +x bootstrap.sh
    $ ./bootstrap.sh -h catalogue_host 
    ```

    *The bootstrap script will install the required libraries, will package the 5GCity SDK and create a service ready to be launched. The configuration file (application.properties) of the 5GCity SDK will contain the data for database connection and catalogue host.*

# 5G Apps & Services Catalogue
*Please refer to installation guide of the [5G Apps & Services Catalogue]*

# Deployment
The configuration file of the 5GCity SDK module is located at: 

  ```sh
  /home/$USER/5GCity-SDK/application.properties
  ```
Users have the possibility to change few configuration parameters via configuration file.

Perform a restart of the service in order to apply changes

  ```sh
  $ sudo systemctl restart 5gcity-sdk.service
  ```

## 5G Apps & Services Catalogue Configuration

The 5G Apps & Services Catalogue can be configured using the application.properties configuration file:
  
  ```
  ### CATALOGUE ###
  catalogue.host=http://localhost:8083/
  ```

If the bootstrap.sh script is used for the installation of the 5GCity SDK, use the option -h to pass the 5G Apps and Services Catalogue URL:
  
  ```
  $ ./bootstrap.sh -h http://localhost:8083/
  ```

## Authentication and Authorization
In the 5GCity Platform, authentication and authorization are handled by other sub-module.
However, authentication and authorization might be enabled directly in the 5GCity SDK modifying the application.properties configuration file:
  
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
[5G Apps & Services Catalogue]: https://github.com/nextworks-it/5g-catalogue
[bootstrap-deps.sh]: ../scripts/config/bootstrap-deps.sh
[5GCity-SDK-Information]: 5GCity-SDK-Information.txt
