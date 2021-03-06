/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.adf.engine.shared;

import org.kie.workbench.common.forms.adf.service.definitions.FormDefinitionSettings;
import org.kie.workbench.common.forms.model.FormDefinition;

/**
 * Service Entry Point for the ADF engine.
 */
public interface FormBuildingService {

    /**
     * Generates a {@link FormDefinition} for the given model if there are {@link FormDefinitionSettings} on the ADF
     * Engine for it.
     */
    FormDefinition generateFormForModel(Object model);

    /**
     * Generates a {@link FormDefinition} for the given Class if there are {@link FormDefinitionSettings} on the ADF
     * Engine for it.
     */
    FormDefinition generateFormForClass(Class clazz);

    /**
     * Generates a {@link FormDefinition} for the given className if there are {@link FormDefinitionSettings} on the ADF
     * Engine for it.
     */
    FormDefinition generateFormForClassName(String className);
}
