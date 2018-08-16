/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.data.modeller.client.formModel;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.forms.data.modeller.client.resources.i18n.DataModellerIntegrationConstants;
import org.kie.workbench.common.forms.data.modeller.model.DataObjectFormModel;
import org.kie.workbench.common.forms.data.modeller.service.DataObjectFormModelCreationService;
import org.kie.workbench.common.forms.editor.client.handler.formModel.FormModelBinder;
import org.kie.workbench.common.forms.model.FormModel;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.UberElement;

@Dependent
public class DataObjectFormModelBinder implements FormModelBinder<DataObjectFormModel>,
                                                  DataObjectFormModelCreationView.Presenter {

    protected Caller<DataObjectFormModelCreationService> dataObjectFormModelCreationService;

    protected DataObjectFormModelCreationView view;

    protected TranslationService translationService;

    @Inject
    public DataObjectFormModelBinder(Caller<DataObjectFormModelCreationService> dataObjectFormModelCreationService,
                                     DataObjectFormModelCreationView view,
                                     TranslationService translationService) {
        this.dataObjectFormModelCreationService = dataObjectFormModelCreationService;
        this.view = view;
        this.translationService = translationService;
    }

    @PostConstruct
    public void initialize() {
        view.init(this);
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public void init(Path projectPath) {
        dataObjectFormModelCreationService.call(dataObjectFormModels -> view.setFormModels((List<DataObjectFormModel>) dataObjectFormModels)).getAvailableDataObjects(
                projectPath);
    }

    @Override
    public DataObjectFormModel getFormModel() {
        return view.getSelectedFormModel();
    }

    @Override
    public boolean isValid() {
        if (getFormModel() == null) {
            view.setErrorMessage(translationService.getTranslation(DataModellerIntegrationConstants.InvalidDataObject));
            return false;
        }
        view.clearValidationErrors();
        return true;
    }

    @Override
    public String getLabel() {
        return translationService.getTranslation(DataModellerIntegrationConstants.DataObject);
    }

    @Override
    public void reset() {
        view.reset();
    }

    @Override
    public UberElement getView() {
        return view;
    }

    @Override
    public boolean supports(FormModel formModel) {
        return true;
    }
}
