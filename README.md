# SDK Composer
The SDK Composer is a sub-module of the SDK toolkit 
# Scope
The composer allows the vertical user to compose services and perform CRUD operations on these. The vertical user will have the possibility to publish the created service into the Application Catalogue, after an internal process of validation and translation into the ETSI TOSCA model. 
# Full deployment
For a full deployment of the SDK, please follow the instruction at: [Full Deployment guide-line]

# API Usage
 - Get Functions: List of available functions to be used during Service composition
    - Endpoint: **GET /sdk/composer/functions**
    As a response body will have a Array<SdkFunction>, in JSON format, with all the Sdk Functions present in the local database.
    
 - Get Services: List of the available services on the SDK
    - Endpoint: **GET /sdk/composer/services**
    As a response body will have a Array<SdkServices>, in JSON format, with all the Sdk Services present in the local database.

 - Create Service
    - Endpoint: **POST /sdk/composer/services**
    - Expected: 
        - {{***serviceId***}}:  Id of the created service as body response
        - ***Status code***: 201 Created
    - Body
    ```json
    {
    "name": "5GCITY-NS2-test",
    "version": "0.2",
    "designer": "NXW",
    "parameter": ["residents", "isVideoService"],
    "license": {
    "type": "PUBLIC",
    "url": "http://example.org"
    },
    "link": [{
        "name": "Management_net",
        "connection_point_names": ["MGMT", "OUTSIDE"]
      }, {
        "name": "Central_service_net",
        "connection_point_names": ["DCNET"]
      }, {
        "name": "External_net",
        "connection_point_names": ["EXT"]
      }, {
        "name": "Media_presentation_net",
        "connection_point_names": ["MEDIA_FW", "MEDIA_WEB"]
      }],
      "component": [{
        "component_id": 1,
        "component_type": "SDK_FUNCTION",
        "mapping_expression": ["FLOOR(residents/1000)", "1"]
      }, {
        "component_id": 4,
        "component_type": "SDK_FUNCTION",
        "mapping_expression": ["1"]
      }],
      "connection_point": [{
        "name": "OUTSIDE",
        "type": "EXTERNAL",
        "required_port": [80, 22]
      }, {
        "name": "MGMT",
        "type": "INTERNAL",
        "internal_cp_id": 8,
        "required_port": []
      }, {
        "name": "DCNET",
        "type": "INTERNAL",
        "internal_cp_id": 5,
        "required_port": []
      }, {
        "name": "EXT",
        "type": "INTERNAL",
        "internal_cp_id": 6,
        "required_port": [80, 22, 443]
      }, {
        "name": "MEDIA_FW",
        "type": "INTERNAL",
        "internal_cp_id": 7,
        "required_port": []
      }, {
        "name": "MEDIA_WEB",
        "type": "INTERNAL",
        "internal_cp_id": 2,
        "required_port": [80, 443]
      }],
      "l3_connectivity": [{
        "connectionPointId": "MGMT",
        "l3Rules": [{
          "protocol": "TCP",
          "src_ip": "0.0.0.0",
          "src_port": 8000,
          "dst_ip": "10.0.0.42",
          "dst_port": 8998
        }]
      }]
      }
    ```
  
 - Delete Service
    - Endpoint: **DELETE /sdk/composer/services/{serviceId}**
    
 - Create Service Descriptor
    - Endpoint: **POST /sdk/composer/services/{serviceId}/create-descriptor**
    - Expected
        - {{***serviceDescriptorId***}}: Id of the Service Descriptor created 
        - ***Status Code***: 201 Created
    - Body: 
    ```json
    {
        "parameterValues": [ 10000, 1]
    }
    ```

 - GET Service Descriptors 
    - Endpoint: **POST /sdk/composer/service-descriptor/**
    - Body: 
    ```sh
    [
        { "id": 29, ...}, 
        { "id": 32, ...}
    ]
    ```
 - GET Service Descriptor 
    - Endpoint: **POST /sdk/composer/service-descriptor/{{serviceDescriptorId}}**
    - Body: 
    ```json
    {
      "id": 32,
      "status": "SAVED",
      "component_type": "SDK_SERVICE",
      "parameters": {
        "residents": 10000,
        "isVideoService": 1
      },
      "sub_descriptor": [{
          "id": 33,
          "template": {
            "id": 1,
            "name": "miniweb-server",
            "description": "Mini web Server.",
            "vendor": "NXW",
            "version": "v1.0",
            "parameter": [
              "isVideo",
              "size"
            ],
            "vnfdId": "057289e2-7b8e-4280-8734-43b924f64b85",
            "vnfd_version": "v1.0",
            "flavour_expression": "IF(isVideo != 0, video_srv_flv, standard_srv_flv)",
            "instantiation_level_expression": "IF(size <= 1, small_il, IF(size <= 10, medium_il, big_il))",
            "connection_point": [{
              "id": 2,
              "name": "cp-eth0",
              "type": "EXTERNAL",
              "required_port": [
                80,
                443
              ]
            }],
            "monitoring_parameter": [{
              "name": "AVERAGE_MEMORY_UTILIZATION",
              "direction": "LOWER_THAN",
              "threshold": 102
            }]
          },
          "component_type": "SDK_FUNCTION"
        },
        {
          "id": 34,
          "template": {
            "id": 4,
            "name": "vFirewall-v5",
            "description": "Virtual Firewall",
            "vendor": "NXW",
            "version": "v5.0",
            "parameter": [
              "traffic"
            ],
            "vnfdId": "a49ef787-aaba-4a06-a677-b30a2e883562",
            "vnfd_version": "v5.0",
            "flavour_expression": "static_df",
            "instantiation_level_expression": "IF(traffic != 0, big_il, medium_il)",
            "metadata": {
              "cloud-init": "#!/bin/vbash\nsource /opt/vyatta/etc/functions/script-template\nconfigure\nset interfaces ethernet eth1  address 192.168.200.1/24\ncommitexit"
            },
            "connection_point": [{
                "id": 8,
                "name": "eth0",
                "type": "EXTERNAL",
                "required_port": []
              },
              {
                "id": 7,
                "name": "eth1",
                "type": "EXTERNAL",
                "required_port": []
              },
              {
                "id": 6,
                "name": "eth3",
                "type": "EXTERNAL",
                "required_port": [
                  80,
                  22,
                  443
                ]
              },
              {
                "id": 5,
                "name": "eth2",
                "type": "EXTERNAL",
                "required_port": []
              }
            ],
            "monitoring_parameter": [{
              "name": "AVERAGE_MEMORY_UTILIZATION",
              "direction": "LOWER_THAN",
              "threshold": 142
            }]
          },
          "component_type": "SDK_FUNCTION"
        }
      ]
      }
    ```
    
 - Delete Service Descriptor
    - Endpoint: **DELETE /sdk/composer/service-descriptor/{serviceDescriptorId}**

 - Publish Service Descriptor
    - Endpoint: **POST /sdk/composer/service-descriptor/{serviceDescriptorId}/publish**
    - Exceptation:
        - ***Status Code***: 202 Accepted


# Development
To contribute to the development of the 5GCity SDK, you may use the very same development workflow as for any other 5GCity Github project. That is, you have to fork the repository and create pull requests.

# Usage
Once the server is running, please check the API Swagger documentation visiting the link: http://0.0.0.0:8081/swagger-ui.html 

# Testing
In order to test the SDK Composer the user need to run the service sdk-composer.service created during the deployment phase.
An instance of the public catalogue is needed in case the publication will be tested.
Since the SDK Functions will be created in the Editor (TBC), the Composer comes up with two preinstantiated SDK Functions for these test purposes. The two functions are the VNF which compose the NS2 of the UC1:
 - Miniwebcache
 - Firewall

The steps to run the tests are the following:
- Get Functions
	```sh
    $ ./get_functions.sh -h localhost -p 8081
	```
	-h: Host where the SDK Composer is running
	-p: SDK Composer listening port
In console will be returned the list of the SDK Functions present in the local database.
*Please take in mind that the IDs may change in different installation. This may reflect the result of the service creation operation. The user may need to adjust the json file of the service in terms of Components composing the Service (id of the functions) and the connection points ids.*
 - Create Service
    ```sh
    $ ./create_service.sh -b json/service.json -h localhost -p 8081
    ```
    -h: Host where the SDK Composer is running
    -p: SDK Composer listening port
    -b: body payload with the composed descriptor
In console will be returned the identifier of the created service (***serviceId***)
 - Create Service Descriptor
    ```sh
    $ ./create_service_descriptor.sh -b json/service_descriptor.json -i serviceId -h localhost -p 8081
    ```
	-h: Host where the SDK Composer is running
	-p: SDK Composer listening port
    -b: Body payload
    -i: Service Identifier (***serviceId***)
In console will be returned the identifier of the created service descriptor (***serviceDescriptorId***)
 - Publish to Catalogue
    ```sh
    $ ./publish.sh -i serviceDescriptorId -h localhost -p 8081
    ```
    -h: Host where the SDK Composer is running
	-p: SDK Composer listening port
    -i: Service Descriptor Identifier (***serviceDescriptorId***)
By publishing to catalogue, the new Service will be present in the public catalogue and if any MANO plugin is enabled in the Catalogue, the new NSD will be pushed into it.

# License
The 5GCity SDK Composer is published under Apache 2.0 license. Please see the LICENSE file for more details

# Lead developers
The following lead developers are responsible for this repository and have admin rights. They can, for example, merge pull requests.
- Elian Kraja [Nextworks S.r.l.]
- Paolo Cruschelli [Nextworks S.r.l.]

# Feedback-Channel
Please use the GitHub issues for feedback. 


[//]: #
[Full Deployment guide-line]: https://github.com/5GCity/5GCity-SDK-Composer/blob/devel/doc/README.md
[Nextworks S.r.l.]: http://www.nextworks.it/
