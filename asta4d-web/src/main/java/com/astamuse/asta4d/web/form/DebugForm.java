package com.astamuse.asta4d.web.form;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

import com.astamuse.asta4d.data.annotation.ContextData;
import com.astamuse.asta4d.data.annotation.ContextDataSet;
import com.astamuse.asta4d.web.WebApplicationConfiguration;
import com.astamuse.asta4d.web.WebApplicationContext;

@ContextDataSet
public class DebugForm extends JsrBeanValidationForm {

    @ContextData
    @NotNull
    @Max(30)
    public SimpleFormField<Integer> age = new SimpleFormField<Integer>(Integer.class);

    @ContextData
    @NotNull
    public String email = null;

    public static void main(String[] args) {
        WebApplicationContext context = new WebApplicationContext();
        context.init();
        WebApplicationContext.setCurrentThreadContext(context);

        WebApplicationConfiguration conf = new WebApplicationConfiguration();
        WebApplicationConfiguration.setConfiguration(conf);

        DebugForm form = new DebugForm();
        form.age.setValue("", "age-debug", "x");
        System.out.println("validate result:" + form.isValid());
    }

}
