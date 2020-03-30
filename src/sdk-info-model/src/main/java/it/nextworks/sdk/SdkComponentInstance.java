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
    property = "componentType")
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
