package it.nextworks.sdk;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.nextworks.sdk.enums.SdkServiceComponentType;

import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

/**
 * Created by Marco Capitani on 25/11/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "component_type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = SdkFunctionDescriptor.class, name = "SDK_FUNCTION"),
    @JsonSubTypes.Type(value = SdkServiceDescriptor.class, name = "SDK_SERVICE")
})
@MappedSuperclass
public abstract class SdkComponentInstance {

    @ManyToOne
    protected SdkServiceDescriptor outerService;

    public abstract SdkServiceComponentType getType();

    public void setOuterService(SdkServiceDescriptor outerService) {
        this.outerService = outerService;
    }

}
