package it.nextworks.composer.plugins.catalogue;

import java.io.File;
import java.util.List;

import it.nextworks.composer.plugins.catalogue.sol005.nsd.CreateNsdInfoRequest;
import it.nextworks.composer.plugins.catalogue.sol005.nsd.NsdInfo;
import it.nextworks.composer.plugins.catalogue.sol005.nsd.NsdInfoModifications;
import it.nextworks.composer.plugins.catalogue.sol005.vnfpackage.CreateVnfPkgInfoRequest;
import it.nextworks.composer.plugins.catalogue.sol005.vnfpackage.UploadVnfPackageFromUriRequest;
import it.nextworks.composer.plugins.catalogue.sol005.vnfpackage.VnfPkgInfo;
import it.nextworks.composer.plugins.catalogue.sol005.vnfpackage.VnfPkgInfoModifications;

public interface CatalogueProviderInterface {

	
	public List<VnfPkgInfo> listVnfPackageInfo();
	
	public VnfPkgInfo createVnfPackageInfo(CreateVnfPkgInfoRequest request);
	
	public VnfPkgInfo getVnfPackageInfo();
	
	public void deleteVnfPackageInfo(String vnfPkgId);
	
	public void updateVnfPackageInfo(String vnfPkgId, VnfPkgInfoModifications vnfInfoModifications);

	public List<?> getVnfdDescriptorInVnfPackage(String vnfPackageId);
	
	public void	updateVnfpackageContent(String vnfPackageId, File content);
	
	public File getVnfPackageContent(String vnfPackageId);
	
	public void updateVnfPackageFromUri(String vnfPackageId, UploadVnfPackageFromUriRequest content);
	
	
	
	public List<NsdInfo> listNsdsInfo();
	
	public NsdInfo createNsdInfo(CreateNsdInfoRequest request);
	
	public NsdInfo getNsdInfo(String nsdInfoId);
	
	public void deleteNsdInfo(String nsdInfoId);
	
	public void updateNsdInfo(String nsdInfoId, NsdInfoModifications request);
	
	public File getNsdContent(String nsInfoId);
	
	public void updateNsdContent(String nsInfoId, File content);
	
	
	
	
	
	
	/*
	public String createNsdResource();
	
	public String createVnfPackageResource();
	
	public void uploadNsdContent();
	
	public void uploadVnfPackageContent();
	
	public void uploadVnfPackageContentFormUri();
	
	*/
}
