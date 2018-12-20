#!/bin/bash


usage() { 
	echo "Usage: $0 -h host -p port -i serviceDescriptorId " 1>&2; 
	echo "Example: $0 -h localhost -p 8081 -i 55 "
	exit 1; 
}

while getopts ":i:h:p:" o; do
    case "${o}" in
        i)
            id=${OPTARG}
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

if [ -z "${id}" ] || [ -z "${host}" ] || [ -z "${port}" ] ; then
    usage
fi


echo "Publishing to the catalogue"
response=$(curl -s --request POST --url http://${host}:${port}/sdk/composer/service-descriptor/${id}/publish)
if [ -z "${response}" ]; then
	echo "Publication successful"
fi



echo ${response} 

exit 0
