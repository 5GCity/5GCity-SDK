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
package it.nextworks.sdk;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import it.nextworks.sdk.enums.SdkFunctionStatus;
import it.nextworks.sdk.enums.Visibility;
import it.nextworks.sdk.enums.SdkServiceComponentType;
import it.nextworks.sdk.evalex.ExtendedExpression;
import it.nextworks.sdk.exceptions.MalformedElementException;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * SDKFunction
 * <p>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "sliceId",
    "status",
    "vnfInfoId",
    "ownerId",
    "name",
    "description",
    "vendor",
    "version",
    "parameters",
    "vnfdId",
    "vnfdProvider",
    "vnfdVersion",
    "visibility",
    "flavourExpression",
    "instantiationLevelExpression",
    "metadata",
    "connectionPoints",
    "monitoringParameters",
    "groupId",
    "accessLevel",
    "swImageData",
    "epoch",
    "minInstancesCount",
    "maxInstancesCount"
})
@Entity
public class SdkFunction implements InstantiableCandidate {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String sliceId;

    private SdkFunctionStatus status;

    private String vnfInfoId;

    @OneToMany(mappedBy = "sdkFunction", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @LazyCollection(LazyCollectionOption.FALSE)
    private Set<ConnectionPoint> connectionPoint = new HashSet<>();

    private String description;

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @OrderColumn
    private List<String> parameters = new ArrayList<>();

    private String vnfdId;

    //private String vnfdVersion;

    private String flavourExpression;

    private String instantiationLevelExpression;

    @OneToMany(mappedBy = "function", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @LazyCollection(LazyCollectionOption.FALSE)
    private Set<Metadata> metadata = new HashSet<>();

    @OneToMany(mappedBy = "sdkFunction", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @LazyCollection(LazyCollectionOption.FALSE)
    private Set<MonitoringParameter> monitoringParameters = new HashSet<>();

    @OneToMany(mappedBy = "function", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @LazyCollection(LazyCollectionOption.FALSE)
    private Set<RequiredPort> requiredPorts = new HashSet<>();

    private String name;

    private String vendor;

    private String version = "1.0";

    private String ownerId;

    //private String vnfdProvider;

    private Visibility visibility = Visibility.fromValue("PRIVATE");

    //private String groupId;

    private Integer accessLevel = 4;//TODO default to?

    private SwImageData swImageData;

    private Long epoch;

    private Integer minInstancesCount = 1;

    private Integer maxInstancesCount = 1;

    @JsonProperty("status")
    public SdkFunctionStatus getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(SdkFunctionStatus status) {
        this.status = status;
    }

    @JsonIgnore
    public String getVnfInfoId() {
        return vnfInfoId;
    }

    @JsonIgnore
    public void setVnfInfoId(String vnfInfoId) {
        this.vnfInfoId = vnfInfoId;
    }

    @JsonProperty("ownerId")
    public String getOwnerId() {
        return ownerId;
    }

    @JsonProperty("ownerId")
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    @JsonProperty("sliceId")
    public String getSliceId() {
        return sliceId;
    }

    @JsonProperty("sliceId")
    public void setSliceId(String sliceId) {
        this.sliceId = sliceId;
    }

/*
    @JsonProperty("vnfdProvider")
    public String getVnfdProvider() {
        return vnfdProvider;
    }

    @JsonProperty("vnfdProvider")
    public void setVnfdProvider(String vnfdProvider) {
        this.vnfdProvider = vnfdProvider;
    }
    */

    @JsonProperty("visibility")
    public Visibility getVisibility() {
        return visibility;
    }

    @JsonProperty("visibility")
    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    @JsonProperty("connectionPoints")
    public Set<ConnectionPoint> getConnectionPoint() {
        return connectionPoint;
    }

    @JsonProperty("connectionPoints")
    public void setConnectionPoint(Set<ConnectionPoint> connectionPoint) {

        this.connectionPoint.clear();
        this.connectionPoint.addAll(connectionPoint);

        for (ConnectionPoint cp : this.connectionPoint) {
            cp.setSdkFunction(this);
        }
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("parameters")
    @Override
    public List<String> getParameters() {
        return parameters;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("parameters")
    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

    @JsonProperty("vnfdId")
    public String getVnfdId() {
        return vnfdId;
    }

    @JsonProperty("vnfdId")
    public void setVnfdId(String vnfdId) {
        this.vnfdId = vnfdId;
    }

    /*
    @JsonProperty("vnfdVersion")
    public String getVnfdVersion() {
        return vnfdVersion;
    }

    @JsonProperty("vnfdVersion")
    public void setVnfdVersion(String vnfdVersion) {
        this.vnfdVersion = vnfdVersion;
    }
    */

    @JsonProperty("flavourExpression")
    public String getFlavourExpression() {
        return flavourExpression;
    }

    @JsonProperty("flavourExpression")
    public void setFlavourExpression(String flavourExpression) {
        this.flavourExpression = flavourExpression;
    }

    @JsonProperty("instantiationLevelExpression")
    public String getInstantiationLevelExpression() {
        return instantiationLevelExpression;
    }

    @JsonProperty("instantiationLevelExpression")
    public void setInstantiationLevelExpression(String instantiationLevelExpression) {
        this.instantiationLevelExpression = instantiationLevelExpression;
    }

    @JsonProperty("id")
    @Override
    public Long getId() {
        return id;
    }

    void setId(Long id) {
        this.id = id;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("metadata")
    public Map<String, String> getMetadata() {
        return metadata.stream().collect(Collectors.toMap(Metadata::getKey, Metadata::getValue));
    }

    @JsonIgnore
    public Set<Metadata> getMetadata2() {
        return metadata;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("metadata")
    public void setMetadata(Map<String, String> metadata) {
        this.metadata.clear();
        this.metadata.addAll(metadata.entrySet().stream()
            .map(e -> new Metadata(e.getKey(), e.getValue(), this))
            .collect(Collectors.toSet()));

        for (Metadata metadatum : this.metadata) {
            metadatum.setFunction(this);
        }
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("monitoringParameters")
    public Set<MonitoringParameter> getMonitoringParameters() {
        return monitoringParameters;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("monitoringParameters")
    public void setMonitoringParameters(Set<MonitoringParameter> monitoringParameters) {
        this.monitoringParameters.clear();
        this.monitoringParameters.addAll(monitoringParameters);

        for (MonitoringParameter mp : this.monitoringParameters) {
            mp.setSdkFunction(this);
        }

    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("vendor")
    public String getVendor() {
        return vendor;
    }

    @JsonProperty("vendor")
    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    @JsonProperty("version")
    public String getVersion() {
        return version;
    }

    @JsonProperty("version")
    public void setVersion(String version) {
        this.version = version;
    }

    @JsonProperty("requiredPorts")
    public Set<RequiredPort> getRequiredPorts() {
        return requiredPorts;
    }

    @JsonProperty("requiredPorts")
    public void setRequiredPorts(Set<RequiredPort> requiredPorts) {
        this.requiredPorts.clear();
        this.requiredPorts.addAll(requiredPorts);

        for (RequiredPort mp : this.requiredPorts) {
            mp.setFunction(this);
        }
    }

    /*
    @JsonProperty("groupId")
    public String getGroupId() {
        return groupId;
    }

    @JsonProperty("groupId")
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
     */

    @JsonProperty("accessLevel")
    public Integer getAccessLevel() {
        return accessLevel;
    }

    @JsonProperty("accessLevel")
    public void setAccessLevel(Integer accessLevel) {
        this.accessLevel = accessLevel;
    }

    @JsonProperty("swImageData")
    public SwImageData getSwImageData() {
        return swImageData;
    }

    @JsonProperty("swImageData")
    public void setSwImageData(SwImageData swImageData) { this.swImageData = swImageData; }

    @JsonIgnore
    public Long getEpoch() {
        return epoch;
    }

    @JsonIgnore
    public void setEpoch(Long epoch) {
        this.epoch = epoch;
    }

    @JsonProperty("minInstancesCount")
    public Integer getMinInstancesCount() {
        return minInstancesCount;
    }

    @JsonProperty("minInstancesCount")
    public void setMinInstancesCount(Integer minInstancesCount) {
        this.minInstancesCount = minInstancesCount;
    }

    @JsonProperty("maxInstancesCount")
    public Integer getMaxInstancesCount() {
        return maxInstancesCount;
    }

    @JsonProperty("maxInstancesCount")
    public void setMaxInstancesCount(Integer maxInstancesCount) {
        this.maxInstancesCount = maxInstancesCount;
    }

    @Override
    @JsonIgnore
    public SdkServiceComponentType getType() {
        return SdkServiceComponentType.SDK_FUNCTION;
    }

    @JsonIgnore
    public ExtendedExpression<String> getFlavourCompiledExpression() {
        return ExtendedExpression.stringValued(flavourExpression, parameters);
    }

    @JsonIgnore
    public ExtendedExpression<String> getILCompiledExpression() {
        return ExtendedExpression.stringValued(instantiationLevelExpression, parameters);
    }

    @Override
    public SdkComponentInstance makeDescriptor(
        List<BigDecimal> parameterValues
    ) {
        return new SdkFunctionDescriptor(this, parameterValues);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(SdkFunction.class.getName())
            .append('[');
        sb.append("connectionPoint");
        sb.append('=');
        sb.append(((this.connectionPoint == null) ? "<null>" : this.connectionPoint));
        sb.append(',');
        sb.append("description");
        sb.append('=');
        sb.append(((this.description == null) ? "<null>" : this.description));
        sb.append(',');
        sb.append("parameters");
        sb.append('=');
        sb.append(((this.parameters == null) ? "<null>" : this.parameters));
        sb.append(',');
        sb.append("vnfdId");
        sb.append('=');
        sb.append(((this.vnfdId == null) ? "<null>" : this.vnfdId));
        sb.append(',');
        sb.append("flavourExpression");
        sb.append('=');
        sb.append(((this.flavourExpression == null) ? "<null>" : this.flavourExpression));
        sb.append(',');
        sb.append("instantiationLevelExpression");
        sb.append('=');
        sb.append(((this.instantiationLevelExpression == null) ? "<null>" : this.instantiationLevelExpression));
        sb.append(',');
        sb.append("id");
        sb.append('=');
        sb.append(((this.id == null) ? "<null>" : this.id));
        sb.append(',');
        sb.append("sliceId");
        sb.append('=');
        sb.append(((this.sliceId == null) ? "<null>" : this.sliceId));
        sb.append(',');
        sb.append("status");
        sb.append('=');
        sb.append(((this.status == null) ? "<null>" : this.status));
        sb.append(',');
        sb.append("ownerId");
        sb.append('=');
        sb.append(((this.ownerId == null) ? "<null>" : this.ownerId));
        sb.append(',');
        sb.append("visibility");
        sb.append('=');
        sb.append(((this.visibility == null) ? "<null>" : this.visibility));
        sb.append(',');
        sb.append("accessLevel");
        sb.append('=');
        sb.append(((this.accessLevel == null) ? "<null>" : this.accessLevel));
        sb.append(',');
        sb.append("swImageData");
        sb.append('=');
        sb.append(((this.swImageData == null) ? "<null>" : this.swImageData));
        sb.append(',');
        sb.append("minInstancesCount");
        sb.append('=');
        sb.append(((this.minInstancesCount == null) ? "<null>" : this.minInstancesCount));
        sb.append(',');
        sb.append("maxInstancesCount");
        sb.append('=');
        sb.append(((this.maxInstancesCount == null) ? "<null>" : this.maxInstancesCount));
        sb.append(',');
        sb.append("epoch");
        sb.append('=');
        sb.append(((this.epoch == null) ? "<null>" : this.epoch));
        sb.append(',');
        sb.append("metadata");
        sb.append('=');
        sb.append(((this.metadata == null) ? "<null>" : this.metadata));
        sb.append(',');
        sb.append("monitoringParameters");
        sb.append('=');
        sb.append(((this.monitoringParameters == null) ? "<null>" : this.monitoringParameters));
        sb.append(',');
        sb.append("name");
        sb.append('=');
        sb.append(((this.name == null) ? "<null>" : this.name));
        sb.append(',');
        sb.append("vendor");
        sb.append('=');
        sb.append(((this.vendor == null) ? "<null>" : this.vendor));
        sb.append(',');
        sb.append("version");
        sb.append('=');
        sb.append(((this.version == null) ? "<null>" : this.version));
        sb.append(',');
        sb.append("requiredPorts");
        sb.append('=');
        sb.append(((this.requiredPorts == null) ? "<null>" : this.requiredPorts));
        sb.append(',');
        if (sb.charAt((sb.length() - 1)) == ',') {
            sb.setCharAt((sb.length() - 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = ((result * 31) + ((this.metadata == null) ? 0 : this.metadata.hashCode()));
        result = ((result * 31) + ((this.instantiationLevelExpression == null) ? 0 : this.instantiationLevelExpression.hashCode()));
        result = ((result * 31) + ((this.vnfdId == null) ? 0 : this.vnfdId.hashCode()));
        result = ((result * 31) + ((this.description == null) ? 0 : this.description.hashCode()));
        result = ((result * 31) + ((this.version == null) ? 0 : this.version.hashCode()));
        result = ((result * 31) + ((this.monitoringParameters == null) ? 0 : this.monitoringParameters.hashCode()));
        result = ((result * 31) + ((this.flavourExpression == null) ? 0 : this.flavourExpression.hashCode()));
        result = ((result * 31) + ((this.vendor == null) ? 0 : this.vendor.hashCode()));
        result = ((result * 31) + ((this.name == null) ? 0 : this.name.hashCode()));
        result = ((result * 31) + ((this.connectionPoint == null) ? 0 : this.connectionPoint.hashCode()));
        result = ((result * 31) + ((this.id == null) ? 0 : this.id.hashCode()));
        result = ((result * 31) + ((this.ownerId == null) ? 0 : this.ownerId.hashCode()));
        result = ((result * 31) + ((this.accessLevel == null) ? 0 : this.accessLevel.hashCode()));
        result = ((result * 31) + ((this.swImageData == null) ? 0 : this.swImageData.hashCode()));
        result = ((result * 31) + ((this.epoch == null) ? 0 : this.epoch.hashCode()));
        result = ((result * 31) + ((this.visibility == null) ? 0 : this.visibility.hashCode()));
        result = ((result * 31) + ((this.parameters == null) ? 0 : this.parameters.hashCode()));
        result = ((result * 31) + ((this.requiredPorts == null) ? 0 : this.requiredPorts.hashCode()));
        result = ((result * 31) + ((this.minInstancesCount == null) ? 0 : this.minInstancesCount.hashCode()));
        result = ((result * 31) + ((this.maxInstancesCount == null) ? 0 : this.maxInstancesCount.hashCode()));
        result = ((result * 31) + ((this.sliceId == null) ? 0 : this.sliceId.hashCode()));
        result = ((result * 31) + ((this.status == null) ? 0 : this.status.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof SdkFunction)) {
            return false;
        }
        SdkFunction rhs = ((SdkFunction) other);

        return (
                ((this.metadata == rhs.metadata) || ((this.metadata != null) && this.metadata.equals(rhs.metadata)))
                && ((this.instantiationLevelExpression == rhs.instantiationLevelExpression) ||
                          ((this.instantiationLevelExpression != null) && this.instantiationLevelExpression.equals(rhs.instantiationLevelExpression)))
                && ((this.vnfdId == rhs.vnfdId) || ((this.vnfdId != null) && this.vnfdId.equals(rhs.vnfdId)))
                && ((this.description == rhs.description) || ((this.description != null) && this.description.equals(rhs.description)))
                && ((this.version == rhs.version) || ((this.version != null) && this.version.equals(rhs.version)))
                && ((this.monitoringParameters == rhs.monitoringParameters) || ((this.monitoringParameters != null) && this.monitoringParameters.equals(rhs.monitoringParameters)))
                && ((this.flavourExpression == rhs.flavourExpression) || ((this.flavourExpression != null) && this.flavourExpression.equals(rhs.flavourExpression)))
                && ((this.vendor == rhs.vendor) || ((this.vendor != null) && this.vendor.equals(rhs.vendor)))
                && ((this.name == rhs.name) || ((this.name != null) && this.name.equals(rhs.name)))
                && ((this.connectionPoint == rhs.connectionPoint) || ((this.connectionPoint != null) && this.connectionPoint.equals(rhs.connectionPoint)))
                && ((this.id == rhs.id) || ((this.id != null) && this.id.equals(rhs.id)))
                && ((this.ownerId == rhs.ownerId) || ((this.ownerId != null) && this.ownerId.equals(rhs.ownerId)))
                && ((this.visibility == rhs.visibility) || ((this.visibility != null) && this.visibility.equals(rhs.visibility)))
                && ((this.ownerId == rhs.ownerId) || ((this.ownerId != null) && this.ownerId.equals(rhs.ownerId)))
                && ((this.accessLevel == rhs.accessLevel) || ((this.accessLevel != null) && this.accessLevel.equals(rhs.accessLevel)))
                && ((this.swImageData == rhs.swImageData) || ((this.swImageData != null) && this.swImageData.equals(rhs.swImageData)))
                && ((this.minInstancesCount == rhs.minInstancesCount) || ((this.minInstancesCount != null) && this.minInstancesCount.equals(rhs.minInstancesCount)))
                && ((this.maxInstancesCount == rhs.maxInstancesCount) || ((this.maxInstancesCount != null) && this.maxInstancesCount.equals(rhs.maxInstancesCount)))
                && ((this.epoch == rhs.epoch) || ((this.epoch != null) && this.epoch.equals(rhs.epoch)))
                && ((this.requiredPorts == rhs.requiredPorts) || ((this.requiredPorts != null) && this.requiredPorts.equals(rhs.requiredPorts)))
                && ((this.status == rhs.status) || ((this.status != null) && this.status.equals(rhs.status)))
                && ((this.sliceId == rhs.sliceId) || ((this.sliceId != null) && this.sliceId.equals(rhs.sliceId)))
                && ((this.parameters == rhs.parameters) || ((this.parameters != null) && this.parameters.equals(rhs.parameters))));
    }

    @JsonIgnore
    @Override
    public void isValid() throws MalformedElementException {
        if(name == null || name.length() == 0)
            throw new MalformedElementException("Please provide valid name");
        if(sliceId == null || sliceId.length() == 0)
            throw new MalformedElementException("Please provide valid sliceId");
        if(ownerId == null || ownerId.length() == 0)
            throw new MalformedElementException("Please provide valid ownerId");
        if(version == null || version.length() == 0)
            throw new MalformedElementException("Please provide valid version");
        if(vendor == null || vendor.length() == 0)
            throw new MalformedElementException("Please provide valid vendor");
        if(vnfdId == null || vnfdId.length() == 0 || !checkVnfdIdFormat())
            throw new MalformedElementException("Please provide valid UUID as vnfdId");
        if(minInstancesCount == null || minInstancesCount < 1)
            throw new MalformedElementException("Please provide valid minInstancesCount (at least 1)");
        if(maxInstancesCount == null || maxInstancesCount < minInstancesCount)
            throw new MalformedElementException("Please provide valid maxInstancesCount (>= minInstancesCount)");
        if(!validateCps())
            throw new MalformedElementException("Please provide valid connection points");
        if(!validateExpressions())
            throw new MalformedElementException("Please provide valid expressions");
        if(!validateMonitoringParameters())
            throw new MalformedElementException("Please provide valid monitoring parameters");
        if(!validateRequiredPorts())
            throw new MalformedElementException("Please provide valid required ports");
        if(swImageData == null)
            throw new MalformedElementException("Please provide valid swImageData");
        else
            swImageData.isValid();

        /*
        return name != null && name.length() > 0
            && ownerId != null
            && groupId != null
            && validateCps()
            && version != null && version.length() > 0
            && vendor != null && vendor.length() > 0
            && vnfdId != null && vnfdId.length() > 0
            && checkVnfdIdFormat()
            && instantiationLevelExpression != null
            && flavourExpression != null
            && validateExpressions()
            && validateMonitoringParameters()
            && validateRequiredPorts()
            && swImageData.isValid()
            && minInstancesCount > 0
            && maxInstancesCount > 0 && maxInstancesCount >= minInstancesCount;

         */
    }

    private boolean checkVnfdIdFormat(){
        String regex = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";//UUID format
        if(vnfdId.matches(regex))
            return true;
        return false;
    }

    private boolean validateMonitoringParameters() {
        boolean validParameter = true;
        final Set<String> parametersName = new HashSet<String>();
        for (MonitoringParameter mp : monitoringParameters) {
            if(!mp.isValid()) {
                validParameter = false;
                break;
            }
            if (!parametersName.add(mp.getName())) {
                validParameter = false;
                break;
            }
            if (mp instanceof MonParamTransformed) {
                validParameter = validParameter && (monitoringParameters.stream().map(MonitoringParameter::getName).collect(Collectors.toSet()).contains(((MonParamTransformed) mp).getTargetParameterId()));
            }
            if (mp instanceof MonParamAggregated) {
                for (String s : ((MonParamAggregated) mp).getParametersId()) {
                    validParameter = validParameter && (monitoringParameters.stream().map(MonitoringParameter::getName).collect(Collectors.toSet()).contains(s));
                }
            }
        }

        return validParameter;
    }

    private boolean validateRequiredPorts() {
        boolean validPort = true;
        for(RequiredPort rp : requiredPorts){
            if(!rp.isValid()){
                validPort = false;
                break;
            }

            validPort = validPort && (connectionPoint.stream().map(ConnectionPoint::getName).collect(Collectors.toSet()).contains(rp.getConnectionPointId()));
        }

        return validPort;
    }

    private boolean validateCps() {
        return connectionPoint != null
            && connectionPoint.size() > 0
            && connectionPoint.stream().allMatch(cp -> cp.isValid() && cp.getInternalCpId() == null && cp.getInternalCpName() == null)
            && connectionPoint.stream()
                            .map(ConnectionPoint::getName)
                            .distinct()
                            .count() == connectionPoint.size()  // I.e. cp names are unique in the service
            && connectionPoint.stream().map(ConnectionPoint::isManagement).filter(x -> x).count() == 1; //one management connection point
    }

    private boolean validateExpressions() {
        try {
            // Check expressions are well-formed.
            getFlavourCompiledExpression();
            getILCompiledExpression();
            return true;
            // Validate against vnfd values??
            // e.g.
            // vnfd.getDfs().containsAll(((StringValuedExpression) getFlavourCompiledExpression()).getResultVars())
        } catch (Exception exc) {
            return false;
        }
    }

    @JsonIgnore
    @Override
    public Integer getFreeParametersNumber() {
        return parameters.size();
    }

}
