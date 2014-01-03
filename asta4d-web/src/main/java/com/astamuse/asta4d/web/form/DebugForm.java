package com.astamuse.asta4d.web.form;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

import com.astamuse.asta4d.data.annotation.ContextData;
import com.astamuse.asta4d.data.annotation.ContextDataSet;
import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.web.WebApplicationConfiguration;
import com.astamuse.asta4d.web.WebApplicationContext;

@ContextDataSet
public class DebugForm extends JsrBeanValidationForm {

    @ContextData
    @NotNull
    @Max(30)
    public SimpleFormField<Integer> age = new SimpleFormField<Integer>(Integer.class) {
        @Override
        public Renderer fieldValueRenderer() {
            // TODO Auto-generated method stub
            return null;
        }
    };

    public static void main(String[] args) {
        WebApplicationContext context = new WebApplicationContext();
        context.init();
        WebApplicationContext.setCurrentThreadContext(context);

        WebApplicationConfiguration conf = new WebApplicationConfiguration();
        WebApplicationConfiguration.setConfiguration(conf);

        DebugForm form = new DebugForm();
        form.age.setValue("", "age-debug", "35");
        System.out.println("validate result:" + form.isValid());
    }

    @Override
    protected List<Field> retrieveValidationFieldList() {
        try {
            return Arrays.asList(this.getClass().getField("age"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
