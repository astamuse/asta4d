package com.astamuse.asta4d.web.test.unit.form;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.astamuse.asta4d.web.form.annotation.renderable.Input;
import com.astamuse.asta4d.web.form.flow.ext.ExcludingFieldRetrievableForm;
import com.astamuse.asta4d.web.form.flow.ext.ExcludingFieldRetrievableFormHelper;

@Test
public class ExcludingFieldRetrievableFormHelperTest {

    public static class TestForm implements ExcludingFieldRetrievableForm {

        @Input
        public String name;

        @Input
        public String cc;

        @Input(name = "xage")
        public String age;

        @Input(name = "xaddr")
        public String addr;

        @Override
        public String[] getExcludeFields() {
            return new String[] { "cc", "xaddr" };
        }
    }

    public void copyIncludeFieldsOnlyTest() {
        TestForm form1 = new TestForm();
        form1.name = "z-name";
        form1.cc = "z-cc";
        form1.age = "z-age";
        form1.name = "z-name";

        TestForm form2 = new TestForm();
        ExcludingFieldRetrievableFormHelper.copyIncludeFieldsOnly(form2, form1);
        Assert.assertEquals(form2.name, form1.name);
        Assert.assertNull(form2.cc);
        Assert.assertEquals(form2.age, form1.age);
        Assert.assertNull(form2.addr);
    }

}
