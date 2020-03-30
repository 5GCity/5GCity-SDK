/*
 * Copyright 2020 Nextworks s.r.l.
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
package it.nextworks.composer.adaptor.interfaces;

import it.nextworks.nfvmano.libs.descriptors.vnfd.nodes.VNF.VNFNode;
import it.nextworks.sdk.SdkFunction;

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
    public String adaptSDKFunctionToVNFNode(SdkFunction function);


    /* *
     * This operation permits the creation of a new VNF Descriptor (VNFNode) for the
     * firewall, enabling the rules defined by the user via gui.
     *
     * @param nodeFW       Generic firewall VNFNode present on database
     * @param l3Properties Rules to be applied
     * @return id of the new VNFNode specific to the given rules
     */
    /*	public String adaptFirewallToVNFNode(VNFNode nodeFW, List<L3ConnectivityProperty> l3Properties);
     */
}
