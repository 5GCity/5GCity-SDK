/*
* Copyright 2018 Nextworks s.r.l.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package it.nextworks.composer.executor.interfaces;

import java.util.List;
import java.util.UUID;

import it.nextworks.sdk.SDKService;
import it.nextworks.sdk.exceptions.ExistingEntityException;
import it.nextworks.sdk.exceptions.NotExistingEntityException;

public interface ServiceManagerProviderInterface {

	public SDKService getServiceByUuid(UUID id) throws NotExistingEntityException;
	
	public SDKService getServiceById(Long id) throws NotExistingEntityException;
	
	public List<SDKService> getServices() throws NotExistingEntityException;
	
	public List<SDKService> getServicesUsingFunction(UUID functionId) throws NotExistingEntityException;
		
	public String createService(SDKService service) throws ExistingEntityException;
	
	public String updateService(SDKService service) throws NotExistingEntityException;
	
	public void deleteService(UUID serviceId) throws NotExistingEntityException;
	

}
