#!/bin/bash

usage() { 
	echo "Usage: $0 -h host -p port" 1>&2; 
	echo "Example: $0 -h localhost -p 8081"
	exit 1; 
}

while getopts ":h:p:" o; do
    case "${o}" in
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

if [ -z "${host}" ] || [ -z "${port}" ] ; then
    usage
fi

response=$(curl -sb -H "Accept: application/json" --url http://${host}:${port}/sdk/composer/functions)

echo ${response} | python -m json.tool

exit 0
