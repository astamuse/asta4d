package com.astamuse.asta4d.web.test.unit.form;

import java.lang.reflect.InvocationTargetException;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.astamuse.asta4d.Configuration;
import com.astamuse.asta4d.web.WebApplicationConfiguration;
import com.astamuse.asta4d.web.form.annotation.renderable.Input;
import com.astamuse.asta4d.web.form.flow.ext.ExcludingFieldRetrievableForm;
import com.astamuse.asta4d.web.form.flow.ext.ExcludingFieldRetrievableFormHelper;
import com.astamuse.asta4d.web.form.flow.ext.IncludingFieldRetrievableForm;

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

    public static class ITestForm extends TestForm implements IncludingFieldRetrievableForm {
        @Override
        public String[] getExcludeFields() {
            return IncludingFieldRetrievableForm.super.getExcludeFields();
        }

        @Override
        public String[] getIncludeFields() {
            return new String[] { "name", "xage" };
        }
    }

    @BeforeClass
    public void setConf() {
        WebApplicationConfiguration conf = WebApplicationConfiguration.getWebApplicationConfiguration();
        if (conf == null) {
            conf = new WebApplicationConfiguration();
            conf.setCacheEnable(false);
            Configuration.setConfiguration(conf);
        }
    }

    public void copyIncludeFieldsOnlyTest() throws IllegalAccessException, InvocationTargetException {
        TestForm form1 = new TestForm();
        form1.name = "z-name";
        form1.cc = "z-cc";
        form1.age = "z-age";
        form1.addr = "x-addr";

        TestForm form2 = new TestForm();
        ExcludingFieldRetrievableFormHelper.copyIncludeFieldsOnly(form2, form1);
        Assert.assertEquals(form2.name, form1.name);
        Assert.assertNull(form2.cc);
        Assert.assertEquals(form2.age, form1.age);
        Assert.assertNull(form2.addr);

        ITestForm iform = new ITestForm();
        iform.name = form1.name;
        iform.cc = form1.cc;
        iform.age = form1.age;
        iform.addr = form1.addr;

        form2 = new TestForm();
        ExcludingFieldRetrievableFormHelper.copyIncludeFieldsOnly(form2, iform);
        Assert.assertEquals(form2.name, iform.name);
        Assert.assertNull(form2.cc);
        Assert.assertEquals(form2.age, iform.age);
        Assert.assertNull(form2.addr);

    }

}
