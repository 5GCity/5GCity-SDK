#!/bin/bash

usage() { 
	echo "Usage: $0 -h cataloguehost" 1>&2; 
	echo "Example: $0 -h http://127.0.0.1:8083/"
	exit 1; 
}

while getopts ":h:" o; do
    case "${o}" in
		h)
			h=${OPTARG}
			;;	
		*)
            usage
            ;;
    esac
done
shift $((OPTIND-1))

if [ -z "${h}" ]; then
    usage
fi

currdir=$(pwd)
export MAVEN_HOME=/opt/maven/current
export PATH=$PATH:$MAVEN_HOME/bin

##Configuring database
echo -e "Database configuration"
sudo -u postgres psql -c "CREATE DATABASE fivegcitysdkdb"
sudo -u postgres psql -U postgres -d fivegcitysdkdb -c "alter user postgres with password 'postgres';"

## Create log directory
echo -e "Creating log directory in /var/log/"
sudo mkdir -p /var/log/sdk/
sudo chown $USER:$USER /var/log/sdk/

## Create packages directory which will contain jar packages
echo -e "Creating configurations directory in /home/$USER/"
mkdir -p /home/$USER/5GCity-SDK/target/

## Move application properties to configation directory
echo -e "Copying application.properties of the SDK to the configs directory"
cp ${currdir}/../../src/composer/src/main/resources/application.properties.template /home/$USER/5GCity-SDK/application.properties

## Change data on application properties
echo -e "Parameterizing the application.properties with the provided data"
sed -i "s|_CATALOGUE_|"${h}"|g" /home/$USER/5GCity-SDK/application.properties

## To avoid compile issue
cp /home/$USER/5GCity-SDK/application.properties ${currdir}/../../src/composer/src/main/resources/application.properties

## Compile SDK-INFO-MODEL library
echo -e "Compiling Sdk-Info-Model library"
cd ${currdir}/../../src/sdk-info-model/ && mvn clean install
if [[ "$?" -ne 0 ]] ; then
  echo 'could not perform library installation'; exit $rc
fi

## Building SDK Composer package
echo -e "Building SDK package"
cd ${currdir}/../../src/composer/ && mvn -DskipTests clean package
if [[ "$?" -ne 0 ]] ; then
  echo 'could not perform packaging of the SDK'; exit $rc
fi

## Moving jar to running directory
echo -e "Moving package to packages directory"
cp ${currdir}/../../src/composer/target/composer-0.0.2.jar /home/$USER/5GCity-SDK/target/.

## Create service file for the SDK Composer
echo "[Unit]" > /home/$USER/5GCity-SDK/5gcity-sdk.service
echo "Description=5GCity SDK" >> /home/$USER/5GCity-SDK/5gcity-sdk.service
echo "After=syslog.target network-online.target" >> /home/$USER/5GCity-SDK/5gcity-sdk.service
echo "" >> /home/$USER/5GCity-SDK/5gcity-sdk.service
echo "[Service]" >> /home/$USER/5GCity-SDK/5gcity-sdk.service
echo "User=${USER}" >> /home/$USER/5GCity-SDK/5gcity-sdk.service
echo "Restart=on-failure" >> /home/$USER/5GCity-SDK/5gcity-sdk.service
echo "ExecStart=/usr/bin/java -jar /home/$USER/5GCity-SDK/target/composer-0.0.2.jar --spring.config.location=file:/home/$USER/5GCity-SDK/" >> /home/$USER/5GCity-SDK/5gcity-sdk.service
echo "RestartSec=3" >> /home/$USER/5GCity-SDK/5gcity-sdk.service
echo "" >> /home/$USER/5GCity-SDK/5gcity-sdk.service
echo "[Install]" >> /home/$USER/5GCity-SDK/5gcity-sdk.service
echo "WantedBy=multi-user.target" >> /home/$USER/5GCity-SDK/5gcity-sdk.service


## Linking file to the services list
echo -e "Linking 5gcity-sdk.service to services"
sudo systemctl enable /home/$USER/5GCity-SDK/5gcity-sdk.service
sudo systemctl start 5gcity-sdk.service

echo -e "In order to activate/deactivate the service run: \n$ sudo systemctl start|stop 5gcity-sdk.service"

exit 0


