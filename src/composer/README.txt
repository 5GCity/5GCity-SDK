#################
DATABASE CREATION
#################
* $ mysql
* > create database <DATABASE-NAME> character set latin1;
* > GRANT ALL PRIVILEGES ON <DATABASE-NAME>.* TO '<USER>'@'localhost';
* > GRANT ALL PRIVILEGES ON <DATABASE-NAME>.* TO '<USER>'@'%';
