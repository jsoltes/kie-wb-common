/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.widgets.canvas.actions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandManager;
import org.kie.workbench.common.stunner.core.client.command.RequiresCommandManager;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mvp.Command;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NameEditBoxWidgetTest {

    public static final String NAME = "name";
    public static final String MODIFIED_NAME = "modified_name";
    public static final String ID = "id";

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;

    @Mock
    private NameEditBoxWidgetView view;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private Command closeCallback;

    @Mock
    private RequiresCommandManager.CommandManagerProvider<AbstractCanvasHandler> commandProvider;

    @Mock
    private CanvasCommandManager canvasCommandManager;

    @Mock
    private Element element;

    @Mock
    private Definition definition;

    private Object objectDefinition = new Object();

    private NameEditBoxWidget presenter;

    @Before
    public void init() {

        when(element.getContent()).thenReturn(definition);
        when(definition.getDefinition()).thenReturn(objectDefinition);
        when(definitionUtils.getName(objectDefinition)).thenReturn(NAME);
        when(definitionUtils.getNameIdentifier(objectDefinition)).thenReturn(ID);
        when(commandProvider.getCommandManager()).thenReturn(canvasCommandManager);

        presenter = new NameEditBoxWidget(definitionUtils,
                                          canvasCommandFactory,
                                          view);

        presenter.setup();

        verify(view).init(presenter);

        presenter.initialize(canvasHandler,
                             closeCallback);

        presenter.setCommandManagerProvider(commandProvider);

        presenter.getElement();

        verify(view).getElement();

        presenter.show(element);

        verify(view).show(NAME);
    }

    @Test
    public void testSaveByPressingEnter() {
        presenter.onKeyPress(12,
                             MODIFIED_NAME);

        verifyNameNotSaved();

        presenter.onKeyPress(13,
                             MODIFIED_NAME);

        verifyNameSaved();
    }

    @Test
    public void testSaveByPressingButton() {
        presenter.onChangeName(MODIFIED_NAME);

        verifyNameNotSaved();

        presenter.onSave();

        verifyNameSaved();
    }

    @Test
    public void testCloseButton() {
        presenter.onChangeName(MODIFIED_NAME);

        assertEquals(MODIFIED_NAME,
                     presenter.getNameValue());

        presenter.onClose();

        assertEquals(null,
                     presenter.getNameValue());

        verify(definitionUtils,
               never()).getNameIdentifier(objectDefinition);
        verify(canvasCommandFactory,
               never()).updatePropertyValue(element,
                                            ID,
                                            MODIFIED_NAME);

        verify(commandProvider,
               never()).getCommandManager();
        verify(canvasCommandManager,
               never()).execute(any(),
                                any());

        verify(view).hide();
        verify(closeCallback).execute();
    }

    protected void verifyNameNotSaved() {
        assertEquals(MODIFIED_NAME,
                     presenter.getNameValue());

        verify(definitionUtils,
               never()).getNameIdentifier(objectDefinition);
        verify(canvasCommandFactory,
               never()).updatePropertyValue(element,
                                            ID,
                                            MODIFIED_NAME);

        verify(commandProvider,
               never()).getCommandManager();
        verify(canvasCommandManager,
               never()).execute(any(),
                                any());

        verify(view,
               never()).hide();
        verify(closeCallback,
               never()).execute();
    }

    protected void verifyNameSaved() {
        assertEquals(MODIFIED_NAME,
                     presenter.getNameValue());

        verify(definitionUtils).getNameIdentifier(objectDefinition);
        verify(canvasCommandFactory).updatePropertyValue(element,
                                                         ID,
                                                         MODIFIED_NAME);

        verify(commandProvider).getCommandManager();
        verify(canvasCommandManager).execute(any(),
                                             any());
        verify(view).hide();
        verify(closeCallback).execute();
    }
}
