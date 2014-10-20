package com.astamuse.asta4d.web.test.unit.sitecategory.i18n;

import java.util.Locale;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.astamuse.asta4d.Configuration;
import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.render.RenderUtil;
import com.astamuse.asta4d.web.WebApplicationConfiguration;
import com.astamuse.asta4d.web.WebApplicationContext;
import com.astamuse.asta4d.web.dispatch.DefaultRequestHandlerInvoker;
import com.astamuse.asta4d.web.sitecategory.i18n.ClassLocationBindedMessageResource;
import com.astamuse.asta4d.web.sitecategory.i18n.ContextTraceAwaredMessagePatternRetriever;

class JaExisting implements ClassLocationBindedMessageResource {

}

class JaNotExisting implements ClassLocationBindedMessageResource {

}

@Test
public class ContextTraceAwaredMessagePatternRetrieverTest {

    @BeforeClass
    public void setConf() {
        Locale.setDefault(Locale.ROOT);
        WebApplicationConfiguration conf = new WebApplicationConfiguration();
        conf.setCacheEnable(false);
        Configuration.setConfiguration(conf);
    }

    @BeforeMethod
    public void initContext() {
        Context context = Context.getCurrentThreadContext();
        if (context == null) {
            context = new WebApplicationContext();
            Context.setCurrentThreadContext(context);
        }
        context.init();

    }

    public void testPropForTemplate_jaExisting() {
        Context.getCurrentThreadContext().setData(RenderUtil.TRACE_VAR_TEMPLATE_PATH,
                "/com/astamuse/asta4d/web/test/unit/sitecategory/i18n/JaExisting.html");
        ContextTraceAwaredMessagePatternRetriever retriever = new ContextTraceAwaredMessagePatternRetriever();
        String msg = retriever.retrieve(Locale.JAPAN, "testKey");
        Assert.assertEquals(msg, "かかか");
    }

    public void testPropForTemplate_jaNotExisting() {
        Context.getCurrentThreadContext().setData(RenderUtil.TRACE_VAR_TEMPLATE_PATH,
                "/com/astamuse/asta4d/web/test/unit/sitecategory/i18n/JaNotExisting.html");
        ContextTraceAwaredMessagePatternRetriever retriever = new ContextTraceAwaredMessagePatternRetriever();
        String msg = retriever.retrieve(Locale.JAPAN, "testKey");
        Assert.assertEquals(msg, "kakaka");
    }

    public void testPropForClass_jaExisting() {
        Context.getCurrentThreadContext().setData(DefaultRequestHandlerInvoker.TRACE_VAR_CURRENT_HANDLER, new JaExisting());
        ContextTraceAwaredMessagePatternRetriever retriever = new ContextTraceAwaredMessagePatternRetriever();
        String msg = retriever.retrieve(Locale.JAPAN, "testKey");
        Assert.assertEquals(msg, "かかか");
    }

    public void testPropForClass_jaNotExisting() {
        Context.getCurrentThreadContext().setData(DefaultRequestHandlerInvoker.TRACE_VAR_CURRENT_HANDLER, new JaNotExisting());
        ContextTraceAwaredMessagePatternRetriever retriever = new ContextTraceAwaredMessagePatternRetriever();
        String msg = retriever.retrieve(Locale.JAPAN, "testKey");
        Assert.assertEquals(msg, "kakaka");
    }

}
