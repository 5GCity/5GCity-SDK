package it.nextworks.composer.adaptor.interfaces;

import java.util.List;

import it.nextworks.nfvmano.libs.descriptors.vnfd.nodes.VNF.VNFNode;
import it.nextworks.sdk.L3ConnectivityProperty;
import it.nextworks.sdk.SDKFunction;

public interface FunctionsAdaptorProviderInterface {

	
	/**
	 * Convert a given VNFNode to a SDK Function. The new SDK Function will be
	 * available for the vertical user
	 * 
	 * @param node Node element provided by the catalogue or created by the admin
	 *             user through the Editor module
	 * @return id of the new SDKFunction element
	 */
	public String adaptVNFNodeToSDKFunction(VNFNode node);

	/**
	 * Convert the SDKFunction to a VNFNode.
	 * 
	 * @param function SDK Function to be converted to a VNFNode
	 * @return id of the new VNFNode element
	 */
	public String adaptSDKFunctionToVNFNode(SDKFunction function);


	/**
	 * This operation permits the creation of a new VNF Descriptor (VNFNode) for the
	 * firewall, enabling the rules defined by the user via gui.
	 * 
	 * @param nodeFW       Generic firewall VNFNode present on database
	 * @param l3Properties Rules to be applied
	 * @return id of the new VNFNode specific to the given rules
	 */
	public String adaptFirewallToVNFNode(VNFNode nodeFW, List<L3ConnectivityProperty> l3Properties);

}
