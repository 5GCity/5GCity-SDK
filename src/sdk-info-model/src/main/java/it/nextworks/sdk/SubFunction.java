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

import it.nextworks.sdk.enums.SdkServiceComponentType;

import javax.persistence.Entity;
import java.util.List;

/**
 * Created by Marco Capitani on 04/12/18.
 *
 * @author Marco Capitani <m.capitani AT nextworks.it>
 */
@Entity
public class SubFunction extends SdkServiceComponent<SdkFunction> {

    private SubFunction() {
        this.type = SdkServiceComponentType.SDK_FUNCTION;
    }

    /**
     * Constructor.
     * <p>
     * Constraint: mappingExpression.size().equals(component.getFreeParametersNumber())
     *
     * @param componentId       the component, should be not null
     * @param mappingExpression the mapping expressions.
     * @throws IllegalArgumentException if the arguments do not satisfy the constraint
     */
    public SubFunction(Long componentId, Integer componentIndex, List<String> mappingExpression, SdkService outerService) {
        this.componentId = componentId;
        this.mappingExpression = mappingExpression;
        this.componentIndex = componentIndex;
        this.outerService = outerService;
        this.type = SdkServiceComponentType.SDK_FUNCTION;
        if (!isValid()) {
            throw new IllegalArgumentException("Invalid arguments");
        }
    }
}
