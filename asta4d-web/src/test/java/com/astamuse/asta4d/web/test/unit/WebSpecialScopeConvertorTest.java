package com.astamuse.asta4d.web.test.unit;

import java.lang.annotation.Annotation;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.astamuse.asta4d.data.TypeUnMacthPolicy;
import com.astamuse.asta4d.data.annotation.ContextData;
import com.astamuse.asta4d.web.WebApplicationContext;
import com.astamuse.asta4d.web.annotation.CookieData;
import com.astamuse.asta4d.web.annotation.FlashData;
import com.astamuse.asta4d.web.annotation.HeaderData;
import com.astamuse.asta4d.web.annotation.PathVar;
import com.astamuse.asta4d.web.annotation.QueryParam;
import com.astamuse.asta4d.web.annotation.SessionData;
import com.astamuse.asta4d.web.annotation.convertor.WebSpecialScopeConvertor;

public class WebSpecialScopeConvertorTest {

    private static class DataStub {

        @CookieData(name = "cook")
        public String cookieData;

        @FlashData(name = "flash")
        public String flashData;

        @HeaderData(name = "header")
        public String headerData;

        @PathVar(name = "path")
        public String pathData;

        @QueryParam(name = "query")
        public String queryData;

        @SessionData(name = "session")
        public String sessionData;

        @SessionData(name = "session", typeUnMatch = TypeUnMacthPolicy.DEFAULT_VALUE)
        public String sessionData2;
    }

    @DataProvider(name = "test-data")
    public Object[][] getPathConvertTestData() throws Exception {
        //@formatter:off
        return new Object[][] { 
            {"cookieData", "cook", WebApplicationContext.SCOPE_COOKIE, TypeUnMacthPolicy.EXCEPTION},
            {"flashData", "flash", WebApplicationContext.SCOPE_FLASH, TypeUnMacthPolicy.EXCEPTION},
            {"headerData", "header", WebApplicationContext.SCOPE_HEADER, TypeUnMacthPolicy.EXCEPTION},
            {"pathData", "path", WebApplicationContext.SCOPE_PATHVAR, TypeUnMacthPolicy.EXCEPTION},
            {"queryData", "query", WebApplicationContext.SCOPE_QUERYPARAM, TypeUnMacthPolicy.EXCEPTION},
            {"sessionData", "session", WebApplicationContext.SCOPE_SESSION, TypeUnMacthPolicy.EXCEPTION},
            {"sessionData2", "session", WebApplicationContext.SCOPE_SESSION, TypeUnMacthPolicy.DEFAULT_VALUE},
        };
        //@formatter:on
    }

    @Test(dataProvider = "test-data")
    public void testConversion(String fieldName, String expectedName, String expectedScope, TypeUnMacthPolicy expectedTypeUnMatch)
            throws Exception {
        Annotation stub = DataStub.class.getField(fieldName).getAnnotations()[0];
        ContextData cd = WebSpecialScopeConvertor.class.newInstance().convert(stub);
        Assert.assertEquals(cd.name(), expectedName);
        Assert.assertEquals(cd.scope(), expectedScope);
        Assert.assertEquals(cd.typeUnMatch(), expectedTypeUnMatch);
    }
}
