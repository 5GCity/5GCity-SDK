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
package it.nextworks.composer.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.nextworks.composer.executor.interfaces.FunctionManagerProviderInterface;
import it.nextworks.composer.executor.repositories.SDKFunctionInstanceRepository;
import it.nextworks.composer.executor.repositories.SDKFunctionRepository;
import it.nextworks.sdk.SDKFunction;
import it.nextworks.sdk.exceptions.NotExistingEntityException;

@Service
public class FunctionManager implements FunctionManagerProviderInterface{

	private static final Logger log = LoggerFactory.getLogger(FunctionManager.class);
			
	@Autowired
	private SDKFunctionRepository SDKFunctionRepository;
	

	@Autowired
	private SDKFunctionInstanceRepository functionInstanceRepository;
	
	public FunctionManager() {}
	
	@Override
	public SDKFunction getFunction(UUID id) throws NotExistingEntityException {
		Optional<SDKFunction> result = SDKFunctionRepository.findByUuid(id);
		if(result.isPresent()) {
			return result.get();
		} else {
			log.error("No SDKFunction with UUID: " + id + " was found.");
			throw new NotExistingEntityException("No SDKFunction with UUID: " + id + " was found.");
		}
	}

	
	
	
	
	@Override
	public List<SDKFunction> getFunctions() {
		List<SDKFunction> functionList = SDKFunctionRepository.findAll();
		if(functionList.size() == 0) {
			log.debug("No Functions are available");
		} else 
			log.debug("SDK Functions present in DB: " + functionList.size());
		return functionList;
	}

}
