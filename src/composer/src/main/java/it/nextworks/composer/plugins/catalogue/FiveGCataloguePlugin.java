package it.nextworks.composer.plugins.catalogue;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.zip.ZipInputStream;

import it.nextworks.composer.controller.elements.SliceResource;
import it.nextworks.composer.plugins.catalogue.api.management.ProjectResource;
import it.nextworks.composer.plugins.catalogue.sol005.nsdmanagement.elements.*;
import it.nextworks.composer.plugins.catalogue.sol005.vnfpackagemanagement.elements.CreateVnfPkgInfoRequest;
import it.nextworks.composer.plugins.catalogue.sol005.vnfpackagemanagement.elements.PackageOperationalStateType;
import it.nextworks.composer.plugins.catalogue.sol005.vnfpackagemanagement.elements.VnfPkgInfo;
import it.nextworks.composer.plugins.catalogue.sol005.vnfpackagemanagement.elements.VnfPkgInfoModifications;
import it.nextworks.nfvmano.libs.common.exceptions.FailedOperationException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import it.nextworks.composer.plugins.catalogue.api.nsd.DefaultApi;
import it.nextworks.composer.plugins.catalogue.invoker.nsd.ApiClient;
import it.nextworks.nfvmano.libs.descriptors.templates.DescriptorTemplate;


public class FiveGCataloguePlugin extends CataloguePlugin {

	private static final Logger log = LoggerFactory.getLogger(FiveGCataloguePlugin.class);
	
	
	DefaultApi nsdApi;
	it.nextworks.composer.plugins.catalogue.api.vnf.DefaultApi vnfApi;
	it.nextworks.composer.plugins.catalogue.api.management.DefaultApi mgmtApi;
	
	public FiveGCataloguePlugin(CatalogueType type, Catalogue catalogue) {
		super(type, catalogue);
		nsdApi = new DefaultApi(new ApiClient(catalogue));
		vnfApi = new it.nextworks.composer.plugins.catalogue.api.vnf.DefaultApi(new it.nextworks.composer.plugins.catalogue.invoker.vnf.ApiClient(catalogue));
		mgmtApi = new it.nextworks.composer.plugins.catalogue.api.management.DefaultApi(new it.nextworks.composer.plugins.catalogue.invoker.management.ApiClient(catalogue));
	}

	@Bean 
	public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() { 
		ObjectMapper mapper = new ObjectMapper(); 
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false); 
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(mapper); 
		return converter; 
	}
	
	public String uploadNetworkService(String servicePackagePath, String project, String contentType, KeyValuePairs userDefinedData, String authorization) throws RestClientException, IOException {

		/* Create CreateNsInfoRequest */
		CreateNsdInfoRequest request = new CreateNsdInfoRequest();
		if(userDefinedData != null)
			request.setUserDefinedData(userDefinedData);
	
		/* Save descriptor to file */
		NsdInfo nsInfo;
		// Create NsdInfo and perform post on /ns_descriptors
		try{
			nsInfo = nsdApi.createNsdInfo(request, project, authorization);
			log.debug("Created nsInfo with id: "  + nsInfo.getId());
		} catch(RestClientException e) {
			log.error("Unable to perform NsdInfo creation on public catalogue: " + e.getMessage());
			throw new RestClientException("Unable to perform NsdInfo creation on public catalogue", e);
		}
		//TODO: implementare la parte di Description Parser
		//log.debug("Creating file from DescriptorTempalate");
		//File fileDescriptor = DescriptorsParser.descriptorTempateToFile(descriptor);
			
		//log.debug("Creating MultipartFile from file");
        File servicePackage = new File(servicePackagePath);
		MultipartFile multipartFile = this.createMultiPartFromFile(servicePackage, contentType);
		try {
			log.debug("Trying to push data to catalogue");
			nsdApi.uploadNSD(nsInfo.getId().toString(), project,  multipartFile, contentType, authorization);
			log.debug("Data has been pushed correctly");
//			nsdApi.uploadNSD(nsInfo.getId().toString(), fileDescriptor, contentType);
		} catch(RestClientException e) {
			log.error("Something went wrong pushing the descriptor content on the catalogue: " + e.getMessage());
			nsdApi.deleteNSDInfo(nsInfo.getId().toString(), project, authorization);
			throw new RestClientException("Something went wrong pushing the descriptor content on the catalogue: " + e.getMessage(), e);
		}
		
		return nsInfo.getId().toString();
	}

	public void deleteNetworkService(String nsInfoId, String project, String authorization){

        // Delete nsInfo
        try{
            NsdInfoModifications updateBody = new NsdInfoModifications();
            updateBody.setNsdOperationalState(NsdOperationalStateType.DISABLED);
            nsdApi.updateNSDInfo(nsInfoId, project, updateBody, authorization);
            nsdApi.deleteNSDInfo(nsInfoId, project, authorization);
            log.debug("Deleted nsInfo with id: "  + nsInfoId);
        } catch(RestClientException e) {
            log.error("Unable to perform nsInfo deletion on public catalogue: " + e.getMessage());
            throw new RestClientException("Unable to perform nsInfo deletion on public catalogue", e);
        }

    }

	public NsdInfo getNsDescriptorInfo(String nsInfoId, String project, String authorization) {

		NsdInfo nsdInfo;
		try {
			nsdInfo = nsdApi.getNSDInfo(nsInfoId, project, authorization);
		} catch(RestClientException e) {
			log.error("RestClientException when trying to get NsdInfo  " + nsInfoId + ". Error: " + e.getMessage());
			throw new RestClientException("RestClientException when trying to get NsdInfo  " + nsInfoId + ". Error: " + e.getMessage(), e);
		}
		
		return nsdInfo;
	}
	
	
	public DescriptorTemplate getNsdContent(String nsInfoId, String project,  String range, String authorization) {
		
		Object obj;
		
		try {
			obj = nsdApi.getNSD(nsInfoId, project, range, authorization);
		} catch(RestClientException e) {
			log.error("RestClientException when trying to get NsdInfo  " + nsInfoId + ". Error: " + e.getMessage());
			throw new RestClientException("RestClientException when trying to get NsdInfo  " + nsInfoId + ". Error: " + e.getMessage(), e);
		}

		if (obj instanceof MultipartFile) {
			try {
				File file = convertToFile((MultipartFile) obj);
				return DescriptorsParser.fileToDescriptorTemplate(file);
			} catch(Exception e) {
				log.error("The file returned from the catalogue is not an File object");
				throw new ClassCastException("The file returned from the catalogue is not an File object");
			}
			
		} else {
			log.error("The file returned from the catalogue is not an File object");
			throw new ClassCastException("The file returned from the catalogue is not an File object"); 
		}
		
	}
	
	
	public List<NsdInfo> getNsdInfoList(String project, String authorization){
		List<NsdInfo> nsdList = null;
		
		try {
			nsdList = nsdApi.getNSDsInfo(project, authorization);
		} catch(RestClientException e) {
			log.error("RestClientException when trying to get list of nsdInfos");
			throw new RestClientException("RestClientException when trying to get list of nsdInfos", e);
		}
		
		return nsdList;
	}
	
	
	public List<VnfPkgInfo> getVnfPackageInfoList(String project, String authorization){
		List<VnfPkgInfo> vnfPkgList = null;
		
		try {
			vnfPkgList = vnfApi.getVNFPkgsInfo(project, authorization);
		} catch(RestClientException e) {
			log.error("RestClientException when trying to get list of VnfPkgInfos : " + e.getMessage());
			throw new RestClientException("RestClientException when trying to get list of VnfPkgInfos", e);
		}
		return vnfPkgList;
	}
	
	
	public MultipartFile getVnfPkgContent(String vnfPkgId, String project, String range, String storagePath, String authorization) {
		
		Resource obj;
        File targetFile;

		try {
			obj = (Resource) vnfApi.getVNFPkg(vnfPkgId, project, range, authorization);
            InputStream inputStream = obj.getInputStream();
            targetFile = new File(String.format("%s%s.zip", storagePath, vnfPkgId));
            java.nio.file.Files.copy(
                inputStream,
                targetFile.toPath(),
                StandardCopyOption.REPLACE_EXISTING);

            ArchiveParser.unzip(targetFile, new File(storagePath, vnfPkgId));

            IOUtils.closeQuietly(inputStream);

            MultipartFile multipartFile = createMultiPartFromFile(targetFile, "multipart/form-data");

            return multipartFile;

		} catch(RestClientException e) {
			log.error("RestClientException when trying to get vnfPkg  " + vnfPkgId + ". Error: " + e.getMessage());
			throw new RestClientException("RestClientException when trying to get vnfPkg  " + vnfPkgId + ". Error: " + e.getMessage(), e);
		} catch (IOException e) {
            log.error("Error while getting VNF Pkg file: " + e.getMessage());
            throw new RestClientException("RestClientException when trying to get vnfPkg  " + vnfPkgId + ". Error: " + e.getMessage(), e);
        }
	}

	public String uploadNetworkFunction(String functionPackagePath, String project, String contentType, KeyValuePairs userDefinedData, String authorization) throws RestClientException, IOException {

	    /* Create CreateVnfPkgInfoRequest */
        CreateVnfPkgInfoRequest request = new CreateVnfPkgInfoRequest();
        if(userDefinedData != null)
            request.setUserDefinedData(userDefinedData);

        /* Save descriptor to file */
        VnfPkgInfo vnfPkgInfo;

        // Create VnfPkgInfo and perform post on /vnf_packages
        try{
            vnfPkgInfo = vnfApi.createVNFPkgInfo(request, project, authorization);
            log.debug("Created vnfPkgInfo with id: "  + vnfPkgInfo.getId());
        } catch(RestClientException e) {
            log.error("Unable to perform VnfPkgInfo creation on public catalogue: " + e.getMessage());
            throw new RestClientException("Unable to perform VnfPkgInfo creation on public catalogue", e);
        }

        File functionPackage = new File(functionPackagePath);
        MultipartFile multipartFile = this.createMultiPartFromFile(functionPackage, contentType);
        try {
            log.debug("Trying to push data to catalogue");
            vnfApi.uploadVNFPkg(vnfPkgInfo.getId().toString(), project, multipartFile, contentType, authorization);
            log.debug("Data has been pushed correctly");
        } catch(RestClientException e) {
            log.error("Something went wrong pushing the descriptor content on the catalogue: " + e.getMessage());
            vnfApi.deleteVNFPkgInfo(vnfPkgInfo.getId().toString(), project, authorization);
            throw new RestClientException("Something went wrong pushing the descriptor content on the catalogue: " + e.getMessage(), e);
        }

        return vnfPkgInfo.getId().toString();
	}

    public void deleteNetworkFunction(String vnfInfoId, String project, String authorization){

        // Delete VnfPkgInfo
        try{
            VnfPkgInfoModifications updateBody = new VnfPkgInfoModifications();
            updateBody.setOperationalState(PackageOperationalStateType.DISABLED);
            vnfApi.updateVNFPkgInfo(vnfInfoId, project, updateBody, authorization);
            vnfApi.deleteVNFPkgInfo(vnfInfoId, project, authorization);
            log.debug("Deleted vnfPkgInfo with id: " + vnfInfoId);
        } catch(RestClientException e) {
            log.error("Unable to perform VnfPkgInfo deletion on public catalogue: " + e.getMessage());
            throw new RestClientException("Unable to perform VnfPkgInfo deletion on public catalogue", e);
        }
    }

    public void createProject(String projectId, String authorization){
        try{
            ProjectResource project = new ProjectResource(projectId, projectId + " project");
            mgmtApi.createProject(project, authorization);
            log.debug("Created project on public catalogue with id: " + projectId);
        } catch(RestClientException e) {
            log.error("Unable to create project on public catalogue: " + e.getMessage());
            throw new RestClientException("Unable to create project on public catalogue", e);
        }
    }

    public void deleteProject(String projectId, String authorization){
        try{
            mgmtApi.deleteProject(projectId, authorization);
            log.debug("Deleted project on public catalogue with id: " + projectId);
        } catch(RestClientException e) {
            log.error("Unable to delete project on public catalogue: " + e.getMessage());
            throw new RestClientException("Unable to delete project on public catalogue", e);
        }
    }

    public void addUserToProject(String projectId, String username, String authorization){
        try{
            //update users DB on public catalogue
            mgmtApi.getUsers(authorization);
            //add user to project on public catalogue
            mgmtApi.addUserToProject(projectId, username, authorization);
            log.debug("Added user " + username + " to project " + projectId + " on public catalogue");
        } catch(RestClientException e) {
            log.error("Unable to add user on public catalogue: " + e.getMessage());
            throw new RestClientException("Unable to add user on public catalogue", e);
        }
    }

    public void delUserFromProject(String projectId, String username, String authorization){
        try{
            //add user to project on public catalogue
            mgmtApi.delUserFromProject(projectId, username, authorization);
            log.debug("Deleted user " + username + " from project " + projectId + " on public catalogue");
        } catch(RestClientException e) {
            log.error("Unable to delete user on public catalogue: " + e.getMessage());
            throw new RestClientException("Unable to delete user on public catalogue", e);
        }
    }

	private MultipartFile createMultiPartFromFile(File file, String contentType) throws IOException {
	    byte[] content = null;
		try {
		    content = Files.readAllBytes(file.toPath());
		} catch (final IOException e) {
		}
		MultipartFile multipartFile = new MockMultipartFile("file",
		                     file.getName(), contentType, content);

		return multipartFile;

	}
	
	private File convertToFile(MultipartFile multipart) throws Exception {
		File convFile = new File(multipart.getOriginalFilename());
		convFile.createNewFile();
		FileOutputStream fos = new FileOutputStream(convFile);
		fos.write(multipart.getBytes());
		fos.close();
		return convFile;
	}
}
