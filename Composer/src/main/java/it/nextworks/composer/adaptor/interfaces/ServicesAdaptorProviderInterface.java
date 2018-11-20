package it.nextworks.composer.adaptor.interfaces;


import it.nextworks.nfvmano.libs.descriptors.nsd.nodes.NS.NSNode;
import it.nextworks.sdk.SDKService;


public interface ServicesAdaptorProviderInterface {

	
	/**
	 * Convert a given network service node into a SDK Service element. The new
	 * SDKService will be available to the vertical user.
	 * 
	 * @param node Node element provided by the catalogue
	 * @return id of the created sdk service
	 */
	public String adaptNSNodeToSDKService(NSNode node);

	/**
	 * Convert a created SDK Service via gui into a network service node.
	 * 
	 * @param service SDK Service provided by the Composer GUI
	 * @return id of the new network service
	 */
	public String adaptSDKServiceToNSNode(SDKService service);

	/**
	 * This operation enables the user to push the NSNode to the public catalogue
	 * 
	 * @param node Node to be pushed into the public catalogue
	 */
	public void publishToCatalogue(NSNode node);
	
	
}
