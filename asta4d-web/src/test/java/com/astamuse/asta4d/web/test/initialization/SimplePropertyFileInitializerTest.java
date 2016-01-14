package com.astamuse.asta4d.web.test.initialization;

import java.io.ByteArrayInputStream;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.astamuse.asta4d.web.WebApplicationConfiguration;
import com.astamuse.asta4d.web.initialization.SimplePropertyFileInitializer;

@Test
public class SimplePropertyFileInitializerTest {

    private static final String PROP_ITEM_1 = "i18nMessageHelper.messagePatternRetriever=com.astamuse.asta4d.web.test.initialization.TestMessagePatternRetriever\n";
    private static final String PROP_ITEM_2 = "i18nMessageHelper.messagePatternRetriever.resourceNames=xmessages\n";

    @DataProvider(name = "prop")
    public Object[][] getTestProp() throws Exception {
        //@formatter:off
        return new Object[][] { 
            {PROP_ITEM_1 + PROP_ITEM_2, new String[]{"xmessages"}}, 
            {PROP_ITEM_2 + PROP_ITEM_1, new String[0]} 
        };
        //@formatter:on
    }

    @Test(dataProvider = "prop")
    public void testItemOrder(String props, String[] expectedValue) throws Exception {
        SimplePropertyFileInitializer initializer = new SimplePropertyFileInitializer();
        WebApplicationConfiguration conf = new WebApplicationConfiguration();
        ByteArrayInputStream input = new ByteArrayInputStream(props.getBytes());
        initializer.initliaze(input, conf);
        TestMessagePatternRetriever retriever = (TestMessagePatternRetriever) conf.getI18nMessageHelper().getMessagePatternRetriever();
        Assert.assertEquals(retriever.getResourceNames(), expectedValue);

    }
}
