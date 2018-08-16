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

import org.kie.workbench.common.forms.editor.client.handler.formModel.FormModelBinder;

public interface ErrorMessageDisplayerView {

    void init(Presenter presenter);

    void setSourceType(String sourceType);

    void show(String message);

    void displayShowMoreAnchor(boolean display);

    void enableContinueButton(boolean enable);

    void setShowMoreLabel(String label);

    void enableRebindOption(FormModelBinder manager);

    boolean isClose();

    void show(String shortMessage, FormModelBinder formModelBinder);

    interface Presenter {

        void notifyShowMorePressed();

        void notifyClose();
    }
}
