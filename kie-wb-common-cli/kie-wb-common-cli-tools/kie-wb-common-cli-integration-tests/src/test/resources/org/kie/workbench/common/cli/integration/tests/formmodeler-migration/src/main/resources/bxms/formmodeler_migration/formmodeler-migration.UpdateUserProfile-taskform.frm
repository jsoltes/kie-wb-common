{"id":"62088553","name":"formmodeler-migration.UpdateUserProfile-taskform","model":{"processName":"UpdateUserProfile","processId":"formmodeler-migration.UpdateUserProfile","properties":[{"name":"boolean","typeInfo":{"type":"BASE","className":"java.lang.Boolean","multiple":false},"metaData":{"entries":[]}},{"name":"cv","typeInfo":{"type":"BASE","className":"org.jbpm.document.Document","multiple":false},"metaData":{"entries":[]}},{"name":"float","typeInfo":{"type":"BASE","className":"java.lang.Float","multiple":false},"metaData":{"entries":[]}},{"name":"integer","typeInfo":{"type":"BASE","className":"java.lang.Integer","multiple":false},"metaData":{"entries":[]}},{"name":"string","typeInfo":{"type":"BASE","className":"java.lang.String","multiple":false},"metaData":{"entries":[]}},{"name":"person","typeInfo":{"type":"OBJECT","className":"bxms.formmodeler_migration.Person","multiple":false},"metaData":{"entries":[]}}],"formModelType":"org.kie.workbench.common.forms.jbpm.model.authoring.process.BusinessProcessFormModel"},"fields":[{"id":"465916016","name":"boolean","label":"boolean (boolean)","required":false,"readOnly":false,"validateOnChange":true,"helpMessage":"","binding":"boolean","standaloneClassName":"java.lang.Boolean","code":"CheckBox","serializedFieldClassName":"org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.checkBox.definition.CheckBoxFieldDefinition"},{"id":"1711137757","name":"cv","label":"cv (cv)","required":false,"readOnly":false,"validateOnChange":true,"helpMessage":"","binding":"cv","standaloneClassName":"org.jbpm.document.Document","code":"Document","serializedFieldClassName":"org.kie.workbench.common.forms.jbpm.model.authoring.document.definition.DocumentFieldDefinition"},{"placeHolder":"","maxLength":100,"id":"926972869","name":"float","label":"float (float)","required":false,"readOnly":false,"validateOnChange":true,"helpMessage":"","binding":"float","standaloneClassName":"java.lang.Float","code":"DecimalBox","serializedFieldClassName":"org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.decimalBox.definition.DecimalBoxFieldDefinition"},{"placeHolder":"","maxLength":100,"id":"466943341","name":"integer","label":"integer (integer)","required":false,"readOnly":false,"validateOnChange":true,"helpMessage":"","binding":"integer","standaloneClassName":"java.lang.Integer","code":"IntegerBox","serializedFieldClassName":"org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.integerBox.definition.IntegerBoxFieldDefinition"},{"maxLength":100,"placeHolder":"","id":"1489171466","name":"string","label":"string (string)","required":false,"readOnly":false,"validateOnChange":true,"helpMessage":"","binding":"string","standaloneClassName":"java.lang.String","code":"TextBox","serializedFieldClassName":"org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.textBox.definition.TextBoxFieldDefinition"},{"nestedForm":"10180b91-0fe4-44b0-912a-d957e958a991","container":"FIELD_SET","id":"1524500059293","name":"person","label":"person","validateOnChange":true,"helpMessage":"","binding":"person","standaloneClassName":"bxms.formmodeler_migration.Person","code":"SubForm","serializedFieldClassName":"org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.subForm.definition.SubFormFieldDefinition"}],"layoutTemplate":{"version":2,"style":"FLUID","layoutProperties":{},"rows":[{"properties":{},"layoutColumns":[{"span":"12","height":"12","properties":{},"rows":[],"layoutComponents":[{"dragTypeName":"org.kie.workbench.common.forms.editor.client.editor.rendering.EditorFieldLayoutComponent","properties":{"field_id":"465916016","form_id":"62088553"}}]}]},{"properties":{},"layoutColumns":[{"span":"12","height":"12","properties":{},"rows":[],"layoutComponents":[{"dragTypeName":"org.kie.workbench.common.forms.editor.client.editor.rendering.EditorFieldLayoutComponent","properties":{"field_id":"1711137757","form_id":"62088553"}}]}]},{"properties":{},"layoutColumns":[{"span":"12","height":"12","properties":{},"rows":[],"layoutComponents":[{"dragTypeName":"org.kie.workbench.common.forms.editor.client.editor.rendering.EditorFieldLayoutComponent","properties":{"field_id":"926972869","form_id":"62088553"}}]}]},{"properties":{},"layoutColumns":[{"span":"12","height":"12","properties":{},"rows":[],"layoutComponents":[{"dragTypeName":"org.kie.workbench.common.forms.editor.client.editor.rendering.EditorFieldLayoutComponent","properties":{"field_id":"466943341","form_id":"62088553"}}]}]},{"properties":{},"layoutColumns":[{"span":"12","height":"12","properties":{},"rows":[],"layoutComponents":[{"dragTypeName":"org.kie.workbench.common.forms.editor.client.editor.rendering.EditorFieldLayoutComponent","properties":{"field_id":"1489171466","form_id":"62088553"}}]}]},{"properties":{},"layoutColumns":[{"span":"12","height":"12","properties":{},"rows":[],"layoutComponents":[{"dragTypeName":"org.kie.workbench.common.forms.editor.client.editor.rendering.EditorFieldLayoutComponent","properties":{"field_id":"1524500059293","form_id":"62088553"}}]}]}]}}