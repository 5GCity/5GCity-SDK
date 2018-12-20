#!/bin/bash


usage() { 
	echo "Usage: $0 -h host -p port -b service " 1>&2; 
	echo "Example: $0 -h localhost -p 8081 -b jsons/service.json"
	exit 1; 
}

while getopts ":b:h:p:" o; do
    case "${o}" in
        b)
            body=${OPTARG}
            ;;
        h)
            host=${OPTARG}
            ;;
        p)
            port=${OPTARG}
            ;;
		*)
            usage
            ;;
    esac
done
shift $((OPTIND-1))

if [ -z "${body}" ] || [ -z "${host}" ] || [ -z "${port}" ] ; then
    usage
fi


echo "Creating a new service }"
response=$(curl -sb --request POST --header 'Content-Type: application/json' --url http://${host}:{port}/sdk/composer/services/ --data @${body})

echo ${response} 

exit 0