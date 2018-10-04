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
import org.hibernate.cfg.NotYetImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.nextworks.composer.executor.interfaces.ServiceManagerProviderInterface;
import it.nextworks.composer.executor.repositories.ConnectionpointRepository;
import it.nextworks.composer.executor.repositories.LinkRepository;
import it.nextworks.composer.executor.repositories.MonitoringParameterRepository;
import it.nextworks.composer.executor.repositories.SDKFunctionInstanceRepository;
import it.nextworks.composer.executor.repositories.SDKFunctionRepository;
import it.nextworks.composer.executor.repositories.SDKServiceRepository;
import it.nextworks.composer.executor.repositories.ScalingAspectRepository;
import it.nextworks.sdk.ConnectionPoint;
import it.nextworks.sdk.Link;
import it.nextworks.sdk.MonitoringParameter;
import it.nextworks.sdk.SDKFunction;
import it.nextworks.sdk.SDKFunctionInstance;
import it.nextworks.sdk.SDKService;
import it.nextworks.sdk.ScalingAspect;
import it.nextworks.sdk.enums.ConnectionPointType;
import it.nextworks.sdk.enums.LinkType;
import it.nextworks.sdk.enums.StatusType;
import it.nextworks.sdk.exceptions.AlreadyPublishedServiceException;
import it.nextworks.sdk.exceptions.ExistingEntityException;
import it.nextworks.sdk.exceptions.MalformattedElementException;
import it.nextworks.sdk.exceptions.NotExistingEntityException;
import it.nextworks.sdk.exceptions.NotPublishedServiceException;

@Service
public class ServiceManager implements ServiceManagerProviderInterface {

	private static final Logger log = LoggerFactory.getLogger(ServiceManager.class);

	@Autowired
	private SDKServiceRepository serviceRepository;

	@Autowired
	private SDKFunctionInstanceRepository functionInstanceRepository;

	@Autowired
	private SDKFunctionRepository functionRepository;

	@Autowired
	private LinkRepository linkRepository;

	@Autowired
	private ConnectionpointRepository cpRepository;

	@Autowired
	private MonitoringParameterRepository monitoringParamRepository;

	@Autowired
	private ScalingAspectRepository scalingRepository;

	@Autowired
	private FunctionInstanceManager functionInstanceManager;

	public ServiceManager() {

	}

	@Override
	public List<SDKService> getServices() {
		log.info("Request for all service stored in database");
		List<SDKService> services = serviceRepository.findAll();
		log.info("Returned list of services");
		return services;
	}

	@Override
	public List<SDKService> getServicesUsingFunction(Long functionId) {
		log.info("Request for all services which are using function with uuid: " + functionId);
		List<SDKService> serviceList = new ArrayList<>();
		List<SDKService> services = serviceRepository.findAll();
		for (SDKService service : services) {
			for (SDKFunctionInstance function : service.getFunctions())
				if (function.getId() == functionId) {
					log.debug("Service " + service.getId() + " has been found");
					serviceList.add(service);
					break;
				}
		}
		return serviceList;
	}

	@Override
	public String createService(SDKService service)
			throws ExistingEntityException, NotExistingEntityException, MalformattedElementException {
		log.info("Storing into database a new service");
		// TODO Find a way to check if service already exists
		SDKService response = null;
		if (service.isValid()) {
			log.debug("Storing into database service with name: " + service.getName());
			// Saving the service
			response = serviceRepository.saveAndFlush(service);
		} else {
			serviceRepository.delete(response);
			log.error("Malformatted SDKService");
			throw new MalformattedElementException("Malformatted SDKService");
		}
		// Getting the functionInstances on the service
		List<SDKFunctionInstance> instances = service.getFunctions();
		// For each functionInstace
		for (SDKFunctionInstance instance : instances) {
			try {
				functionInstanceManager.createInstance(instance, service);
			} catch (ExistingEntityException e) {
				serviceRepository.delete(response);
				log.error("FunctionInstance uuid " + response.getId() + " already present in database");
				throw new ExistingEntityException(
						"FunctionInstance uuid " + response.getId() + " already present in database");
			} catch (NotExistingEntityException e2) {
				// remove from DB service instance
				serviceRepository.delete(response);
				throw new NotExistingEntityException(
						"Function uuid " + instance.getFunctionId() + " not found on database");
			} catch (MalformattedElementException e3) {
				serviceRepository.delete(response);
				throw new MalformattedElementException("Malformatted SDKFunctionInstance. Check FlavourType");
			}
		}

		// Getting Links
		if (service.getTopologyList() != null) {
			List<Link> links = service.getTopologyList();
			for (Link link : links) {
				if (link.isValid() && link.getType() == LinkType.EXTERNAL) {
					link.setService(service);
					linkRepository.saveAndFlush(link);
					// Getting ConnectionPoints
					List<Long> cp_ids = link.getConnectionPointIds();
					for (Long id : cp_ids) {
						Optional<ConnectionPoint> cp = cpRepository.findById(id);
						if (!cp.isPresent() || !cp.get().isValid()
								|| cp.get().getType() != ConnectionPointType.EXTERNAL) {
							serviceRepository.delete(response);
							log.error("Malformatted request on ConnectionPoints connected to the link");
							throw new MalformattedElementException(
									"Malformatted request on ConnectionPoints connected to the link");
						}
					}
				} else {
					serviceRepository.delete(response);
					log.error("Malformatted request");
					throw new MalformattedElementException("Malformatted request");
				}
			}
		}

		// Getting Monitoring Parameters
		if (service.getMonitoringParameters() != null) {
			List<MonitoringParameter> monitoringParameters = service.getMonitoringParameters();
			for (MonitoringParameter param : monitoringParameters) {
				if (param.isValid()) {
					param.setService(service);
					monitoringParamRepository.saveAndFlush(param);
				} else {
					serviceRepository.delete(response);
					log.error("Malformatted request");
					throw new MalformattedElementException("Malformatted request");
				}
			}
		}

		// Getting Scaling Aspects
		if (service.getScalingAspects() != null) {
			List<ScalingAspect> scalingAspects = service.getScalingAspects();
			for (ScalingAspect scalingAspect : scalingAspects) {
				if (scalingAspect.isValid()) {
					scalingAspect.setService(service);
					scalingRepository.saveAndFlush(scalingAspect);
					// Getting Monitoring Parameters related to ScalingAspect
					if (scalingAspect.getMonitoringParameters() != null) {
						List<MonitoringParameter> monitoringParams = scalingAspect.getMonitoringParameters();
						for (MonitoringParameter param : monitoringParams) {
							if (param.isValidForScalingPurpose()) {
								param.setScalingAspect(scalingAspect);
								monitoringParamRepository.saveAndFlush(param);
							} else {
								serviceRepository.delete(response);
								log.error("Malformatted request");
								throw new MalformattedElementException("Malformatted request");
							}
						}
					}
				} else {
					serviceRepository.delete(response);
					log.error("Malformatted request");
					throw new MalformattedElementException("Malformatted request");
				}
			}
		}
		return service.getId().toString();
	}

	@Override
	public String updateService(SDKService service) throws NotExistingEntityException, MalformattedElementException {
		log.info("Updating an existing service with id: " + service.getId());
		// Check if service exists
		Optional<SDKService> srv = serviceRepository.findById(service.getId());
		if (!service.isValid()) {
			log.error("Service id " + service.getId() + " is malformatted");
			throw new MalformattedElementException("Service id " + service.getId() + " is malformatted");
		}
		log.debug("Service is valid");
		if (!srv.isPresent()) {
			log.error("Service id " + service.getId() + " already present in database");
			throw new NotExistingEntityException("Service id " + service.getId() + " not present in database");
		}
		log.debug("Service found on db");
		// First phase: Check if Objects are OK to be pushed on DB
		// Check if Instances exists, otherwise has to be created (on second phase)
		if (!validateInstances(service)) {
			log.error("SDKFunctionInstances referred to the SDKService " + service.getId() + " cannot be validated ");
			throw new MalformattedElementException(
					"SDKFunctionInstances referred to the SDKService " + service.getId() + " cannot be validated");
		}
		log.debug("Instances validated");
		// Check if MonitoringParameters are valid
		if (!validateMonitoringParameters(service)) {
			log.error("MonitoringParameters referred to the SDKService " + service.getId() + " cannot be validated ");
			throw new MalformattedElementException(
					"MonitoringParameters referred to the SDKService " + service.getId() + " cannot be validated");
		}
		log.debug("Monitoring Parameters validated");
		// Check if ScalingAspects are valid
		if (!validateScalingAspects(service)) {
			log.error("ScalingAspects referred to the SDKService " + service.getId() + " cannot be validated ");
			throw new MalformattedElementException(
					"ScalingAspects referred to the SDKService " + service.getId() + " cannot be validated");
		}
		log.debug("ScalingAspects validated");
		// Check if Links are valid
		if (!validateLinks(service)) {
			log.error("Links referred to the SDKService " + service.getId() + " cannot be validated ");
			throw new MalformattedElementException(
					"Links referred to the SDKService " + service.getId() + " cannot be validated");
		}
		log.debug("Links validated");
		// Second phase: If all valid, Update/add objects to db
		log.debug("Updating into database service with id: " + service.getId());

		List<Link> oldList = srv.get().getTopologyList();
		// Update del service su DB
		serviceRepository.saveAndFlush(service);

		// Update instances (or add new ones)
		for (SDKFunctionInstance instance : service.getFunctions()) {
			Optional<SDKFunctionInstance> dbInstance = functionInstanceRepository.findById(instance.getId());
			if (!dbInstance.isPresent())
				functionInstanceManager.updateInstance(instance, service);
			else
				try {
					functionInstanceManager.createInstance(instance, service);
				} catch (ExistingEntityException e) {
					log.error("This has not to happen");
				}
		}
		// Removing cancelled SDKFunctionInstances
		Optional<SDKService> dbService = serviceRepository.findById(service.getId());
		for (SDKFunctionInstance instance : srv.get().getFunctions()) {
			if (!service.getFunctions().contains(instance)) {
				functionInstanceManager.deleteInstance(instance.getId());
			}
		}
		// Update MonitoringParameters
		updateMonitoringParameters(service.getId(), service.getMonitoringParameters());
		// Update ScalingAspects
		updateScalingAspect(service.getId(), service.getScalingAspects());
		// Update links
		updateLinks(oldList, service);

		serviceRepository.saveAndFlush(service);

		return service.getId().toString();
	}

	@Override
	public SDKService getServiceById(Long id) throws NotExistingEntityException {
		log.info("Request for service with ID: " + id);
		Optional<SDKService> service = serviceRepository.findById(id);
		if (service.isPresent()) {
			return service.get();
		} else {
			log.error("Service with UUID " + id + " not found");
			throw new NotExistingEntityException("Service with ID " + id + " not found");
		}
	}

	@Override
	public void deleteService(Long serviceId) throws NotExistingEntityException {
		log.info("Request for deletion of service with uuid: " + serviceId);
		Optional<SDKService> service = serviceRepository.findById(serviceId);
		if (service.isPresent()) {
			// Delete functionInstance from Functions
			for (SDKFunctionInstance instance : service.get().getFunctions()) {
				Optional<SDKFunctionInstance> dbInstance = functionInstanceRepository.findById(instance.getId());
				if (dbInstance.isPresent()) {
					Optional<SDKFunction> function = functionRepository.findById(dbInstance.get().getFunctionId());
					if (function.isPresent()) {
						// dbInstance.get().setSdkFunction(null);
						dbInstance.get().setFunctionId(null);
						functionInstanceRepository.saveAndFlush(dbInstance.get());
					} else {
						log.error("Function with ID " + instance.getFunctionId() + " not found");
						throw new NotExistingEntityException(
								"Function with ID " + instance.getFunctionId() + " not found");
					}
				} else {
					log.error("Instance with ID " + instance.getId() + " not found");
					throw new NotExistingEntityException("Instance with ID " + instance.getId() + " not found");
				}
			}
			// Delete CPS from link
			deleteCps(service.get());
			serviceRepository.delete(service.get());
		} else {
			log.error("Service with UUID " + serviceId + " not found");
			throw new NotExistingEntityException("Service with ID " + serviceId + " not found");
		}
	}

	@Override
	public void publishService(Long serviceId) throws NotExistingEntityException, AlreadyPublishedServiceException {
		log.info("Request for publication of service with uuid: " + serviceId);
		// Check if service exists
		Optional<SDKService> optService = serviceRepository.findById(serviceId);
		if (!optService.isPresent()) {
			log.error("The Service with UUID: " + serviceId + " is not present in database");
			throw new NotExistingEntityException("The Service with UUID: " + serviceId + " is not present in database");
		} else {
			// Check if is already published
			SDKService service = optService.get();
			if (service.getStatus() == StatusType.COMMITTED) {
				log.error("The service identified by UUID: " + serviceId + " has been already published");
				throw new AlreadyPublishedServiceException(
						"The service identified by UUID: " + serviceId + " has been already published");
			} else {
				// A thread will be created to handle this request in order to perform it
				// asynchronously.
				service.setStatus(StatusType.COMMITTED);
				serviceRepository.saveAndFlush(service);
				throw new NotYetImplementedException("Method not yet implemented");
			}
		}
	}

	@Override
	public void unPublishService(Long serviceId) throws NotExistingEntityException, NotPublishedServiceException {
		log.info("Request for delete the publication of service with id: " + serviceId);
		Optional<SDKService> optService = serviceRepository.findById(serviceId);
		if (!optService.isPresent()) {
			log.error("The Service with ID: " + serviceId + " is not present in database");
			throw new NotExistingEntityException("The Service with ID: " + serviceId + " is not present in database");
		} else {
			// Check if is already published
			SDKService service = optService.get();
			if (service.getStatus() == StatusType.SAVED) {
				log.error("The service identified by ID: " + serviceId + " has not been published yet");
				throw new NotPublishedServiceException(
						"The service identified by ID: " + serviceId + " has been already published");
			} else {
				// A thread will be created to handle this request in order to perform it
				// asynchronously.
				service.setStatus(StatusType.SAVED);
				serviceRepository.saveAndFlush(service);
				throw new NotYetImplementedException("Method not yet implemented");
			}
		}
	}

	@Override
	public void updateScalingAspect(Long serviceId, List<ScalingAspect> scalingAspects)
			throws NotExistingEntityException, MalformattedElementException {
		log.info("Request to update list of scalingAspects for a specific SDK Service " + serviceId);
		Optional<SDKService> service = serviceRepository.findById(serviceId);
		if (!service.isPresent()) {
			log.error("The Service with ID: " + serviceId + " is not present in database");
			throw new NotExistingEntityException("The Service with ID: " + serviceId + " is not present in database");
		}
		for (ScalingAspect scale : scalingAspects) {
			if (!scale.isValid()) {
				log.error("Malformed Scaling Aspect");
				throw new MalformattedElementException("Malformed Scaling Aspect");
			}
		}
		log.debug("Updating list of scaling aspects on service");
		service.get().setScalingAspects(scalingAspects);
		log.debug("Updating list of scaling aspects on database");
		serviceRepository.saveAndFlush(service.get());
	}

	@Override
	public void deleteScalingAspect(Long serviceId, List<ScalingAspect> scalingAspects)
			throws NotExistingEntityException, MalformattedElementException {
		log.info("Request to delete a list of scalingAspects for a specific SDK Service " + serviceId);
		Optional<SDKService> service = serviceRepository.findById(serviceId);
		if (!service.isPresent()) {
			log.error("The Service with ID: " + serviceId + " is not present in database");
			throw new NotExistingEntityException("The Service with ID: " + serviceId + " is not present in database");
		}
		for (ScalingAspect scale : scalingAspects) {
			if (!scale.isValid()) {
				log.error("Malformed Scaling Aspect");
				throw new MalformattedElementException("Malformed Scaling Aspect");
			}
		}
		log.debug("All scaling aspects are valid");
		for (ScalingAspect scale : scalingAspects) {
			service.get().deleteScalingAspect(scale);
		}
		log.debug("All scaling aspects have been deleted. Saving to database");
		serviceRepository.saveAndFlush(service.get());

	}

	@Override
	public List<ScalingAspect> getScalingAspect(Long serviceId) throws NotExistingEntityException {
		log.info("Request to get the list of scalingAspects for a specific SDK Service " + serviceId);
		Optional<SDKService> service = serviceRepository.findById(serviceId);
		List<ScalingAspect> list = new ArrayList<>();
		if (!service.isPresent()) {
			log.error("The Service with ID: " + serviceId + " is not present in database");
			throw new NotExistingEntityException("The Service with ID: " + serviceId + " is not present in database");
		} else {
			for (ScalingAspect scale : service.get().getScalingAspects())
				list.add(scale);
		}
		return list;
	}

	@Override
	public void updateMonitoringParameters(Long serviceId, List<MonitoringParameter> monitoringParameters)
			throws NotExistingEntityException, MalformattedElementException {
		log.info("Request to update list of scalingAspects for a specific SDK Service " + serviceId);
		Optional<SDKService> service = serviceRepository.findById(serviceId);
		if (!service.isPresent()) {
			log.error("The Service with ID: " + serviceId + " is not present in database");
			throw new NotExistingEntityException("The Service with ID: " + serviceId + " is not present in database");
		}
		for (MonitoringParameter param : monitoringParameters) {
			if (!param.isValid()) {
				log.error("Malformed MonitoringParameter");
				throw new MalformattedElementException("Malformed MonitoringParameter");
			}
		}
		log.debug("Updating list of monitoring parameters on service");
		service.get().setMonitoringParameters(monitoringParameters);
		log.debug("Updating list of monitoring parameters on database");
		serviceRepository.saveAndFlush(service.get());

	}

	@Override
	public void deleteMonitoringParameters(Long serviceId, List<MonitoringParameter> monitoringParameters)
			throws NotExistingEntityException, MalformattedElementException {
		log.info("Request to delete a list of monitoring parameters for a specific SDK Service " + serviceId);
		Optional<SDKService> service = serviceRepository.findById(serviceId);
		if (!service.isPresent()) {
			log.error("The Service with ID: " + serviceId + " is not present in database");
			throw new NotExistingEntityException("The Service with ID: " + serviceId + " is not present in database");
		}
		for (MonitoringParameter param : monitoringParameters) {
			if (!param.isValid()) {
				log.error("Malformed MonitoringParameter");
				throw new MalformattedElementException("Malformed MonitoringParameter");
			}
		}
		log.debug("All monitoring parameters are valid. Deleting them from SDK Service");
		for (MonitoringParameter param : monitoringParameters) {
			service.get().deleteMonitoringParameter(param);
		}
		log.debug("All monitoring parameters have been deleted. Saving to database");
		serviceRepository.saveAndFlush(service.get());

	}

	@Override
	public List<MonitoringParameter> getMonitoringParameters(Long serviceId) throws NotExistingEntityException {
		log.info("Request to get the list of monitoring parameters for a specific SDK Service " + serviceId);
		Optional<SDKService> service = serviceRepository.findById(serviceId);
		if (!service.isPresent()) {
			log.error("The Service with ID: " + serviceId + " is not present in database");
			throw new NotExistingEntityException("The Service with ID: " + serviceId + " is not present in database");
		}
		List<MonitoringParameter> list = new ArrayList<>();
		for (MonitoringParameter param : service.get().getMonitoringParameters())
			list.add(param);
		return list;
	}

	private boolean validateScalingAspects(SDKService service) {
		if (service.getScalingAspects() != null) {
			List<ScalingAspect> scalingAspects = service.getScalingAspects();
			for (ScalingAspect scalingAspect : scalingAspects) {
				if (scalingAspect.isValid()) {
					if (scalingAspect.getMonitoringParameters() != null) {
						List<MonitoringParameter> monitoringParams = scalingAspect.getMonitoringParameters();
						for (MonitoringParameter param : monitoringParams) {
							if (!param.isValidForScalingPurpose()) {
								return false;
							}
						}
					}
				} else {
					return false;
				}
			}
		}
		return true;
	}

	private boolean validateMonitoringParameters(SDKService service) {
		List<MonitoringParameter> monitoringParameters = service.getMonitoringParameters();
		if (monitoringParameters != null) {
			for (MonitoringParameter param : monitoringParameters) {
				if (!param.isValid()) {
					return false;
				}
			}
		}
		return true;
	}

	private boolean validateLinks(SDKService service) {
		if (service.getTopologyList() != null) {
			List<Link> links = service.getTopologyList();
			for (Link link : links) {
				if (link.isValid() && link.getType() == LinkType.EXTERNAL) {
					// Getting ConnectionPoints
					List<Long> cp_ids = link.getConnectionPointIds();
					for (Long id : cp_ids) {
						Optional<ConnectionPoint> cp = cpRepository.findById(id);
						if (!(cp.isPresent() && cp.get().isValid()
								&& cp.get().getType() == ConnectionPointType.EXTERNAL)) {
							return false;
						}
					}
				} else {
					return false;
				}
			}
		}
		return true;
	}

	private boolean validateInstances(SDKService service) {
		List<SDKFunctionInstance> instances = service.getFunctions();
		for (SDKFunctionInstance instance : instances) {
			// Check if already in DB and related to the instance
			Optional<SDKFunctionInstance> dbInstance = functionInstanceRepository.findById(instance.getId());
			if (!dbInstance.isPresent()) {
				continue;
			} else if (!functionInstanceManager.validateInstance(instance)) {
				return false;
			}
		}
		return true;
	}

	private void updateLinks(List<Link> oldList, SDKService service) throws NotExistingEntityException {
		// Remove all links
		deleteCps(service);
		// ADD new links
		for (Link link : service.getTopologyList()) {
			link.setService(service);
			linkRepository.saveAndFlush(link);
		}

	}

	private void deleteCps(SDKService service) {
		for (Link link : service.getTopologyList()) {
			List<Long> cps = link.getConnectionPointIds();
			for (Long cpId : cps) {
				Optional<ConnectionPoint> cp = cpRepository.findById(cpId);
				if (cp.isPresent()) {
					cpRepository.delete(cp.get());
				}
			}
//			List<ConnectionPoint> cps = link.getConnectionPoints();
//			for (Long cpId : link.getConnectionPointIds()) {
//				for (ConnectionPoint cp : cps) {
//					log.debug("++++++++++++++++ Checkign CP: " + cpId + " in links connection points");
//					if (cp.getId() == cpId) {
//						cps.remove(cp);
//						log.debug("Found CP with cpId: " + cpId);
//						cpRepository.delete(cp);
//					}
//				}
//			}
//			link.setConnectionPoints(cps);
			linkRepository.saveAndFlush(link);
		}
	}
}
