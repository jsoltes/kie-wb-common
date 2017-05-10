package org.kie.workbench.common.forms.jbpm.server.service.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.bpmn2.Definitions;
import org.jbpm.simulation.util.BPMN2Utils;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.forms.jbpm.model.authoring.AbstractJBPMFormModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.JBPMVariable;
import org.kie.workbench.common.forms.jbpm.model.authoring.process.BusinessProcessFormModel;
import org.kie.workbench.common.forms.jbpm.model.authoring.task.TaskFormModel;

import static org.junit.Assert.*;

public class BPMNFormModelGeneratorImplTest {

    private final String PROJECT_NAME = "myProject";
    private final String RESOURCES_PATH = "/definitions/";
    private final String BPMN2_SUFFIX = ".bpmn2";
    private final String PROCESS_WITHOUT_VARIABLES_NAME = "process-without-variables";
    private final String PROCESS_WITH_ALL_VARIABLES_NAME = "process-with-all-possible-variables";
    private final String PROCESS_WITH_ALL_VARIABLES_ID = PROJECT_NAME + "." + PROCESS_WITH_ALL_VARIABLES_NAME;

    private final Set<String> JBPM_VARIABLE_NAMES = new HashSet<>(Arrays.asList("string",
                                                                                "integer",
                                                                                "boolean",
                                                                                "float",
                                                                                "object",
                                                                                "dataObject",
                                                                                "customType"));

    private final Set<String> JBPM_VARIABLE_TYPES = new HashSet<>(Arrays.asList(String.class.getName(),
                                                                                Integer.class.getName(),
                                                                                Boolean.class.getName(),
                                                                                Float.class.getName(),
                                                                                String.class.getName(),
                                                                                "com.myteam.myproject.Person",
                                                                                "com.test.MyType"));
    private final Set<String> INPUT_VARIABLE_NAMES = new HashSet<>();
    private final Set<String> OUTPUT_VARIABLE_NAMES = new HashSet<>();
    private final Set<String> INPUT_AND_OUTPUT_VARIABLE_NAMES = new HashSet<>();

    BPMNFormModelGeneratorImpl generator;

    Definitions processWithoutVariablesDefinitions;
    Definitions processWithAllVariablesDefinitions;

    @Before
    public void setUp() throws Exception {
        for (String jbpmVariableName : JBPM_VARIABLE_NAMES) {
            INPUT_VARIABLE_NAMES.add("_" + jbpmVariableName);
        }
        for (String jbpmVariableName : JBPM_VARIABLE_NAMES) {
            OUTPUT_VARIABLE_NAMES.add(jbpmVariableName + "_");
        }
        for (String jbpmVariableName : JBPM_VARIABLE_NAMES) {
            INPUT_AND_OUTPUT_VARIABLE_NAMES.add("_" + jbpmVariableName + "_");
        }
        generator = new BPMNFormModelGeneratorImpl();

        processWithoutVariablesDefinitions = BPMN2Utils.getDefinitions(BPMNFormModelGeneratorImplTest.class.getResourceAsStream(RESOURCES_PATH + PROCESS_WITHOUT_VARIABLES_NAME + BPMN2_SUFFIX));
        processWithAllVariablesDefinitions = BPMN2Utils.getDefinitions(BPMNFormModelGeneratorImplTest.class.getResourceAsStream(RESOURCES_PATH + PROCESS_WITH_ALL_VARIABLES_NAME + BPMN2_SUFFIX));
    }

    @Test
    public void correctProcessFormModelIsGeneratedForProcessWithoutProcessVariables() {
        BusinessProcessFormModel processFormModel = generator.generateProcessFormModel(processWithoutVariablesDefinitions);
        assertProcessFormModelFieldsAreCorrect(processFormModel,
                                               PROCESS_WITHOUT_VARIABLES_NAME);
        assertTrue(processFormModel.getVariables().isEmpty());
    }

    @Test
    public void noTaskFormModelsAreGeneratedForProcessWithoutHumanTasks() {
        List<TaskFormModel> taskFormModels = generator.generateTaskFormModels(processWithoutVariablesDefinitions);
        assertTrue(taskFormModels.isEmpty());
    }

    @Test
    public void generateAllForProcessWithAllPossibleProcessVariables() {
        final int NUMBER_OF_HUMAN_TASKS = 7;
        //generate all = generateProcessFormModel + generateTaskFormModels
        BusinessProcessFormModel processFormModel = generator.generateProcessFormModel(processWithAllVariablesDefinitions);
        assertProcessFormModelFieldsAreCorrect(processFormModel,
                                               PROCESS_WITH_ALL_VARIABLES_NAME);
        assertJBPMVariablesAreCorrect(processFormModel,
                                      JBPM_VARIABLE_NAMES,
                                      JBPM_VARIABLE_TYPES);
        List<TaskFormModel> taskFormModels = generator.generateTaskFormModels(processWithAllVariablesDefinitions);
        assertEquals(NUMBER_OF_HUMAN_TASKS,
                     taskFormModels.size());
    }

    @Test
    public void correctTaskFormModelIsGeneratedForTaskWithoutAnyInputsOrOutputs() {
        final String TASK_ID = "_04F50D91-A11C-42C2-AC63-79A50AC0D862";
        final String TASK_NAME = "emptyTask";

        TaskFormModel taskFormModel = generator.generateTaskFormModel(processWithAllVariablesDefinitions,
                                                                      TASK_ID);
        assertTaskFormModelIsCorrect(taskFormModel,
                                     PROCESS_WITH_ALL_VARIABLES_ID,
                                     TASK_ID,
                                     TASK_NAME);
        assertTrue(taskFormModel.getVariables().isEmpty());
    }

    @Test
    public void correctTaskFormModelIsGeneratedForTaskInTheSubprocess() {
        final String AD_HOC_SUBPROCESS_ID = "_27F1608C-4D2A-4379-B601-33B425E8F560";
        final String TASK_ID = "_AFD1A863-57C6-46EB-A85D-0ADB1E21FA13";
        final String TASK_NAME = "taskWithDifferentInputsAndOutputs";

        TaskFormModel taskFormModel = generator.generateTaskFormModel(processWithAllVariablesDefinitions,
                                                                      TASK_ID);
        assertTaskFormModelIsCorrect(taskFormModel,
                                     AD_HOC_SUBPROCESS_ID,
                                     TASK_ID,
                                     TASK_NAME);

        Set<String> expectedVariableNames = new HashSet<>(INPUT_VARIABLE_NAMES);
        expectedVariableNames.addAll(OUTPUT_VARIABLE_NAMES);

        Set<String> expectedVariableTypes = new HashSet<>(JBPM_VARIABLE_TYPES);
        expectedVariableTypes.addAll(JBPM_VARIABLE_TYPES);

        assertJBPMVariablesAreCorrect(taskFormModel,
                                      expectedVariableNames,
                                      expectedVariableTypes);
    }

    @Test
    public void correctTaskFormModelIsGeneratedForTaskWithTheInputsAndOutputsBoundToTheSameNames() {
        final String TASK_ID = "_F9FD6E46-06AF-41E3-97AA-B27EB2822058";
        final String TASK_NAME = "taskWithTheSameInputsAndOutputs";

        TaskFormModel taskFormModel = generator.generateTaskFormModel(processWithAllVariablesDefinitions,
                                                                      TASK_ID);
        assertTaskFormModelIsCorrect(taskFormModel,
                                     PROCESS_WITH_ALL_VARIABLES_ID,
                                     TASK_ID,
                                     TASK_NAME);

        assertJBPMVariablesAreCorrect(taskFormModel,
                                      INPUT_AND_OUTPUT_VARIABLE_NAMES,
                                      JBPM_VARIABLE_TYPES);
    }

    @Test
    public void correctTaskFormModelIsGeneratedForTaskThatContainsOnlyInputs() {
        final String TASK_ID = "_B8BE7A48-2AE7-4545-A9D3-EA6656C4022C";
        final String TASK_NAME = "taskOnlyWithInputs";

        TaskFormModel taskFormModel = generator.generateTaskFormModel(processWithAllVariablesDefinitions,
                                                                      TASK_ID);
        assertTaskFormModelIsCorrect(taskFormModel,
                                     PROCESS_WITH_ALL_VARIABLES_ID,
                                     TASK_ID,
                                     TASK_NAME);

        assertJBPMVariablesAreCorrect(taskFormModel,
                                      INPUT_VARIABLE_NAMES,
                                      JBPM_VARIABLE_TYPES);
    }

    private void assertProcessFormModelFieldsAreCorrect(BusinessProcessFormModel formModel,
                                                        String PROCESS_NAME) {
        assertEquals(PROJECT_NAME + "." + PROCESS_NAME,
                     formModel.getProcessId());
        assertEquals(PROCESS_NAME,
                     formModel.getProcessName());
    }

    private void assertJBPMVariablesAreCorrect(AbstractJBPMFormModel formModel,
                                               Set<String> expectedVariableNames,
                                               Set<String> expectedVariableTypes) {
        Set<String> actualVariableNames = new HashSet<>();
        Set<String> actualVariableTypes = new HashSet<>();

        for (JBPMVariable variable : formModel.getVariables()) {
            actualVariableNames.add(variable.getName());
            actualVariableTypes.add(variable.getType());
        }

        assertEquals(expectedVariableNames,
                     actualVariableNames);
        assertEquals(expectedVariableTypes,
                     actualVariableTypes);
    }

    private void assertTaskFormModelIsCorrect(TaskFormModel taskFormModel,
                                              String  processId,
                                              String taskId,
                                              String taskName) {
        assertEquals(processId,
                     taskFormModel.getProcessId());
        assertEquals(taskId,
                     taskFormModel.getTaskId());
        assertEquals(taskName,
                     taskFormModel.getTaskName());
//        assertEquals(taskName + BPMNVariableUtils.TASK_FORM_SUFFIX,
//                     taskFormModel.getFormName());
    }
}