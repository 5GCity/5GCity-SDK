#!/bin/bash

user=$2
database=$1
passwd=$3

    mysql -e "CREATE DATABASE ${database} /*\!40100 DEFAULT CHARACTER SET utf8 */;"
    mysql -e "CREATE USER ${user}@localhost IDENTIFIED BY '${passwd}';"
    mysql -e "GRANT ALL PRIVILEGES ON ${database}.* TO '${user}'@'localhost';"
    mysql -e "FLUSH PRIVILEGES;"

exit 0
