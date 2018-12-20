#!/bin/bash

usage() { 
	echo "Usage: $0 -d database -u username -p password -h cataloguehost" 1>&2; 
	echo "Example: $0 -d sdk -u root -p passwd -h http://127.0.0.1:8083/"
	exit 1; 
}

while getopts ":u:p:h:d:" o; do
    case "${o}" in
        u)
            u=${OPTARG}
            ;;
        p)
            p=${OPTARG}
            ;;
		h)
			h=${OPTARG}
			;;
		d)
			d=${OPTARG}
			;;	
		*)
            usage
            ;;
    esac
done
shift $((OPTIND-1))

if [ -z "${u}" ] || [ -z "${p}" ] || [ -z "${h}" ] || [ -z "${d}" ]; then
    usage
fi

currdir=$(pwd)

##Configuring database
echo -e "Database configuration"
sudo su -c 'configure-mariadb.sh ${d} ${u} ${p}' 


## Create packages directory which will contain jar packages
echo -e "Creating configurations directory in /home/$USER/"
mkdir -p /home/$USER/SDK/COMPOSER/target/


## Move application properties to configation directory
echo -e "Copying application.properties of the SDK Composer to the configs directory"
cp ${currdir}/src/composer/src/main/resources/application.properties.template /home/$USER/SDK/COMPOSER/application.properties

## Change data on application properties
echo -e "Parameterizing the application.properties with the provided data"
sed -i "s/_DB_/"${d}"/g" /home/$USER/SDK/COMPOSER/application.properties
sed -i "s/_DBUSER_/"${u}"/g" /home/$USER/SDK/COMPOSER/application.properties
sed -i "s/_DBPASS_/"${p}"/g" /home/$USER/SDK/COMPOSER/application.properties
sed -i "s|_CATALOGUE_|"${h}"|g" /home/$USER/SDK/COMPOSER/application.properties

## To avoid compile issue
cp /home/$USER/SDK/COMPOSER/application.properties ${currdir}/src/composer/src/main/resources/application.properties


## Check if DB service is up and running 
# echo -e " Skipping mariadb check ... supposed to be up from prerequirements"

## Compile SDK-INFO-MODEL library
echo -e "Compiling Sdk-Info-Model library"
cd ${currdir}/src/sdk-info-model/ && mvn clean install 
if [[ "$?" -ne 0 ]] ; then
  echo 'could not perform library installation'; exit $rc
fi

## Building SDK Composer package
echo -e "Building SDK Composer package"
cd ${currdir}/src/composer/ && mvn -DskipTests clean package 
if [[ "$?" -ne 0 ]] ; then
  echo 'could not perform packaging of the SDK Composer'; exit $rc
fi

## Moving jar to running directory
echo -e "Moving package to packages directory"
cp ${currdir}/src/composer/target/composer-0.0.2.jar /home/$USER/SDK/COMPOSER/target/.


## Create service file for the SDK Composer
echo "[Unit]" > /home/$USER/SDK/COMPOSER/sdk-composer.service
echo "Description=5G-City SDK Composer" >> /home/$USER/SDK/COMPOSER/sdk-composer.service
echo "After=syslog.target network-online.target" >> /home/$USER/SDK/COMPOSER/sdk-composer.service
echo "" >> /home/$USER/SDK/COMPOSER/sdk-composer.service
echo "[Service]" >> /home/$USER/SDK/COMPOSER/sdk-composer.service
echo "User=ubuntu" >> /home/$USER/SDK/COMPOSER/sdk-composer.service
echo "Restart=on-failure" >> /home/$USER/SDK/COMPOSER/sdk-composer.service
echo "ExecStart=/usr/bin/java -jar /home/$USER/SDK/COMPOSER/target/composer-0.0.2.jar --spring.config.location=file:/home/$USER/SDK/COMPOSER/" >> /home/$USER/SDK/COMPOSER/sdk-composer.service
echo "RestartSec=3" >> /home/$USER/SDK/COMPOSER/sdk-composer.service
echo "" >> /home/$USER/SDK/COMPOSER/sdk-composer.service
echo "[Install]" >> /home/$USER/SDK/COMPOSER/sdk-composer.service
echo "WantedBy=multi-user.target" >> /home/$USER/SDK/COMPOSER/sdk-composer.service


## Linking file to the services list
echo -e "Linking sdk-composer.service to services"
sudo systemctl enable /home/$USER/SDK/COMPOSER/sdk-composer.service

echo -e "In order to activate/deactivate the service run: \n$ sudo systemctl start|stop sdk-composer.service"




