/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.cm.client.wires;

import com.ait.lienzo.client.core.shape.MultiPath;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.WiresContainerShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;

/**
 * Mock implementation of AbstractCaseManagementShape for Unit Tests
 */
public class MockCaseManagementShape extends AbstractCaseManagementShape<WiresContainerShapeView> {

    public MockCaseManagementShape() {
        super(new ViewEventType[]{},
              new MultiPath(),
              0,
              0);
    }

    @Override
    public AbstractCaseManagementShape getGhost() {
        return new MockCaseManagementShape();
    }

    @Override
    public WiresContainerShapeView setSize(double width,
                                           double height) {
        return this;
    }
}
