#!/bin/bash


usage() { 
	echo "Usage: $0 -h host -p port -b parameters -i serviceId " 1>&2; 
	echo "Example: $0 -h localhost -p 8081 -b jsons/descriptor_parameters.json -i 35"
	exit 1; 
}

while getopts ":b:i:h:p:" o; do
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
		i)
            id=${OPTARG}
            ;;
		*)
            usage
            ;;
    esac
done
shift $((OPTIND-1))

if [ -z "${body}" ] || [ -z "${id}" ] || [ -z "${host}" ] || [ -z "${port}" ] ; then
    usage
fi


echo "Creation service descriptor related to service di: ${id}"
response=$(curl -sb --request POST --header 'Content-Type: application/json' --url http://${host}:${port}/sdk/services/${id}/create-descriptor --data @${body})

echo ${response}

exit 0
