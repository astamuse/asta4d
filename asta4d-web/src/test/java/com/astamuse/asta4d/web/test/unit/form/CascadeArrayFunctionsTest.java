package com.astamuse.asta4d.web.test.unit.form;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.astamuse.asta4d.web.form.CascadeArrayFunctions;

@Test
public class CascadeArrayFunctionsTest {

    @DataProvider(name = "rewriteArrayIndexPlaceHolderTestData")
    public Object[][] rewriteArrayIndexPlaceHolderTestData() {
        //@formatter:off
        return new Object[][] { 
            {"jj-@", new int[]{1}, "jj-1"},
            {"jj-@-@@", new int[]{1,2}, "jj-1-2"},
            {"jj-@-@@", new int[]{1}, "jj-1-@@"},
            {"jj-@-@@", new int[]{}, "jj-@-@@"},
        };
        //@formatter:on
    }

    @Test(dataProvider = "rewriteArrayIndexPlaceHolderTestData")
    public void rewriteArrayIndexPlaceHolderTest(String sourceStr, int[] indexes, String expectedStr) {
        String ret = (new CascadeArrayFunctions() {
        }).rewriteArrayIndexPlaceHolder(sourceStr, indexes);
        Assert.assertEquals(ret, expectedStr);
    }
}
