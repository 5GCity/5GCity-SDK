**************************************************
        Validation tool: swagger-cli
**************************************************

------------------------------------
        Prerequisites
------------------------------------

1. Make sure you have Node 6.x installed

        $ apt-cache policy nodejs

If you do not see entries like this and only see 4.x, create a new file:

        $ sudo nano /etc/apt/sources.list.d/nodesource.list

and put inside the file

        deb https://deb.nodesource.com/node_6.x xenial main
        deb-src https://deb.nodesource.com/node_6.x xenial main


Download the GPG Signing Key from Nodesource for the repository.

        $ curl -s https://deb.nodesource.com/gpgkey/nodesource.gpg.key | sudo apt-key add -

        $ sudo apt-get update.

        $ sudo apt-get install nodejs


2. Make sure you have NPM 3.x installed

        $ sudo apt-get install npm



------------------------------------
        Install swagger-cli
------------------------------------
        $ npm install [-g] swagger-cli

To validate json:
        $ swagger-cli validate openapi3.json

To generate single file api:
        $ swagger-cli bundle openapi3.json > fullapi.json




**************************************************
        OPEN API Editing
**************************************************

------------------------------------
        Install swagger-editor
------------------------------------
        $git clone https://github.com/swagger-api/swagger-editor.git
        $cd swagger-editor
        $npm install
        $npm run build
        $npm start

