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
import it.nextworks.sdk.enums.SdkServiceComponentType;
import it.nextworks.sdk.enums.SdkServiceStatus;
import it.nextworks.sdk.enums.Visibility;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * SDKService
 * <p>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "status",
    "serviceId",
    "nsInfoId",
    "componentType",
    "parameters",
    "subDescriptor"
})
@Entity
public class SdkServiceDescriptor extends SdkComponentInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private SdkServiceStatus status;

    private String nsInfoId;

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SELECT)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @OrderColumn
    private List<BigDecimal> parameterValues;

    @OneToMany(mappedBy = "outerService", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @LazyCollection(LazyCollectionOption.FALSE)
    private Set<SdkFunctionDescriptor> subFunctions = new HashSet<>();

    @OneToMany(mappedBy = "outerService", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @LazyCollection(LazyCollectionOption.FALSE)
    private Set<SdkServiceDescriptor> subServices = new HashSet<>();

    @ManyToOne
    private SdkService template;

    private String sliceId;

    private SdkServiceDescriptor() {
        super();
        // JPA only
    }

    public SdkServiceDescriptor(
        SdkService template,
        List<BigDecimal> parameterValues,
        Set<SdkComponentInstance> subDescriptors,
        String sliceId
    ) {
        super();
        if (!(template.getFreeParametersNumber() == parameterValues.size())) {
            throw new IllegalArgumentException(String.format(
                "Parameter number not matching: expected %s, got %s",
                template.getFreeParametersNumber(),
                parameterValues.size()
            ));
        }
        this.parameterValues = parameterValues;
        this.template = template;
        this.sliceId = sliceId;
        setSubDescriptors(subDescriptors);
    }

    @Override
    @JsonProperty("componentType")
    public SdkServiceComponentType getType() {
        return SdkServiceComponentType.SDK_SERVICE;
    }

    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    @JsonProperty("id")
    void setId(Long id) {
        this.id = id;
    }

    @JsonIgnore
    public String getNsInfoId() {
        return nsInfoId;
    }

    @JsonIgnore
    public void setNsInfoId(String nsInfoId) {
        this.nsInfoId = nsInfoId;
    }

    @JsonIgnore
    public SdkService getTemplate() {
        return template;
    }

    @JsonProperty("status")
    public SdkServiceStatus getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(SdkServiceStatus status) {
        this.status = status;
    }

    @JsonProperty("subDescriptor")
    public Set<SdkComponentInstance> getSubDescriptors() {
        Set<SdkComponentInstance> output = new HashSet<>(subFunctions);
        output.addAll(subServices);
        return output;
    }

    @JsonProperty("subDescriptor")
    public void setSubDescriptors(Set<SdkComponentInstance> components) {
        Map<SdkServiceComponentType, List<SdkComponentInstance>> byType =
            components.stream().collect(Collectors.groupingBy(SdkComponentInstance::getType));
        if (byType.size() > 2) {
            throw new IllegalArgumentException(String.format(
                "Unknown component type(s). Expected %s, got %s",
                Arrays.asList("SDK_FUNCTION", "SDK_SERVICE"),
                byType.keySet()
            ));
        }
        subFunctions = byType.getOrDefault(SdkServiceComponentType.SDK_FUNCTION, Collections.emptyList()).stream()
            .map(SdkFunctionDescriptor.class::cast)
            .collect(Collectors.toSet());
        subServices = byType.getOrDefault(SdkServiceComponentType.SDK_SERVICE, Collections.emptyList()).stream()
            .map(SdkServiceDescriptor.class::cast)
            .collect(Collectors.toSet());
        validateComponents();

        for (SdkFunctionDescriptor subFunction : subFunctions) {
            subFunction.setOuterService(this);
        }

        for (SdkServiceDescriptor subService : subServices) {
            subService.setOuterService(this);
        }
    }

    @JsonProperty("serviceId")
    public Long getServiceId(){
        return this.template.getId();
    }

    @JsonProperty("sliceId")
    public String getSliceId() {
        return sliceId;
    }

    @JsonProperty("sliceId")
    public void setSliceId(String sliceId) {
        this.sliceId = sliceId;
    }

    private void validateComponents() {
        Set<Long> subServicesIds = subServices.stream()
            .map(ss -> ss.getTemplate().getId())
            .collect(Collectors.toSet());
        Set<Long> subFunctionIds = subFunctions.stream()
            .map(sf -> sf.getTemplate().getId())
            .collect(Collectors.toSet());
        for (SdkServiceComponent component : template.getComponents()) {
            switch (component.getType()) {
                case SDK_SERVICE:
                    if (!subServicesIds.remove(component.getComponentId())) {
                        throw new IllegalStateException(String.format(
                            "Invalid sub service instances: component %s not represented",
                            component.getId()
                        ));
                    }
                    break;
                case SDK_FUNCTION:
                    if (!subFunctionIds.remove(component.getComponentId())) {
                        throw new IllegalStateException(String.format(
                            "Invalid sub function instances: component %s not represented",
                            component.getId()
                        ));
                    }
                    break;
                default:
                    throw new IllegalStateException(String.format(
                        "Unknown component type %s",
                        component.getType()
                    ));
            }
        }
        if (!subServicesIds.isEmpty()) {
            throw new IllegalStateException(String.format(
                "Illegal sub service instances: instances %s is not related to any component",
                subServicesIds
            ));
        }
        if (!subFunctionIds.isEmpty()) {
            throw new IllegalStateException(String.format(
                "Illegal sub function instances: instances %s is not related to any component",
                subFunctionIds
            ));
        }
    }

    @JsonIgnore
    public boolean isValid() {
        return template != null
            && parameterValues != null
            && template.getFreeParametersNumber() == parameterValues.size()
            && sliceId != null;
    }

    @JsonProperty("parameters")
    public Map<String, BigDecimal> getParamsMap() {
        Map<String, BigDecimal> output = new HashMap<>();
        for (int i = 0; i < parameterValues.size(); i++) {
            output.put(template.getParameters().get(i), parameterValues.get(i));
        }
        return output;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(SdkServiceDescriptor.class.getName())
            .append('[');
        sb.append("status");
        sb.append('=');
        sb.append(((this.status == null) ? "<null>" : this.status));
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
        int result = super.hashCode();
        result = ((result * 31) + ((this.status == null) ? 0 : this.status.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof SdkServiceDescriptor)) {
            return false;
        }
        SdkServiceDescriptor rhs = ((SdkServiceDescriptor) other);
        return ((this.status == rhs.status) || ((this.status != null) && this.status.equals(rhs.status)))
            && super.equals(other);
    }

    @JsonIgnore
    public Integer getFreeParametersNumber() {
        return 0;
    }
}
