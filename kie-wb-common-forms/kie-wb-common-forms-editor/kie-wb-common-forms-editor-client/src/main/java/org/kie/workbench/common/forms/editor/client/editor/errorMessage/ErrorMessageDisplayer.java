/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.editor.client.editor.errorMessage;

import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.forms.editor.client.handler.formModel.FormModelBinder;
import org.kie.workbench.common.forms.editor.client.handler.formModel.FormModelsPresenter;
import org.kie.workbench.common.forms.editor.client.handler.formModel.container.FormModelCreationContainer;
import org.kie.workbench.common.forms.editor.client.resources.i18n.FormEditorConstants;
import org.kie.workbench.common.forms.editor.model.FormModelerContentError;
import org.uberfire.mvp.Command;

@Dependent
public class ErrorMessageDisplayer implements ErrorMessageDisplayerView.Presenter {

    private TranslationService translationService;

    private ErrorMessageDisplayerView view;

    FormModelsPresenter formModelsPresenter;

    private boolean showMoreEnabled = false;
    private boolean showMore = false;

    private FormModelerContentError error;

    private Command closeCommand;

    @Inject
    public ErrorMessageDisplayer(TranslationService translationService, ErrorMessageDisplayerView view) {
        this.translationService = translationService;
        this.view = view;
    }

    @PostConstruct
    void init() {
        view.init(this);
    }

    public void show(FormModelerContentError error, Command closeCommand) {
        PortablePreconditions.checkNotNull("error", error);
        PortablePreconditions.checkNotNull("closeCommand", closeCommand);

        this.error = error;
        this.closeCommand = closeCommand;

        view.setSourceType(error.getSourceType());

        showMoreEnabled = error.getFullMessage() != null;

        view.displayShowMoreAnchor(showMoreEnabled);

        if (showMoreEnabled) {
            showMore = false;
            view.setShowMoreLabel(translationService.getTranslation(FormEditorConstants.ShowMoreLabel));
        }

        if(error.getOriginalModel()!=null && error.getOriginalModel().allowsRebinding()){
            Optional<FormModelBinder> optional = formModelsPresenter.getRegisteredCreationManagers().stream().
                    map(FormModelCreationContainer::getCreationViewManager)
                    .filter(f->f.supports(error.getOriginalModel()))
                    .findAny();

            if(optional.isPresent()) {
                view.show(error.getShortMessage(), optional.get());
            } else {
                view.show(error.getShortMessage());
            }
        } else {
            view.show(error.getShortMessage());
        }
    }

    @Override
    public void notifyClose() {
        if (view.isClose()) {
            closeCommand.execute();
        }
    }

    @Override
    public void notifyShowMorePressed() {
        if (showMoreEnabled) {
            if (showMore) {
                view.show(error.getShortMessage());
                view.setShowMoreLabel(translationService.getTranslation(FormEditorConstants.ShowMoreLabel));
            } else {
                view.show(error.getFullMessage());
                view.setShowMoreLabel(translationService.getTranslation(FormEditorConstants.ShowLessLabel));
            }
            showMore = !showMore;
        }
    }

    public void enableContinue(boolean enable) {
        view.enableContinueButton(enable);
    }
}
