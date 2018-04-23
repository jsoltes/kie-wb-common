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

package org.kie.workbench.common.cli.integration.tests;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.forms.data.modeller.model.DataObjectFormModel;
import org.kie.workbench.common.forms.fields.test.TestMetaDataEntryManager;
import org.kie.workbench.common.forms.jbpm.model.authoring.process.BusinessProcessFormModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.task.TaskFormModel;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.services.backend.serialization.FormDefinitionSerializer;
import org.kie.workbench.common.forms.services.backend.serialization.impl.FieldSerializer;
import org.kie.workbench.common.forms.services.backend.serialization.impl.FormDefinitionSerializerImpl;
import org.kie.workbench.common.forms.services.backend.serialization.impl.FormModelSerializer;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class FormMigrationIntegrationTest {

    private static final String FORMS_FOLDER = "formmodeler-migration/src/main/resources/bxms/formmodeler_migration/";

    private static final Map<String, List<String>> expectedAddressFields = new HashMap<String, List<String>>() {{
        put("person_address",
            Arrays.asList("address (person)",
                          "SubForm",
                          "address"));
        put("person_addressList",
            Arrays.asList("addressList (person)",
                          "MultipleSubForm",
                          "addressList"));
        put("person_age",
            Arrays.asList("age (person)",
                          "IntegerBox",
                          "age"));
        put("person_birthdate",
            Arrays.asList("birthdate (person)",
                          "DatePicker",
                          "birthdate"));
        put("person_married",
            Arrays.asList("married (person)",
                          "CheckBox",
                          "married"));
        put("person_name",
            Arrays.asList("name (person)",
                          "TextBox",
                          "name"));
        put("person_salary",
            Arrays.asList("salary (person)",
                          "DecimalBox",
                          "salary"));
        put("person_sex",
            Arrays.asList("sex (person)",
                          "TextBox",
                          "sex"));
    }};

    private FormDefinitionSerializer formSerializer;

    @Before
    public void init() {
        formSerializer = new FormDefinitionSerializerImpl(new FieldSerializer(),
                                                          new FormModelSerializer(),
                                                          new TestMetaDataEntryManager());
    }

    @Test
    public void testPersonForm() throws IOException {
        FormDefinition personForm = getFormFromResources("PersonForm");
        testLayout(personForm,
                   4,
                   2);
        testFields(personForm,
                   expectedAddressFields);
        testHelpMessage(personForm.getFieldByName("person_salary"),
                        "Please enter your salary in dollars.");
        testRequired(personForm.getFieldByName("person_birthdate"),
                     true);
        testReadOnly(personForm.getFieldByName("person_married"),
                     true);

        testDataObjectModel(personForm,
                            "bxms.formmodeler_migration.Person");
    }

    @Test
    public void testHelperSubforms() throws IOException {
        testAllHelperFormsWereGenerated(Arrays.asList("formmodeler-migration.UpdateUserProfile-taskform-person.frm",
                                                      "taskWithDifferentIO-taskform-person.frm",
                                                      "taskWithSameIO-taskform-person.frm"));
        testHelperFormContainsCorrectFields("taskWithSameIO-taskform-person",
                                            Arrays.asList("person_address",
                                                          "person_addressList",
                                                          "person_birthdate",
                                                          "person_married",
                                                          "person_salary",
                                                          "person_sex"));
    }

    @Test
    public void testProcessForm() throws IOException {
        FormDefinition processForm = getFormFromResources("formmodeler-migration.UpdateUserProfile-taskform");
        testFields(processForm,
                   new HashMap<String, List<String>>() {{
                       put("boolean",
                           Arrays.asList("boolean (boolean)",
                                         "CheckBox",
                                         "boolean"));
                       put("cv",
                           Arrays.asList("cv (cv)",
                                         "Document",
                                         "cv"));
                       put("float",
                           Arrays.asList("float (float)",
                                         "DecimalBox",
                                         "float"));
                       put("integer",
                           Arrays.asList("integer (integer)",
                                         "IntegerBox",
                                         "integer"));
                       put("string",
                           Arrays.asList("string (string)",
                                         "TextBox",
                                         "string"));
                       put("person",
                           Arrays.asList("person",
                                         "SubForm",
                                         "person"));
                   }});
        testProcessModel(processForm,
                         "UpdateUserProfile");
    }

    @Test
    public void testTaskForm() throws IOException {
        FormDefinition taskForm = getFormFromResources("taskWithDifferentIO-taskform");
        testFields(taskForm,
                   new HashMap<String, List<String>>() {{
                       put("boolean",
                           Arrays.asList("_boolean (boolean)",
                                         "CheckBox",
                                         "_boolean"));
                       put("cv",
                           Arrays.asList("_cv (cv)",
                                         "Document",
                                         "_cv"));
                       put("float",
                           Arrays.asList("_float (float)",
                                         "DecimalBox",
                                         "_float"));
                       put("integer",
                           Arrays.asList("_integer (integer)",
                                         "IntegerBox",
                                         "_integer"));
                       put("string",
                           Arrays.asList("_string (string)",
                                         "TextBox",
                                         "_string"));
                       put("person",
                           Arrays.asList("person",
                                         "SubForm",
                                         "_person"));
                   }});
        testTaskModel(taskForm,
                      "taskWithDifferentIO");
    }

    private FormDefinition getFormFromResources(String formName) throws IOException {
        File form = new File(this.getClass().getResource(FORMS_FOLDER + formName + ".frm").getFile());
        return formSerializer.deserialize(FileUtils.readFileToString(form,
                                                                     Charset.defaultCharset()));
    }

    private void testFields(FormDefinition form,
                            Map<String, List<String>> expectedFields) {
        Map<String, List<String>> actualFields = form.getFields().stream()
                .collect(Collectors.toMap(FieldDefinition::getName,
                                          f -> Arrays.asList(f.getLabel(),
                                                             f.getFieldType().getTypeName(),
                                                             f.getBinding())));
        assertThat(actualFields).isEqualTo(expectedFields);
    }

    private void testLayout(FormDefinition form,
                            int expectedRows,
                            int expectedColumns) {
        LayoutTemplate layout = form.getLayoutTemplate();
        int actualRows = layout.getRows().size();
        int actualColumns = layout.getRows().get(0).getLayoutColumns().size();
        assertThat(actualRows).isEqualTo(expectedRows);
        assertThat(actualColumns).isEqualTo(expectedColumns);
    }

    private void testHelpMessage(FieldDefinition field,
                                 String expectedMessage) {
        assertThat(field.getHelpMessage()).isEqualTo(expectedMessage);
    }

    private void testRequired(FieldDefinition field,
                              boolean expectedValue) {
        assertThat(field.getRequired()).isEqualTo(expectedValue);
    }

    private void testReadOnly(FieldDefinition field,
                              boolean expectedValue) {
        assertThat(field.getReadOnly()).isEqualTo(expectedValue);
    }

    private void testDataObjectModel(FormDefinition form,
                                     String expectedModel) {
        assertThat(((DataObjectFormModel) form.getModel()).getType()).isEqualTo(expectedModel);
    }

    private void testProcessModel(FormDefinition form,
                                  String expectedModel) {
        assertThat(((BusinessProcessFormModel) form.getModel()).getProcessName()).isEqualTo(expectedModel);
    }

    private void testTaskModel(FormDefinition form,
                               String expectedModel) {
        assertThat(((TaskFormModel) form.getModel()).getTaskName()).isEqualTo(expectedModel);
    }

    private void testAllHelperFormsWereGenerated(List<String> expectedForms) {
        File formFolder = new File(this.getClass().getResource(FORMS_FOLDER).getFile());
        File[] files = formFolder.listFiles();
        assert files != null;
        List<String> actualForms = Arrays.stream(files).map(File::getName).collect(Collectors.toList());
        assertThat(actualForms).containsAll(expectedForms);
    }

    private void testHelperFormContainsCorrectFields(String form,
                                                     List<String> expectedFields) throws IOException {
        List<String> actualFields = getFormFromResources(form).getFields().stream().map(FieldDefinition::getName).collect(Collectors.toList());
        assertThat(actualFields).isEqualTo(expectedFields);
    }
}
