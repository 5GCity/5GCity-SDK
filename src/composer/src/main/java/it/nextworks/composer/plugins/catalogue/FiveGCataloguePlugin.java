package it.nextworks.composer.plugins.catalogue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import it.nextworks.composer.plugins.catalogue.api.nsd.DefaultApi;
import it.nextworks.composer.plugins.catalogue.elements.nsd.CreateNsdInfoRequest;
import it.nextworks.composer.plugins.catalogue.elements.nsd.KeyValuePairs;
import it.nextworks.composer.plugins.catalogue.elements.nsd.NsdInfo;
import it.nextworks.composer.plugins.catalogue.elements.vnf.CreateVnfPkgInfoRequest;
import it.nextworks.composer.plugins.catalogue.elements.vnf.VnfPkgInfo;
import it.nextworks.composer.plugins.catalogue.invoker.nsd.ApiClient;
import it.nextworks.nfvmano.libs.descriptors.templates.DescriptorTemplate;


public class FiveGCataloguePlugin extends CataloguePlugin {

	private static final Logger log = LoggerFactory.getLogger(FiveGCataloguePlugin.class);
	
	
	DefaultApi nsdApi;
	it.nextworks.composer.plugins.catalogue.api.vnf.DefaultApi vnfApi;
	
	public FiveGCataloguePlugin(CatalogueType type, Catalogue catalogue) {
		super(type, catalogue);
		nsdApi = new DefaultApi(new ApiClient(catalogue));
		vnfApi = new it.nextworks.composer.plugins.catalogue.api.vnf.DefaultApi(new it.nextworks.composer.plugins.catalogue.invoker.vnf.ApiClient(catalogue));
	}

	@Bean 
	public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() { 
		ObjectMapper mapper = new ObjectMapper(); 
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false); 
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(mapper); 
		return converter; 
	}
	
	public String uploadNetworkService(DescriptorTemplate descriptor, String contentType, KeyValuePairs userDefinedData) throws RestClientException, IOException {

		
		/* Create CreateNsInfoRequest */
		CreateNsdInfoRequest request = new CreateNsdInfoRequest();
		if(userDefinedData != null)
			request.setUserDefinedData(userDefinedData);
	
		/* Save descriptor to file */
		
		
		NsdInfo nsInfo;
		// Create NsdInfo and perform post on /ns_descriptors
		try{
			nsInfo = nsdApi.createNsdInfo(request);
			log.debug("Created nsInfo with id: "  + nsInfo.getId());
		} catch(RestClientException e1) {
			log.error("Unable to perform NsdInfo creation on public catalogue: " + e1.getMessage());
			throw new RestClientException("Unable to perform NsdInfo creation on public catalogue"); 
		}
		//TODO: implementare la parte di Description Parser
		log.debug("Creating file from DescriptorTempalate");
		File fileDescriptor = DescriptorsParser.descriptorTempateToFile(descriptor);
			
		log.debug("Creating MultipartFile from file");
		MultipartFile multipartFile = this.createMultiPartFromFile(fileDescriptor, contentType);
		
		try {
			log.debug("Trying to push data to catalogue");
			nsdApi.uploadNSD(nsInfo.getId().toString(), multipartFile, contentType);
			log.debug("Data has been pushed correctly");
//			nsdApi.uploadNSD(nsInfo.getId().toString(), fileDescriptor, contentType);
		} catch(RestClientException e2) {
			log.error("Something went wrong pushing the descriptor content on the catalogue: " + e2.getMessage());
			nsdApi.deleteNSDInfo(nsInfo.getNsdId().toString());
			throw new RestClientException("Something went wrong pushing the descriptor content on the catalogue: " + e2.getMessage());
		}
		
		return nsInfo.getId().toString();
	}
	
	public String uploadNetworkService(File descriptor, String contentType, KeyValuePairs userDefinedData) throws RestClientException, IOException {
		
		/* Create CreateNsInfoRequest */
		CreateNsdInfoRequest request = new CreateNsdInfoRequest();
		if(userDefinedData != null)
			request.setUserDefinedData(userDefinedData);
	
		/* Save descriptor to file */
		
		
		NsdInfo nsInfo;
		// Create NsdInfo and perform post on /ns_descriptors
		try{
			nsInfo = nsdApi.createNsdInfo(request);
			log.debug("Created nsInfo with id: "  + nsInfo.getId());
		} catch(RestClientException e1) {
			log.error("Unable to perform NsdInfo creation on public catalogue: " + e1.getMessage());
			throw new RestClientException("Unable to perform NsdInfo creation on public catalogue"); 
		}
//		//TODO: implementare la parte di Description Parser
//		log.debug("Creating file from DescriptorTempalate");
//		File fileDescriptor = DescriptorsParser.descriptorTempateToFile(descriptor);
//			
		log.debug("Creating MultipartFile from file");
		MultipartFile multipartFile = this.createMultiPartFromFile(descriptor, contentType);
		
		try {
			log.debug("Trying to push data to catalogue");
			nsdApi.uploadNSD(nsInfo.getId().toString(), multipartFile, contentType);
			log.debug("Data has been pushed correctly");
//			nsdApi.uploadNSD(nsInfo.getId().toString(), fileDescriptor, contentType);
		} catch(RestClientException e2) {
			log.error("Something went wrong pushing the descriptor content on the catalogue: " + e2.getMessage());
			nsdApi.deleteNSDInfo(nsInfo.getNsdId().toString());
			throw new RestClientException("Something went wrong pushing the descriptor content on the catalogue: " + e2.getMessage());
		}
		
		return nsInfo.getId().toString();
	}
	
	
	
	public NsdInfo getNsDescriptorInfo(String nsInfoId) {

		NsdInfo nsdInfo;
		try {
			nsdInfo = nsdApi.getNSDInfo(nsInfoId);
		} catch(RestClientException e1) {
			log.error("RestClientException when trying to get NsdInfo  " + nsInfoId + ". Error: " + e1.getMessage());
			throw new RestClientException("RestClientException when trying to get NsdInfo  " + nsInfoId + ". Error: " + e1.getMessage()); 
		}
		
		return nsdInfo;
		
	}
	
	
	public DescriptorTemplate getNsdContent(String nsInfoId, String range) {
		
		Object obj;
		
		try {
			obj = nsdApi.getNSD(nsInfoId, range);
		} catch(RestClientException e1) {
			log.error("RestClientException when trying to get NsdInfo  " + nsInfoId + ". Error: " + e1.getMessage());
			throw new RestClientException("RestClientException when trying to get NsdInfo  " + nsInfoId + ". Error: " + e1.getMessage()); 
		}

		if (obj instanceof MultipartFile) {
			try {
				File file = convertToFile((MultipartFile) obj);
				return DescriptorsParser.fileToDescriptorTemplate(file);
			} catch(Exception e2) {
				log.error("The file returned from the catalogue is not an File object");
				throw new ClassCastException("The file returned from the catalogue is not an File object"); 
			}
			
		} else {
			log.error("The file returned from the catalogue is not an File object");
			throw new ClassCastException("The file returned from the catalogue is not an File object"); 
		}
		
	}
	
	
	public List<NsdInfo> getNsdInfoList(){
		List<NsdInfo> nsdList = null;
		
		try {
			nsdList = nsdApi.getNSDsInfo();
		} catch(RestClientException e1) {
			log.error("RestClientException when trying to get list of nsdInfos");
			throw new RestClientException("RestClientException when trying to get list of nsdInfos"); 
		}
		
		return nsdList;
	}
	
	
/*	public List<VnfPkgInfo> getVnfPackageInfoList(){
		List<VnfPkgInfo> vnfPkgList = null;
		
		try {
			vnfPkgList = vnfApi.getVNFPkgsInfo();
		} catch(RestClientException e1) {
			log.error("RestClientException when trying to get list of VnfPkgInfos");
			throw new RestClientException("RestClientException when trying to get list of VnfPkgInfos"); 
		}
		return vnfPkgList;
	}
	
	
	public File getVnfPkgContent(String vnfPkgId, String range) {
		
		Object obj;
		
		try {
			obj = vnfApi.getVNFPkg(vnfPkgId, range);
		} catch(RestClientException e1) {
			log.error("RestClientException when trying to get vnfPkg  " + vnfPkgId + ". Error: " + e1.getMessage());
			throw new RestClientException("RestClientException when trying to get vnfPkg  " + vnfPkgId + ". Error: " + e1.getMessage()); 
		}

		if (obj instanceof MultipartFile) {
			try {
				File file = convertToFile((MultipartFile) obj);
				return file;
			} catch(Exception e2) {
				log.error("The file returned from the catalogue is not an File object");
				throw new ClassCastException("The file returned from the catalogue is not an File object"); 
			}
			
		} else {
			log.error("The file returned from the catalogue is not an File object");
			throw new ClassCastException("The file returned from the catalogue is not an File object"); 
		}
		
	}
	
	
	public Object getVnfPackage(String vnfPkgInfoId) {
		//TODO: What exactly is returning this function?
		Object vnfPkg;
		try {
			vnfPkg = vnfApi.getVNFD(vnfPkgInfoId);		
		} catch(RestClientException e1) {
			log.error("RestClientException when trying to get vnfPackage identified by " + vnfPkgInfoId + ". Error: " + e1.getMessage());
			throw new RestClientException("RestClientException when trying to get vnfPackage identified by " + vnfPkgInfoId + ". Error: " + e1.getMessage()); 
		}
		return vnfPkg;
		
	}
	*/
	
	public VnfPkgInfo createvnfPackageInfo(CreateVnfPkgInfoRequest vnfPackageInfoRequest) {
		VnfPkgInfo vnfPkgInfo = null;
		try {
			vnfPkgInfo = vnfApi.createVNFPkgInfo(vnfPackageInfoRequest);
		} catch(RestClientException e1) {
			
		}
		return vnfPkgInfo;
		
	}
	
	
	
	
	private MultipartFile createMultiPartFromFile(File file, String contentType) throws IOException {
//		DiskFileItem fileItem = new DiskFileItem("file", "text/plain", false, file.getName(), (int) file.length() , file.getParentFile());
//	    fileItem.getOutputStream();
//	    MultipartFile multipartFile = new CommonsMultipartFile(fileItem);
		
		byte[] content = null;
		try {
		    content = Files.readAllBytes(file.toPath());
		} catch (final IOException e) {
		}
		MultipartFile multipartFile = new MockMultipartFile("file",
		                     file.getName(), contentType, content);
		

		
		
//		FileInputStream input = new FileInputStream(file);
//	    MultipartFile multipartFile = new MockMultipartFile("file",
//	            file.getName(), contentType, IOUtils.toByteArray(input));
//
//		FileItem fileItem = new DiskFileItem("mainFile", Files.probeContentType(file.toPath()), false, file.getName(), (int) file.length(), file.getParentFile());
//
//		try {
//		    InputStream input = new FileInputStream(file);
//		    OutputStream os = fileItem.getOutputStream();
//		    IOUtils.copy(input, os);
//		    // Or faster..
//		    // IOUtils.copy(new FileInputStream(file), fileItem.getOutputStream());
//		} catch (IOException ex) {
//		    // do something.
//		}
//
//		MultipartFile multipartFile = new CommonsMultipartFile(fileItem);
		
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
