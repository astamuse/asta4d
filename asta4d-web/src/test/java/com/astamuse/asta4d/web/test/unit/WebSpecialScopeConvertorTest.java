/*
 * Copyright 2014 astamuse company,Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.astamuse.asta4d.web.test.unit;

import java.lang.annotation.Annotation;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.data.InjectUtil;
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

    public static class DataStub {

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

    @BeforeMethod
    public void initContext() {
        Context ctx = new Context();
        Context.setCurrentThreadContext(ctx);
    }

    @AfterMethod
    public void clearContext() {
        Context.setCurrentThreadContext(null);
    }

    @DataProvider(name = "test-data")
    public Object[][] getPathConvertTestData() throws Exception {
        //@formatter:off
        return new Object[][] { 
            {"cookieData", "cook", "cook123", WebApplicationContext.SCOPE_COOKIE, TypeUnMacthPolicy.EXCEPTION},
            {"flashData", "flash", "flash123", WebApplicationContext.SCOPE_FLASH, TypeUnMacthPolicy.EXCEPTION},
            {"headerData", "header", "header123", WebApplicationContext.SCOPE_HEADER, TypeUnMacthPolicy.EXCEPTION},
            {"pathData", "path", "path123", WebApplicationContext.SCOPE_PATHVAR, TypeUnMacthPolicy.EXCEPTION},
            {"queryData", "query", "query123", WebApplicationContext.SCOPE_QUERYPARAM, TypeUnMacthPolicy.EXCEPTION},
            {"sessionData", "session", "session123", WebApplicationContext.SCOPE_SESSION, TypeUnMacthPolicy.EXCEPTION},
            {"sessionData2", "session", "session-123", WebApplicationContext.SCOPE_SESSION, TypeUnMacthPolicy.DEFAULT_VALUE},
        };
        //@formatter:on
    }

    @Test(dataProvider = "test-data")
    public void testConversion(String fieldName, String expectedName, String expectedValue, String expectedScope,
            TypeUnMacthPolicy expectedTypeUnMatch) throws Exception {
        Annotation stub = DataStub.class.getField(fieldName).getAnnotations()[0];
        ContextData cd = WebSpecialScopeConvertor.class.newInstance().convert(stub);
        Assert.assertEquals(cd.name(), expectedName);
        Assert.assertEquals(cd.scope(), expectedScope);
        Assert.assertEquals(cd.typeUnMatch(), expectedTypeUnMatch);

        Context.getCurrentThreadContext().setData(expectedScope, expectedName, expectedValue);
        DataStub stubData = new DataStub();
        InjectUtil.injectToInstance(stubData);
        Assert.assertEquals(DataStub.class.getField(fieldName).get(stubData), expectedValue);
    }
}
