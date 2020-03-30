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
import it.nextworks.sdk.enums.Visibility;
import it.nextworks.sdk.enums.ConnectionPointType;
import it.nextworks.sdk.enums.SdkServiceComponentType;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * SDKServiceTemplate
 * <p>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "sliceId",
    "ownerId",
    "name",
    "version",
    "designer",
    "parameters",
    "license",
    "link",
    "components",
    "metadata",
    "connectionPoints",
    "l3Connectivity",
    "extMonitoringParameters",
    "intMonitoringParameters",
    "visibility",
    "groupId",
    "accessLevel"
})
@Entity
public class SdkService implements InstantiableCandidate {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String version = "1.0";

    private String designer;

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @OrderColumn
    private List<String> parameters = new ArrayList<>();

    @OneToMany(mappedBy = "outerService", cascade = CascadeType.ALL, orphanRemoval=true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @LazyCollection(LazyCollectionOption.FALSE)
    private Set<SubFunction> subFunctions = new HashSet<>();

    @OneToMany(mappedBy = "outerService", cascade = CascadeType.ALL, orphanRemoval=true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @LazyCollection(LazyCollectionOption.FALSE)
    private Set<SubService> subServices = new HashSet<>();

    @Embedded
    private License license;

    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL, orphanRemoval=true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @LazyCollection(LazyCollectionOption.FALSE)
    private Set<Link> link = new HashSet<>();

    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL, orphanRemoval=true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @LazyCollection(LazyCollectionOption.FALSE)
    private Set<L3Connectivity> l3Connectivity = new HashSet<>();

    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL, orphanRemoval=true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @LazyCollection(LazyCollectionOption.FALSE)
    private Set<Metadata> metadata = new HashSet<>();

    @OneToMany(mappedBy = "sdkServiceExt", cascade = CascadeType.ALL, orphanRemoval=true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @LazyCollection(LazyCollectionOption.FALSE)
    private Set<MonitoringParameter> extMonitoringParameters = new HashSet<>();

    @OneToMany(mappedBy = "sdkServiceInt", cascade = CascadeType.ALL, orphanRemoval=true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @LazyCollection(LazyCollectionOption.FALSE)
    private Set<MonitoringParameter> intMonitoringParameters = new HashSet<>();

    @OneToMany(mappedBy = "sdkService", cascade = CascadeType.ALL, orphanRemoval=true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @LazyCollection(LazyCollectionOption.FALSE)
    private Set<ConnectionPoint> connectionPoint = new HashSet<>();

    @OneToMany(mappedBy = "sdkService", cascade = CascadeType.ALL, orphanRemoval=true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @LazyCollection(LazyCollectionOption.FALSE)
    private Set<ServiceAction> actions = new HashSet<>();

    @OneToMany(mappedBy = "sdkService", cascade = CascadeType.ALL, orphanRemoval=true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @LazyCollection(LazyCollectionOption.FALSE)
    private Set<ServiceActionRule> actionRules = new HashSet<>();

    private String ownerId;

    //private String groupId;

    private Visibility visibility = Visibility.fromValue("PRIVATE");

    private Integer accessLevel = 4;

    private String sliceId;

    @Override
    public SdkServiceDescriptor makeDescriptor(List<BigDecimal> parameterValues) {
        Set<SdkComponentInstance> subInstances = new HashSet<>();
        Map<String, BigDecimal> parameterMap = new HashMap<>();
        if (!(parameters.size() == parameterValues.size())) {
            throw new IllegalArgumentException(String.format(
                "Parameter values amount invalid. Expected %d, got %s",
                parameters.size(),
                parameterValues.size()
            ));
        }
        for (int i = 0; i < parameters.size(); i++) {
            parameterMap.put(parameters.get(i), parameterValues.get(i));
        }
        for (SdkServiceComponent component : getComponents()) {
            subInstances.add(component.instantiate(parameterMap));
        }
        return new SdkServiceDescriptor(
            this,
            parameterValues,
            subInstances,
            this.sliceId
        );
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("actions")
    public Set<ServiceAction> getActions() {
        return actions;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("actions")
    public void setActions(Set<ServiceAction> actions) {
        //this.actions = actions;
        this.actions.clear();
        this.actions.addAll(actions);

        for (ServiceAction sa : this.actions) {
            sa.setSdkService(this);
        }
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("actionRules")
    public Set<ServiceActionRule> getActionRules() {
        return actionRules;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("actionRules")
    public void setActionRules(Set<ServiceActionRule> actionRules) {

        this.actionRules.clear();
        this.actionRules.addAll(actionRules);

        for (ServiceActionRule sa : this.actionRules) {
            sa.setSdkService(this);
        }
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("connectionPoints")
    public Set<ConnectionPoint> getConnectionPoint() {
        return connectionPoint;
    }

    @JsonProperty("connectionPoints")
    public void setConnectionPoint(Set<ConnectionPoint> connectionPoint) {

        this.connectionPoint.clear();
        this.connectionPoint.addAll(connectionPoint);

        for (ConnectionPoint cp : this.connectionPoint) {
            cp.setSdkService(this);
        }
    }

    @JsonIgnore
    private Set<ConnectionPoint> getLowerCPs() {
        if (!isResolved()) {
            throw new IllegalStateException("Internal cps not available: service not resolved");
        }
        Stream<ConnectionPoint> servicesCp = subServices.stream()
            .flatMap(ss -> ss.getComponent().getConnectionPoint().stream()
                .filter(cp -> cp.getType().equals(ConnectionPointType.EXTERNAL))
            );
        Stream<ConnectionPoint> funcCp = subFunctions.stream()
            .flatMap(sf -> sf.getComponent().getConnectionPoint().stream()
                .filter(cp -> cp.getType().equals(ConnectionPointType.EXTERNAL))
            );
        return Stream.concat(servicesCp, funcCp).collect(Collectors.toSet());
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("parameters")
    @Override
    public List<String> getParameters() {
        return parameters;
    }

    @JsonProperty("parameters")
    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

    @JsonIgnore
    @Override
    public Integer getFreeParametersNumber() {
        return parameters.size();
    }

    @JsonProperty("designer")
    public String getDesigner() {
        return designer;
    }

    @JsonProperty("designer")
    public void setDesigner(String designer) {
        this.designer = designer;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("component")
    public Set<SdkServiceComponent> getComponents() {
        Set<SdkServiceComponent> output = new HashSet<>(subFunctions);
        output.addAll(subServices);
        return output;
    }

    @JsonProperty("component")
    public void setComponents(Set<SdkServiceComponent> components) {
        Map<SdkServiceComponentType, List<SdkServiceComponent>> byType =
            components.stream().collect(Collectors.groupingBy(SdkServiceComponent::getType));
        if (byType.size() > 2) {
            throw new IllegalArgumentException(String.format(
                "Unknown component type(s). Expected %s, got %s",
                Arrays.asList(SubFunction.class.getSimpleName(), SubService.class.getSimpleName()),
                byType.keySet()
            ));
        }
        subFunctions.clear();
        subServices.clear();
        subFunctions.addAll(byType.getOrDefault(SdkServiceComponentType.SDK_FUNCTION, Collections.emptyList()).stream()
            .map(SubFunction.class::cast)
            .collect(Collectors.toSet()));
        subServices.addAll(byType.getOrDefault(SdkServiceComponentType.SDK_SERVICE, Collections.emptyList()).stream()
            .map(SubService.class::cast)
            .collect(Collectors.toSet()));
        if (!validateComponents()) {
            throw new IllegalArgumentException(
                "Invalid components provided. Make sure all are valid and pointing to saved entities."
            );
        }

        for (SubFunction subFunction : this.subFunctions) {
            subFunction.setOuterService(this);
        }
        for (SubService subService : this.subServices) {
            subService.setOuterService(this);
        }
    }

    @JsonProperty("id")
    @Override
    public Long getId() {
        return id;
    }

    @JsonProperty("id")
    public void setServiceId(String serviceId) {
        this.id = Long.valueOf(serviceId);
    }

    void setId(Long id) {
        this.id = id;
    }

    @JsonProperty("sliceId")
    public String getSliceId() {
        return sliceId;
    }

    @JsonProperty("sliceId")
    public void setSliceId(String sliceId) {
        this.sliceId = sliceId;
    }

    @JsonProperty("license")
    public License getLicense() {
        return license;
    }

    @JsonProperty("license")
    public void setLicense(License license) {
        this.license = license;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("link")
    public Set<Link> getLink() {
        return link;
    }

    @JsonProperty("link")
    public void setLink(Set<Link> link) {
        this.link.clear();
        this.link.addAll(link);

        for (Link l : this.link) {
            l.setService(this);
        }
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("l3Connectivity")
    public Set<L3Connectivity> getL3Connectivity() {
        return l3Connectivity;
    }

    @JsonProperty("l3Connectivity")
    public void setL3Connectivity(Set<L3Connectivity> l3Connectivity) {
        this.l3Connectivity.clear();
        this.l3Connectivity.addAll(l3Connectivity);

        for (L3Connectivity l3c : this.l3Connectivity) {
            l3c.setService(this);
        }
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("metadata")
    public Map<String, String> getMetadata() {
        return metadata.stream().collect(Collectors.toMap(Metadata::getKey, Metadata::getValue));
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("metadata")
    public void setMetadata(Map<String, String> metadata) {
        this.metadata.clear();
        this.metadata.addAll(metadata.entrySet().stream()
            .map(e -> new Metadata(e.getKey(), e.getValue(), this))
            .collect(Collectors.toSet()));

        for (Metadata metadatum : this.metadata) {
            metadatum.setService(this);
        }
    }

    @JsonIgnore
    public Set<Metadata> getMetadata2() {
        return metadata;
    }

    @JsonIgnore
    public void setMetadata2(Set<Metadata> metadata) {
        this.metadata = metadata;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("extMonitoringParameters")
    public Set<MonitoringParameter> getExtMonitoringParameters() {
        return extMonitoringParameters;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("extMonitoringParameters")
    public void setExtMonitoringParameters(Set<MonitoringParameter> extMonitoringParameters) {
        this.extMonitoringParameters.clear();
        this.extMonitoringParameters.addAll(extMonitoringParameters);

        for (MonitoringParameter mp : this.extMonitoringParameters) {
            mp.setSdkServiceExt(this);
        }

    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("intMonitoringParameters")
    public Set<MonitoringParameter> getIntMonitoringParameters() {
        return intMonitoringParameters;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("intMonitoringParameters")
    public void setIntMonitoringParameters(Set<MonitoringParameter> intMonitoringParameters) {
        this.intMonitoringParameters.clear();
        this.intMonitoringParameters.addAll(intMonitoringParameters);

        for (MonitoringParameter mp : this.intMonitoringParameters) {
            mp.setSdkServiceInt(this);
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

    @JsonProperty("version")
    public String getVersion() {
        return version;
    }

    @JsonProperty("version")
    public void setVersion(String version) {
        this.version = version;
    }

    @JsonProperty("ownerId")
    public String getOwnerId() {
        return ownerId;
    }

    @JsonProperty("ownerId")
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    @JsonProperty("visibility")
    public Visibility getVisibility() {
        return visibility;
    }

    @JsonProperty("visibility")
    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
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

    @Override
    @JsonIgnore
    public SdkServiceComponentType getType() {
        return SdkServiceComponentType.SDK_SERVICE;
    }

    public void resolveComponents(Set<SdkFunction> functions, Set<SdkService> services) throws MalformedElementException{

        this.isValid();

        Map<Long, SdkFunction> functionMap = functions.stream()
            .collect(Collectors.toMap(SdkFunction::getId, Function.identity()));
        for (SubFunction subFunction : subFunctions) {
            SdkFunction function = functionMap.get(subFunction.getComponentId());
            if (function == null) {
                throw new IllegalArgumentException(String.format(
                    "Invalid components: function %s missing",
                    subFunction.getComponentId()
                ));
            }
            subFunction.setComponent(function);
        }
        Map<Long, SdkService> serviceMap = services.stream()
            .collect(Collectors.toMap(SdkService::getId, Function.identity()));
        for (SubService subService : subServices) {
            SdkService service = serviceMap.get(subService.getComponentId());
            if (service == null) {
                throw new IllegalArgumentException(String.format(
                    "Invalid components: service %s missing",
                    subService.getComponentId()
                ));
            }
            subService.setComponent(service);
        }
        resolveCps();
        for (L3Connectivity l3c : l3Connectivity) {
            Map<String, ConnectionPoint> byName = getConnectionPoint().stream().collect(Collectors.toMap(
                ConnectionPoint::getName,
                Function.identity()
            ));
            l3c.setConnectionPoint(byName.get(l3c.getConnectionPointName()));
        }
        for (Link l : link) {
            l.setConnectionPoints(connectionPoint);
        }
    }

    private void resolveCps() {
        // Note: services can "forget" lower CPs, if they don't plan to connect to them
        Set<Long> lowerCPIds = getLowerCPs().stream().map(ConnectionPoint::getId).collect(Collectors.toSet());
        connectionPoint.stream()
            .filter(cp -> cp.getType().equals(ConnectionPointType.INTERNAL))
            .forEach(
                cp -> {
                    if (!lowerCPIds.contains(cp.getInternalCpId())) {
                        throw new IllegalStateException(String.format(
                            "Invalid connection points: internal CP %s referencing invalid lower cp %s",
                            cp.getName(),
                            cp.getInternalCpId()
                        ));
                    }
                }
            );
    }

    private boolean validateExpressions() {
        return getComponents().stream().allMatch(c -> c.validateParameters(parameters));
    }

    private boolean validateComponents() {
        return // pre-check
            subFunctions != null
                && subServices != null
                && (subFunctions.size() + subServices.size()) > 0
                // components are valid & registered
                && subFunctions.stream().allMatch(SdkServiceComponent::isValid)
                && subServices.stream().allMatch(SdkServiceComponent::isValid)
                // no duplicate components
                && subFunctions.stream().map(SdkServiceComponent::getComponentId).count() == subFunctions.size()
                && subServices.stream().map(SdkServiceComponent::getComponentId).count() == subServices.size();
    }

    @Transient
    @JsonIgnore
    private Map<String, String> associateCpCache = new HashMap<>();

    @JsonIgnore
    private boolean isNotPartOfIntToExtBridge(ConnectionPoint cp) {
        String cpName = cp.getName();
        if (associateCpCache.containsKey(cpName)) {
            return associateCpCache.get(cpName) == null;
        }
        switch (cp.getType()) {
            case EXTERNAL:
                 if (cp.getInternalCpName() == null) {
                     associateCpCache.put(cpName, null);
                     return true;
                 } else {
                     String otherCp = cp.getInternalCpName();
                     associateCpCache.put(cpName, otherCp);
                     associateCpCache.put(otherCp, cpName);
                     return false;
                 }
            case INTERNAL:
                Optional<ConnectionPoint> match = connectionPoint.stream()
                    .filter(otherCp ->
                        otherCp.getType().equals(ConnectionPointType.EXTERNAL)
                            && cpName.equals(otherCp.getInternalCpName())
                    )
                    .findAny();
                if (match.isPresent()) {
                    String otherCp = match.get().getName();
                    associateCpCache.put(cpName, otherCp);
                    associateCpCache.put(otherCp, cpName);
                    return false;
                } else {
                    associateCpCache.put(cpName, null);
                    return true;
                }
            default:
                throw new IllegalStateException(String.format(
                    "Unknown cp type %s on cp %s",
                    cp.getType(),
                    cpName
                ));
        }
    }

    private boolean validateLinks() {
        boolean preCheck = link != null
            && !link.isEmpty();
        if (!preCheck) {
            return false;
        }
        Set<ConnectionPoint> thisCps = getConnectionPoint();
        Set<String> availableCpIds = thisCps.stream()
            .filter(this::isNotPartOfIntToExtBridge)
            .map(ConnectionPoint::getName)
            .collect(Collectors.toSet());
        // Check correspondence between links and cps
        Set<String> takenCps = new HashSet<>();
        for (Link l : link) {
            if(!l.isValid())
                return false;
            Set<String> linkCps = new HashSet<>(l.getConnectionPointNames());
            Set<Integer> componentIndexes = new HashSet<>();
            for(ConnectionPoint cp : thisCps){
                //check if the link is connected to multiple Internal Cps of the same component
                if(cp.getType() == ConnectionPointType.INTERNAL && linkCps.contains(cp.getName())){
                    if(!componentIndexes.add(cp.getComponentIndex())) {
                        throw new IllegalStateException(String.format(
                            "Invalid link %s, connected to multiple Connection Points of the same component", l.getId()));
                    }
                }
                //check uf the link is connected to multiple External Cps
                if(cp.getType() == ConnectionPointType.EXTERNAL && linkCps.contains(cp.getName())){
                    if(!componentIndexes.add(-1)){
                        throw new IllegalStateException(String.format(
                            "Invalid link %s, connected to multiple External Connection Points", l.getId()));
                    }
                }
            }
            if (!availableCpIds.containsAll(linkCps)) {
                // some cp is missing
                linkCps.removeAll(availableCpIds);
                if (takenCps.containsAll(linkCps)) {
                    // already used
                    throw new IllegalStateException(String.format(
                        "Invalid links, CPs '%s' are connected to multiple links",
                        linkCps
                    ));
                } else {
                    // some are unknown
                    linkCps.removeAll(takenCps);
                    throw new IllegalStateException(String.format(
                        "Invalid links, CPs '%s' are not available for linking",
                        linkCps
                    ));
                }
            }
            availableCpIds.removeAll(linkCps);
            takenCps.addAll(linkCps);
        }
        return true;
    }

    private boolean validateCps() {
        return connectionPoint != null
            && connectionPoint.stream().allMatch(cp -> cp.isValid() && (!(cp.getType() == ConnectionPointType.INTERNAL) || cp.getInternalCpId() != null))
            && connectionPoint.stream()
                            .map(ConnectionPoint::getName)
                            .distinct()
                            .count() == connectionPoint.size();  // I.e. cp names are unique in the service
    }

    private boolean validateL3Connectivity() {
        return l3Connectivity != null
            && l3Connectivity.stream().allMatch(L3Connectivity::isValid);
        // TODO check the connection point names match
    }

    private boolean validateComponentIndex(){
        boolean validComponent = true;
        final Set<Integer> componentIndexes = new HashSet<Integer>();
        for(SdkServiceComponent component : this.getComponents())
        if (!componentIndexes.add(component.getComponentIndex())) {
            validComponent = false;
            break;
        }

        return validComponent;
    }

    @JsonIgnore
    @Override
    public void isValid() throws MalformedElementException{
        if(name == null || name.length() == 0)
            throw new MalformedElementException("Please provide valid name");
        if(sliceId == null || sliceId.length() == 0)
            throw new MalformedElementException("Please provide valid sliceId");
        if(ownerId == null || ownerId.length() == 0)
            throw new MalformedElementException("Please provide valid ownerId");
        if(designer == null || designer.length() == 0)
            throw new MalformedElementException("Please provide valid designer");
        if(version == null || version.length() == 0)
            throw new MalformedElementException("Please provide valid version");
        if(license == null || !license.isValid())
            throw new MalformedElementException("Please provide valid license");
        if(!validateL3Connectivity())
            throw new MalformedElementException("Please provide valid l3 connectivity");
        if(!validateMonitoringParameters())
            throw new MalformedElementException("Please provide valid monitoring parameters");
        if(!validateAction())
            throw new MalformedElementException("Please provide valid actions and action rules");
        if(!validateCps())
            throw new MalformedElementException("Please provide valid connection points");
        if(!validateExpressions())
            throw new MalformedElementException("Please provide valid parameters");
        if(!validateComponents() || !validateComponentIndex())
            throw new MalformedElementException("Please provide valid components");
        try{
            if(!validateLinks())
                throw new MalformedElementException("Please provide valid links");
        }catch(IllegalStateException e){
            throw new MalformedElementException(e.getMessage());
        }

        /*
        return  name != null
                && ownerId != null
                && groupId != null
                && designer != null && designer.length() > 0
                && version != null && version.length() > 0
                && license != null && license.isValid()
                && validateComponents()
                && validateComponentIndex()
                && validateL3Connectivity()
                && validateMonitoringParameters()
                && validateAction()
                && parameters != null
                && validateLinks()
                && validateExpressions()
                && validateCps();
         */
    }

    private boolean validateAction(){
        boolean validActions = true;
        final Set<String> actionNames = new HashSet<String>();
        for(ServiceAction ac : actions){
            if(!ac.isValid() || !actionNames.add(ac.getName())) {
                validActions = false;
                break;
            }
        }

        final Set<String> monitoringParametersNames = new HashSet<>();
        for(MonitoringParameter monitorigParameter : extMonitoringParameters)
            monitoringParametersNames.add(monitorigParameter.getName());

        if(actionRules != null) {
            for(ServiceActionRule ar : actionRules){
                if(!ar.isValid() || !actionNames.containsAll(ar.getActionsId())){
                    validActions = false;
                    break;
                }
                for(RuleCondition condition : ar.getConditions()){
                    if(!monitoringParametersNames.contains(condition.getParameterId())) {
                        validActions = false;
                        break;
                    }
                }
                /*
                for(RuleCondition rc : ar.getConditions()){
                    validActions = validActions && (intMonitoringParameters.stream().map(MonitoringParameter::getName).collect(Collectors.toSet()).contains(rc.getParameterId())
                    || extMonitoringParameters.stream().map(MonitoringParameter::getName).collect(Collectors.toSet()).contains(rc.getParameterId()));
                }
                 */
            }
        }

        return validActions;
    }

    private boolean validateMonitoringParameters(){
        boolean validParameter = true;
        final Set<String> parametersName = new HashSet<String>();
        Set<MonitoringParameter> monitoringParameters = new HashSet<>();
        monitoringParameters.addAll(extMonitoringParameters);
        monitoringParameters.addAll(intMonitoringParameters);
        for(MonitoringParameter mp : monitoringParameters){
            if(!mp.isValid() || !parametersName.add(mp.getName())) {
                validParameter = false;
                break;
            }
            if(mp instanceof MonParamTransformed){
                validParameter = validParameter && (monitoringParameters.stream().map(MonitoringParameter::getName).collect(Collectors.toSet()).contains(((MonParamTransformed) mp).getTargetParameterId()));
            }
            if(mp instanceof MonParamAggregated){
                for(String s : ((MonParamAggregated) mp).getParametersId()){
                    validParameter = validParameter && (monitoringParameters.stream().map(MonitoringParameter::getName).collect(Collectors.toSet()).contains(s));
                }
            }
        }

        return validParameter;
    }

    private void appendContents(StringBuilder sb) {
        sb.append("\n    ");
        sb.append("designer");
        sb.append('=');
        sb.append(((this.designer == null) ? "<null>" : this.designer));
        sb.append(',');
        sb.append("\n    ");
        sb.append("subFunctions");
        sb.append('=');
        sb.append(((this.subFunctions == null) ? "<null>" : this.subFunctions));
        sb.append(',');
        sb.append("\n    ");
        sb.append("subServices");
        sb.append('=');
        sb.append(((this.subServices == null) ? "<null>" : this.subServices));
        sb.append(',');
        sb.append("\n    ");
        sb.append("id");
        sb.append('=');
        sb.append(((this.id == null) ? "<null>" : this.id));
        sb.append(',');
        sb.append("\n    ");
        sb.append("sliceId");
        sb.append('=');
        sb.append(((this.sliceId == null) ? "<null>" : this.sliceId));
        sb.append(',');
        sb.append("\n    ");
        sb.append("ownerId");
        sb.append('=');
        sb.append(((this.ownerId == null) ? "<null>" : this.ownerId));
        sb.append(',');
        sb.append("\n    ");
        sb.append("visibility");
        sb.append('=');
        sb.append(((this.visibility == null) ? "<null>" : this.visibility));
        sb.append(',');
        sb.append("\n    ");
        sb.append("accessLevel");
        sb.append('=');
        sb.append(((this.accessLevel == null) ? "<null>" : this.accessLevel));
        sb.append(',');
        sb.append("\n    ");
        sb.append("license");
        sb.append('=');
        sb.append(((this.license == null) ? "<null>" : this.license));
        sb.append(',');
        sb.append("\n    ");
        sb.append("link");
        sb.append('=');
        sb.append(((this.link == null) ? "<null>" : this.link));
        sb.append(',');
        sb.append("\n    ");
        sb.append("l3Connectivity");
        sb.append('=');
        sb.append(((this.l3Connectivity == null) ? "<null>" : this.l3Connectivity));
        sb.append(',');
        sb.append("\n    ");
        sb.append("metadata");
        sb.append('=');
        sb.append(((this.metadata == null) ? "<null>" : this.metadata));
        sb.append(',');
        sb.append("\n    ");
        sb.append("extMonitoringParameters");
        sb.append('=');
        sb.append(((this.extMonitoringParameters == null) ? "<null>" : this.extMonitoringParameters));
        sb.append(',');
        sb.append("\n    ");
        sb.append("intMonitoringParameters");
        sb.append('=');
        sb.append(((this.intMonitoringParameters == null) ? "<null>" : this.intMonitoringParameters));
        sb.append(',');
        sb.append("\n    ");
        sb.append("name");
        sb.append('=');
        sb.append(((this.name == null) ? "<null>" : this.name));
        sb.append(',');
        sb.append("version");
        sb.append('=');
        sb.append(((this.version == null) ? "<null>" : this.version));
        sb.append(',');
        sb.append("actions");
        sb.append('=');
        sb.append(((this.actions == null) ? "<null>" : this.actions));
        sb.append(',');
        sb.append("version");
        sb.append('=');
        sb.append(((this.actionRules == null) ? "<null>" : this.actionRules));
        sb.append(',');
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(SdkService.class.getName())
            .append('[');
        sb.append("parameters");
        sb.append('=');
        sb.append(((this.parameters == null) ? "<null>" : this.parameters));
        sb.append(',');
        appendContents(sb);
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
        result = ((result * 31) + ((this.subFunctions == null) ? 0 : this.subFunctions.hashCode()));
        result = ((result * 31) + ((this.subServices == null) ? 0 : this.subServices.hashCode()));
        result = ((result * 31) + ((this.metadata == null) ? 0 : this.metadata.hashCode()));
        result = ((result * 31) + ((this.l3Connectivity == null) ? 0 : this.l3Connectivity.hashCode()));
        result = ((result * 31) + ((this.link == null) ? 0 : this.link.hashCode()));
        result = ((result * 31) + ((this.designer == null) ? 0 : this.designer.hashCode()));
        result = ((result * 31) + ((this.version == null) ? 0 : this.version.hashCode()));
        result = ((result * 31) + ((this.license == null) ? 0 : this.license.hashCode()));
        result = ((result * 31) + ((this.extMonitoringParameters == null) ? 0 : this.extMonitoringParameters.hashCode()));
        result = ((result * 31) + ((this.intMonitoringParameters == null) ? 0 : this.intMonitoringParameters.hashCode()));
        result = ((result * 31) + ((this.name == null) ? 0 : this.name.hashCode()));
        result = ((result * 31) + ((this.id == null) ? 0 : this.id.hashCode()));
        result = ((result * 31) + ((this.parameters == null) ? 0 : this.parameters.hashCode()));
        result = ((result * 31) + ((this.ownerId == null) ? 0 : this.ownerId.hashCode()));
        result = ((result * 31) + ((this.visibility == null) ? 0 : this.visibility.hashCode()));
        result = ((result * 31) + ((this.accessLevel == null) ? 0 : this.accessLevel.hashCode()));
        result = ((result * 31) + ((this.actions == null) ? 0 : this.actions.hashCode()));
        result = ((result * 31) + ((this.actionRules == null) ? 0 : this.actionRules.hashCode()));
        result = ((result * 31) + ((this.sliceId == null) ? 0 : this.sliceId.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof SdkService)) {
            return false;
        }
        SdkService rhs = ((SdkService) other);
        return (((((((((((((((((((((this.subFunctions == rhs.subFunctions) || ((this.subFunctions != null) && this.subFunctions.equals(rhs.subFunctions)))
            && ((this.subServices == rhs.subServices) || ((this.subServices != null) && this.subServices.equals(rhs.subServices))))
            && ((this.metadata == rhs.metadata) || ((this.metadata != null) && this.metadata.equals(rhs.metadata))))
            && ((this.l3Connectivity == rhs.l3Connectivity) || ((this.l3Connectivity != null) && this.l3Connectivity.equals(rhs.l3Connectivity))))
            && ((this.link == rhs.link) || ((this.link != null) && this.link.equals(rhs.link))))
            && ((this.designer == rhs.designer) || ((this.designer != null) && this.designer.equals(rhs.designer))))
            && ((this.version == rhs.version) || ((this.version != null) && this.version.equals(rhs.version))))
            && ((this.license == rhs.license) || ((this.license != null) && this.license.equals(rhs.license))))
            && ((this.extMonitoringParameters == rhs.extMonitoringParameters) || ((this.extMonitoringParameters != null) && this.extMonitoringParameters.equals(rhs.extMonitoringParameters))))
            && ((this.intMonitoringParameters == rhs.intMonitoringParameters) || ((this.intMonitoringParameters != null) && this.intMonitoringParameters.equals(rhs.intMonitoringParameters))))
            && ((this.name == rhs.name) || ((this.name != null) && this.name.equals(rhs.name))))
            && ((this.id == rhs.id) || ((this.id != null) && this.id.equals(rhs.id))))
            && ((this.ownerId == rhs.ownerId) || ((this.ownerId != null) && this.ownerId.equals(rhs.ownerId))))
            && ((this.visibility == rhs.visibility) || ((this.visibility != null) && this.visibility.equals(rhs.visibility))))
            && ((this.accessLevel == rhs.accessLevel) || ((this.accessLevel != null) && this.accessLevel.equals(rhs.accessLevel))))
            && ((this.actions == rhs.actions) || ((this.actions != null) && this.actions.equals(rhs.actions))))
            && ((this.actionRules == rhs.actionRules) || ((this.actionRules != null) && this.actionRules.equals(rhs.actionRules))))
            && ((this.sliceId == rhs.sliceId) || ((this.sliceId != null) && this.sliceId.equals(rhs.sliceId))))
            && ((this.parameters == rhs.parameters) || ((this.parameters != null) && this.parameters.equals(rhs.parameters)))));
    }

    @JsonIgnore
    private boolean isResolved() {
        return getComponents().stream().allMatch(SdkServiceComponent::isResolved);
    }

    @PrePersist
    private void prePersist() {
        if (!isResolved()) {
            throw new IllegalStateException("Cannot persist service: not resolved");
        }
    }

    @PostLoad
    private void postLoad() {

        for (Link l : link) {
            l.setConnectionPoints(connectionPoint);
        }

        Map<String, ConnectionPoint> byName = getConnectionPoint().stream().collect(Collectors.toMap(
            ConnectionPoint::getName,
            Function.identity()
        ));

        for (L3Connectivity l3c : l3Connectivity) {
            l3c.setConnectionPoint(byName.get(l3c.getConnectionPointName()));
        }
    }
}
