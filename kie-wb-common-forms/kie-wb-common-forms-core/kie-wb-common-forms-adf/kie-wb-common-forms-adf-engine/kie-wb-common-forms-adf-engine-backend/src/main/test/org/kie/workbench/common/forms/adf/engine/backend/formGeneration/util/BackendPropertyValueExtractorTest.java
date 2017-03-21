package org.kie.workbench.common.forms.adf.engine.backend.formGeneration.util;

import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class BackendPropertyValueExtractorTest {
    private static final String STRING_PROPERTY = "Cordon Bleu";
    private static final int INT_PROPERTY = 109;
    private static final boolean BOOL_PROPERTY = true;

    private BackendPropertyValueExtractor extractor;
    private MyTestBean bean;

    @Before
    public void setUp() throws Exception {
        extractor = new BackendPropertyValueExtractor();
        bean = new MyTestBean();
    }

    @Test
    public void testWithMyTestBean() {
        bean.setStringProperty(STRING_PROPERTY);
        bean.setIntProperty(INT_PROPERTY);
        bean.setBoolProperty(BOOL_PROPERTY);

        assertEquals(INT_PROPERTY, extractor.readValue(bean, "intProperty"));
        assertEquals(STRING_PROPERTY, extractor.readValue(bean, "stringProperty"));
        assertEquals(BOOL_PROPERTY, extractor.readValue(bean, "boolProperty"));
    }

    @Test
    public void testWithWrongProperty() {
        assertNull(extractor.readValue(bean, "nonExistingProperty"));
    }

    @Test
    public void testWithEmptyModel() {
        assertNull(extractor.readValue(null, null));
    }

    public class MyTestBean implements Serializable {

        private String stringProperty;
        public int intProperty;
        private boolean boolProperty;

        public MyTestBean() {
        }

        public String getStringProperty() {
            return stringProperty;
        }

        public void setStringProperty(String stringProperty) {
            this.stringProperty = stringProperty;
        }

        public int getIntProperty() {
            return intProperty;
        }

        public void setIntProperty(int intProperty) {
            this.intProperty = intProperty;
        }

        public boolean getBoolProperty() {
            return boolProperty;
        }

        public void setBoolProperty(boolean boolProperty) {
            this.boolProperty = boolProperty;
        }
    }

}