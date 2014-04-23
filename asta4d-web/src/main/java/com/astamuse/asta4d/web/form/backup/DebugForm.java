package com.astamuse.asta4d.web.form.backup;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.tuple.Pair;

import com.astamuse.asta4d.data.annotation.ContextData;
import com.astamuse.asta4d.data.annotation.ContextDataSet;
import com.astamuse.asta4d.util.i18n.ParamMapResourceBundleHelper;
import com.astamuse.asta4d.util.i18n.format.NamedPlaceholderFormatter;
import com.astamuse.asta4d.web.WebApplicationConfiguration;
import com.astamuse.asta4d.web.WebApplicationContext;

@ContextDataSet
public class DebugForm extends JsrBeanValidationForm {

    public final static String TypeMismatchMessageForInt = "{name} should be available integer.";

    @ContextData
    @NotNull
    @Max(30)
    @TypeMismatch(message = TypeMismatchMessageForInt)
    public SimpleFormField<Integer> age = new SimpleFormField<Integer>(Integer.class);

    @ContextData
    @NotNull
    public String email = null;

    public static void main(String[] args) {
        WebApplicationContext context = new WebApplicationContext();
        context.init();
        WebApplicationContext.setCurrentThreadContext(context);

        WebApplicationConfiguration conf = new WebApplicationConfiguration();
        conf.setPlaceholderFormatter(new NamedPlaceholderFormatter());
        WebApplicationConfiguration.setConfiguration(conf);

        DebugForm form = new DebugForm();
        form.age.setData("", "age-debug", "x");
        System.out.println("validate result:" + form.isValid());
    }

    @Override
    public void addMessage(String name, String message) {
        ParamMapResourceBundleHelper helper = new ParamMapResourceBundleHelper();
        String convertedMsg = helper.getMessage(message, Pair.of("name", name));
        super.addMessage(name, convertedMsg);
    }

}
