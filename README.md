# 5GCity SDK
The 5GCity SDK is a sub-module of the 5GCity Platform

# Scope
The 5GCity SDK allows the vertical user to compose services and functions, and perform CRUD operations on these. The vertical user will have the possibility to publish the created services or functions into the 5G Apps & Services Catalogue, after an internal process of validation and translation into the ETSI TOSCA model. 

# Full deployment
For a full deployment of the 5GCity SDK, please follow the instruction at: [Full Deployment guide-line]

# API Usage
For the swagger documentation of the 5GCity SDK, please refer to: [Swagger documentation]
## SDK Slice
The term Slice in the context of 5GCity SDK represents the concept of Project.
 
 - Get Slices
    - Endpoint: **GET /sdk/slicemanagement/slices**  
    - Response:
		- As a response body will have an Array<SliceResource>, in JSON format, with all the Sdk Slices in the local database.
		- ***Status code***: 200 OK
		
 - Get Slices per user
    - Endpoint: **GET /sdk/slicemanagement/slices/?user="user_id"**  
    - Response:
		- As a response body will have an Array<SliceResource>, in JSON format, with all the Sdk Slices user can access.
		- ***Status code***: 200 OK
		
 - Get Slice  
    - Endpoint: **GET /sdk/slicemanagement/slices/{sliceId}**
    - Response:
		- As a response body will have the specified SliceResource, in JSON format.
		- ***Status code***: 200 OK
		
 - Create Slice
    - Endpoint: **POST /sdk/slicemanagement/slices**
    - Body
    ```json
    {
		"sliceDescription": "Test slice",
		"sliceId": "slice_test"
	}
    ```	
    - Response:   
        - As a response body will have the created SliceResource, in JSON format. The slice is created also in the 5G App & Service Catalogue. 
        - ***Status code***: 201 Created  

 - Delete Slice
    - Endpoint: **DELETE /sdk/slicemanagement/slices/{sliceId}**
    - Response:
		- ***Status code***: 204 No Content
		
 - Add a User to a Slice
	- Endpoint: **PUT /sdk/slicemanagement/slices/{sliceId}/users/{userName}**
    - Response:
		- ***Status code***: 200 OK

 - Delete a User from a Slice
	- Endpoint: **DELETE /sdk/slicemanagement/slices/{sliceId}/users/{userName}**
    - Response:
		- ***Status code***: 200 OK
		
## SDK Functions 
 - Get Functions
    - Endpoint: **GET /sdk/functions/?sliceId="slice_id"**  
    - Response:
		- As a response body will have an Array<SdkFunction>, in JSON format, with all the Sdk Functions present in the local database.
		- ***Status code***: 200 OK
		
 - Get Function  
    - Endpoint: **GET /sdk/functions/{functionId}**
    - Response:
		- As a response body will have the specified SdkFunction, in JSON format.
		- ***Status code***: 200 OK
		
 - Create Function
    - Endpoint: **POST /sdk/functions**
    - Body
    ```json
    {
		"name": "function_test",
		"version": "1.0",
		"description": "Test Function",
		"vnfdId": "057289e2-7b8e-4280-8734-43b924f64b85",
		"sliceId": "slice_test",
		"ownerId": "NXW",
		"vendor": "NXW",
		"visibility": "PRIVATE",
		"accessLevel": 2,
		"parameters": [
		  "isVideo",
		  "size"
		],
		"flavourExpression": "IF(isVideo != 0, video_flv_srv, standard_flv_srv)",
		"instantiationLevelExpression": "IF(size <= 1, small_il, IF(size <= 10, medium_il, big_il))",
		"metadata": {
		},
		"connectionPoints": [
		  {
			"name": "cp-eth0",
			"cpType": "EXTERNAL",
			"isManagement": true,
			"requiredPort": [80,443]
		  }
		],
		"monitoringParameters": [
		  {
			"metricName": "CPU_UTILIZATION",
			"metricType": "SYSTEM",
			"parameterType": "FUNCTION",
			"name": "cpu_utilization"
		  },
		  {
			"transform": "MAX_OVER_TIME",
			"argumentList": [],
			"targetParameterName": "cpu_utilization",
			"parameterType": "TRANSFORMED",
			"name": "max_cpu_utilization"
		  }
		],
		"swImageData": {
		  "imgName": "image_test",
		  "imgVersion": "1.0",
		  "checksum": "123456789abcdef",
		  "containerFormat": "bare",
		  "diskFormat": "qcow2",
		  "minDisk": 20,
		  "minRam": 2048,
		  "minCpu": 1,
		  "size": 2
		},
		"minInstancesCount": 3,
		"maxInstancesCount": 5,
		"requiredPorts": []
	}
    ```
    - Response:   
        - ***functionId***:  Id of the created function as body response  
        - ***Status code***: 201 Created  
        	
 - Delete Function
    - Endpoint: **DELETE /sdk/functions/{functionId}**
    - Response:
		- ***Status code***: 204 No Content
		
 - Publish Function
    - Endpoint: **POST /sdk/functions/{functionId}/publish**
    - Response:
        - ***Status Code***: 202 Accepted
 
 - Un-Publish Function
    - Endpoint: **POST /sdk/functions/{functionId}/unpublish**
    - Response:
        - ***Status Code***: 202 Accepted
        
## SDK Services   
 - Get Services
    - Endpoint: **GET /sdk/services/?sliceId="slice_id"**
    - Response:  
		- As a response body will have a Array<SdkService>, in JSON format, with all the Sdk Services present in the local database.
		- ***Status code***: 200 OK
		
 - Get Service  
    - Endpoint: **GET /sdk/services/{serviceId}**
    - Response:
		- As a response body will have the specified SdkService, in JSON format.
		- ***Status code***: 200 OK
		
 - Create Service
    - Endpoint: **POST /sdk/services**
    - Body
    ```json
	{
		"name": "service_test",
		"version": "1.0",
		"sliceId": "slice_test",
		"ownerId": "NXW",
		"designer": "NXW",
		"visibility": "PUBLIC",
		"accessLevel": 0,
		"parameters": [
		  "param1",
		  "param2"
		],
		"metadata": {
		  "use.spam": "egg"
		},
		"license": {
		  "type": "PUBLIC",
		  "url": "http://example.org"
		},
		"component": [
		  {
			"componentId": 1,
			"componentType": "SDK_FUNCTION",
			"mappingExpressions": [
			  "FLOOR(param1/1000)",
			  "param2"
			],
			"componentIndex": 0
		  },
		  {
			"componentId": 8,
			"componentType": "SDK_FUNCTION",
			"mappingExpressions": [],
			"componentIndex": 1
		  },
		  {
			"componentId": 5,
			"componentType": "SDK_FUNCTION",
			"mappingExpressions": [],
			"componentIndex": 2
		  }
		],
		"connectionPoints": [
		  {
			"name": "service_ext_cp",
			"cpType": "EXTERNAL",
			"requiredPort": []
		  },
		  {
			"name": "fw_miniweb_cp",
			"cpType": "INTERNAL",
			"internalCpId": 12,
			"requiredPort": [],
			"componentIndex": 1
		  },
		  {
			"name": "miniweb_fw_cp",
			"cpType": "INTERNAL",
			"internalCpId": 2,
			"requiredPort": [80,443],
			"componentIndex": 0
		  },
		  {
			"name": "vPlate_fw_cp",
			"cpType": "INTERNAL",
			"internalCpId": 6,
			"requiredPort": [],
			"componentIndex": 2
		  },
		  {
			"name": "fw_ext_cp",
			"cpType": "INTERNAL",
			"internalCpId": 9,
			"requiredPort": [],
			"componentIndex": 1
		  },
		  {
			"name": "fw_vPlate_cp",
			"cpType": "INTERNAL",
			"internalCpId": 10,
			"requiredPort": [],
			"componentIndex": 1
		  }
		],
		"link": [
		  {
			"name": "vPlate_net",
			"connectionPointNames": [
			  "fw_vPlate_cp",
			  "vPlate_fw_cp"
			]
		  },
		  {
			"name": "ext_net",
			"connectionPointNames": [
			  "fw_ext_cp",
			  "service_ext_cp"
			]
		  },
		  {
			"name": "miniweb_net",
			"connectionPointNames": [
			  "fw_miniweb_cp",
			  "miniweb_fw_cp"
			]
		  }
		],
		"l3Connectivity": [
		  {
			"connectionPointName": "service_ext_cp",
			"l3Rules": [
			  {
				"protocol": "TCP",
				"srcIp": "0.0.0.0",
				"srcPort": 8000,
				"dstIp": "10.0.0.42",
				"dstPort": 8998
			  }
			]
		  }
		],
		"extMonitoringParameters": [
		  {
			"componentIndex": 2,
			"importedParameterId": "7",
			"parameterType": "IMPORTED",
			"name": "vPlate_cpu_utilization"
		  }
		],
		"intMonitoringParameters": [
		  {
			"componentIndex": 1,
			"importedParameterId": "14",
			"parameterType": "IMPORTED",
			"name": "fw_cpu_utilization"
		  },
		  {
			"transform": "AVG_OVER_TIME",
			"argumentList": [],
			"targetParameterName": "fw_cpu_utilization",
			"parameterType": "TRANSFORMED",
			"name": "fw_cpu_utilization_avg"
		  },
		  {
			"aggregatorFunc": "max",
			"parametersName": ["fw_cpu_utilization", "vPlate_cpu_utilization"],
			"parameterType": "AGGREGATED",
			"name": "max_cpu_utilization"
		  }
		],
		"actions": [
		  {
			"componentIndex": "1",
			"step": 1,
			"actionType": "SCALE_IN",
			"name": "scale_in"
		  }
		],
		"actionRules": [
		  {
			"actionsName": [
			  "scale_in"
			],
                        "name": "testRule3",
                        "duration": "1m",
                        "severity": "major",
			"conditions": [
			  {
				"parameterName": "fw_cpu_utilization",
				"value": 90,
				"comparator": "geq"
			  }
			],
			"operator": "and"
		  }
		]
	}  
    ```
    - Response:   
        - ***serviceId***:  Id of the created service as body response  
        - ***Status code***: 201 Created  
    
 - Delete Service
    - Endpoint: **DELETE /sdk/services/{serviceId}**
    - Response:
		- ***Status code***: 204 No Content
				
 - Create Service Descriptor
    - Endpoint: **POST /sdk/services/{serviceId}/create_descriptor** 
    - Body: 
    ```json
    {
        "parameterValues": [ 10000, 1]
    }
    ```
    - Response:  
        - ***serviceDescriptorId***: Id of the Service Descriptor created   
        - ***Status Code***: 201 Created 

## SDK Service Descriptors
 - Get Service Descriptors 
    - Endpoint: **GET /sdk/service_descriptor/?sliceId="slice_id"**
    - Response:
		- As a response body will have an Array<SdkServiceDescriptor>, in JSON format, with all the Sdk Service Descriptors present in the local database.
		- ***Status code***: 200 OK
		
 - Get Service Descriptor 
    - Endpoint: **GET /sdk/service_descriptor/{serviceDescriptorId}**
    - Response:
		- As a response body will have the specified SdkServiceDescriptor, in JSON format.
		- ***Status code***: 200 OK
		
 - Delete Service Descriptor
    - Endpoint: **DELETE /sdk/service_descriptor/{serviceDescriptorId}**
	- Response:
		- ***Status code***: 204 No Content
		
 - Publish Service Descriptor
    - Endpoint: **POST /sdk/service_descriptor/{serviceDescriptorId}/publish**
    - Response:
        - ***Status Code***: 202 Accepted
 
 - Un-Publish Service Descriptor
    - Endpoint: **POST /sdk/service_descriptor/{serviceDescriptorId}/unpublish**
    - Response:
        - ***Status Code***: 202 Accepted
        		
# Development
To contribute to the development of the 5GCity SDK, you may use the very same development workflow as for any other 5GCity Github project. That is, you have to fork the repository and create pull requests.

# Usage
Once the server is running, please check the API Swagger documentation visiting the link: http://0.0.0.0:8081/swagger-ui.html 

# Testing
In order to test the 5GCity SDK the user need to run the service 5gcity-sdk.service created during the deployment phase and access the API Swagger documentation.
An instance of the 5G Apps & Services Catalogue is needed in case the publication will be tested.
By publishing to 5G Apps & Services Catalogue, the new service (or function) will be present in the 5G Apps & Services Catalogue and if any MANO plugin is enabled in the 5G Apps & Services Catalogue, the new NSD (or VNFD) will be pushed into it.

# License
The 5GCity SDK is published under Apache 2.0 license. Please see the LICENSE file for more details

# Lead developers
The following lead developers are responsible for this repository and have admin rights. They can, for example, merge pull requests.
- Elian Kraja [Nextworks S.r.l.]
- Leonardo Agueci [Nextworks S.r.l.]

# Feedback-Channel
Please use the GitHub issues for feedback. 


[//]: #
[Full Deployment guide-line]: doc/README.md
[Nextworks S.r.l.]: http://www.nextworks.it/
[Swagger documentation]: doc/model/swagger-2.0.yaml