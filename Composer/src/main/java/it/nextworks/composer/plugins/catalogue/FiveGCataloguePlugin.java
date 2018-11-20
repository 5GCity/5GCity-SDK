package it.nextworks.composer.plugins.catalogue;

import java.io.File;
import java.util.List;

import org.hibernate.cfg.NotYetImplementedException;

import it.nextworks.composer.plugins.catalogue.sol005.nsd.CreateNsdInfoRequest;
import it.nextworks.composer.plugins.catalogue.sol005.nsd.NsdInfo;
import it.nextworks.composer.plugins.catalogue.sol005.nsd.NsdInfoModifications;
import it.nextworks.composer.plugins.catalogue.sol005.vnfpackage.CreateVnfPkgInfoRequest;
import it.nextworks.composer.plugins.catalogue.sol005.vnfpackage.UploadVnfPackageFromUriRequest;
import it.nextworks.composer.plugins.catalogue.sol005.vnfpackage.VnfPkgInfo;
import it.nextworks.composer.plugins.catalogue.sol005.vnfpackage.VnfPkgInfoModifications;

public class FiveGCataloguePlugin extends CataloguePlugin{

	public FiveGCataloguePlugin(CatalogueType type, Catalogue catalogue) {
		super(type, catalogue);
	}

//	@Override
//	public String createNsdResource() {
//		throw new NotYetImplementedException();
//	}
//
//	@Override
//	public String createVnfPackageResource() {
//		throw new NotYetImplementedException();
//	}
//
//	@Override
//	public void uploadNsdContent() {
//		throw new NotYetImplementedException();
//	}
//
//	@Override
//	public void uploadVnfPackageContent() {
//		throw new NotYetImplementedException();
//	}
//
//	@Override
//	public void uploadVnfPackageContentFormUri() {
//		throw new NotYetImplementedException();
//	}
//	

	@Override
	public List<VnfPkgInfo> listVnfPackageInfo() {
		// TODO Auto-generated method stub
		throw new NotYetImplementedException();
	}

	@Override
	public VnfPkgInfo createVnfPackageInfo(CreateVnfPkgInfoRequest request) {
		// TODO Auto-generated method stub
		throw new NotYetImplementedException();
	}

	@Override
	public VnfPkgInfo getVnfPackageInfo() {
		// TODO Auto-generated method stub
		throw new NotYetImplementedException();
	}

	@Override
	public void deleteVnfPackageInfo(String vnfPkgId) {
		// TODO Auto-generated method stub
		throw new NotYetImplementedException();
		
	}

	@Override
	public void updateVnfPackageInfo(String vnfPkgId, VnfPkgInfoModifications vnfInfoModifications) {
		// TODO Auto-generated method stub
		throw new NotYetImplementedException();
		
	}

	@Override
	public List<?> getVnfdDescriptorInVnfPackage(String vnfPackageId) {
		// TODO Auto-generated method stub
		throw new NotYetImplementedException();
	}

	@Override
	public void updateVnfpackageContent(String vnfPackageId, File content) {
		// TODO Auto-generated method stub
		throw new NotYetImplementedException();
		
	}

	@Override
	public File getVnfPackageContent(String vnfPackageId) {
		// TODO Auto-generated method stub
		throw new NotYetImplementedException();
	}

	@Override
	public void updateVnfPackageFromUri(String vnfPackageId, UploadVnfPackageFromUriRequest content) {
		// TODO Auto-generated method stub
		throw new NotYetImplementedException();
	}

	@Override
	public List<NsdInfo> listNsdsInfo() {
		// TODO Auto-generated method stub
		throw new NotYetImplementedException();
	}

	@Override
	public NsdInfo createNsdInfo(CreateNsdInfoRequest request) {
		// TODO Auto-generated method stub
		throw new NotYetImplementedException();
	}

	@Override
	public NsdInfo getNsdInfo(String nsdInfoId) {
		// TODO Auto-generated method stub
		throw new NotYetImplementedException();
	}

	@Override
	public void deleteNsdInfo(String nsdInfoId) {
		// TODO Auto-generated method stub
		throw new NotYetImplementedException();
	}

	@Override
	public void updateNsdInfo(String nsdInfoId, NsdInfoModifications request) {
		// TODO Auto-generated method stub
		throw new NotYetImplementedException();
	}

	@Override
	public File getNsdContent(String nsInfoId) {
		// TODO Auto-generated method stub
		throw new NotYetImplementedException();
	}

	@Override
	public void updateNsdContent(String nsInfoId, File content) {
		// TODO Auto-generated method stub
		throw new NotYetImplementedException();
	}

}
