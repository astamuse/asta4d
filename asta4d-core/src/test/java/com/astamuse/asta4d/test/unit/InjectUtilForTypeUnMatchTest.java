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
package com.astamuse.asta4d.test.unit;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.data.ContextDataHolder;
import com.astamuse.asta4d.data.DataOperationException;
import com.astamuse.asta4d.data.InjectTrace;
import com.astamuse.asta4d.data.InjectUtil;
import com.astamuse.asta4d.data.TypeUnMacthPolicy;
import com.astamuse.asta4d.data.annotation.ContextData;
import com.astamuse.asta4d.data.annotation.ContextDataSet;
import com.astamuse.asta4d.test.render.infra.BaseTest;

public class InjectUtilForTypeUnMatchTest extends BaseTest {

    @ContextDataSet
    public static class TestSetForException {
        @ContextData(typeUnMatch = TypeUnMacthPolicy.EXCEPTION)
        private long f1 = -1;

        public long getF1() {
            return f1;
        }
    }

    @Test(expectedExceptions = DataOperationException.class, expectedExceptionsMessageRegExp = ".*cannot be coverted from.*")
    public void exceptionWhenInjectableOnMethodSetParam() throws Exception {
        Context ctx = Context.getCurrentThreadContext();
        ctx.setData("f1", "xx77");

        Object[] params = InjectUtil.getMethodInjectParams(getMethod("requestHolderForException", TestSetForException.class));

    }

    @Test(expectedExceptions = DataOperationException.class, expectedExceptionsMessageRegExp = ".*cannot be coverted from.*")
    public void exceptionWhenInjectableOnMethodParam() throws Exception {
        Context ctx = Context.getCurrentThreadContext();
        ctx.setData("holder", "xx77");

        Object[] params = InjectUtil.getMethodInjectParams(getMethod("requestHolderForException", int.class));

    }

    @Test(expectedExceptions = DataOperationException.class, expectedExceptionsMessageRegExp = ".*cannot be coverted from.*")
    public void exceptionWhenInjectableOnMethodParam2() throws Exception {
        Context ctx = Context.getCurrentThreadContext();
        ctx.setData("holder", "xx77");

        Object[] params = InjectUtil.getMethodInjectParams(getMethod("requestHolderForException2", int.class));

    }

    @Test(expectedExceptions = DataOperationException.class, expectedExceptionsMessageRegExp = ".*cannot be coverted from.*")
    public void exceptionWhenInjectableOnMethodParam3() throws Exception {
        Context ctx = Context.getCurrentThreadContext();
        ctx.setData("holder", "xx77");

        Object[] params = InjectUtil.getMethodInjectParams(getMethod("requestHolderForException3", int.class));

    }

    @Test(expectedExceptions = DataOperationException.class, expectedExceptionsMessageRegExp = ".*cannot be coverted from.*")
    public void exceptionWhenInjectArrayDataOnMethodSetParam() throws Exception {
        Context ctx = Context.getCurrentThreadContext();
        ctx.setData("f1", new String[] { "xx77" });

        Object[] params = InjectUtil.getMethodInjectParams(getMethod("requestHolderForException", TestSetForException.class));

    }

    @Test(enabled = false)
    public void requestHolderForException(TestSetForException holder) {
    }

    @Test(enabled = false)
    public void requestHolderForException(@ContextData(typeUnMatch = TypeUnMacthPolicy.EXCEPTION) int holder) {
    }

    @Test(enabled = false)
    public void requestHolderForException2(@ContextData int holder) {
    }

    @Test(enabled = false)
    public void requestHolderForException3(int holder) {
    }

    @ContextDataSet
    public static class TestSetForDefaultValue {
        @ContextData(typeUnMatch = TypeUnMacthPolicy.DEFAULT_VALUE)
        private long f1 = -1;

        @ContextData(typeUnMatch = TypeUnMacthPolicy.DEFAULT_VALUE_AND_TRACE)
        private long f2 = -1;

        @ContextData(typeUnMatch = TypeUnMacthPolicy.DEFAULT_VALUE_AND_TRACE)
        private Date f3 = new Date(0);

        public long getF1() {
            return f1;
        }

        public long getF2() {
            return f2;
        }

        public Date getF3() {
            return f3;
        }
    }

    @Test
    public void defaultValueWhenInjectableOnMethodSetParam() throws Exception {
        Context ctx = Context.getCurrentThreadContext();
        ctx.setData("f1", "xx77");
        ctx.setData("f2", "xx66");
        ctx.setData("f3", "xx55");

        Object[] params = InjectUtil.getMethodInjectParams(getMethod("requestHolderForDefaultValue", TestSetForDefaultValue.class));
        TestSetForDefaultValue set = (TestSetForDefaultValue) params[0];

        Assert.assertEquals(set.getF1(), 0);
        Assert.assertEquals(set.getF2(), 0);
        Assert.assertNull(set.getF3());

        ContextDataHolder traceHolder;

        Field field1 = TestSetForDefaultValue.class.getDeclaredField("f1");
        traceHolder = InjectTrace.getInstanceInjectionTraceInfo(set, field1);
        Assert.assertNull(traceHolder);

        Field field2 = TestSetForDefaultValue.class.getDeclaredField("f2");
        traceHolder = InjectTrace.getInstanceInjectionTraceInfo(set, field2);
        Assert.assertEquals(traceHolder.getFoundOriginalData(), "xx66");

        Field field3 = TestSetForDefaultValue.class.getDeclaredField("f3");
        traceHolder = InjectTrace.getInstanceInjectionTraceInfo(set, field3);
        Assert.assertEquals(traceHolder.getFoundOriginalData(), "xx55");
    }

    @Test
    public void defaultValueWhenInjectableOnMethodParam() throws Exception {
        Context ctx = Context.getCurrentThreadContext();
        ctx.setData("f1", "xx77");
        ctx.setData("f2", "xx66");

        Method m = getMethod("requestHolderForDefaultValue", int.class, int.class);

        Object[] params = InjectUtil.getMethodInjectParams(m);

        Assert.assertEquals(params[0], 0);
        Assert.assertEquals(params[1], 0);

        ContextDataHolder traceHolder;

        traceHolder = InjectTrace.getMethodInjectionTraceInfo(m, 0);
        Assert.assertNull(traceHolder);

        traceHolder = InjectTrace.getMethodInjectionTraceInfo(m, 1);
        Assert.assertEquals(traceHolder.getFoundOriginalData(), "xx66");

    }

    @Test(enabled = false)
    public void requestHolderForDefaultValue(TestSetForDefaultValue holder) {
    }

    @Test(enabled = false)
    public void requestHolderForDefaultValue(@ContextData(typeUnMatch = TypeUnMacthPolicy.DEFAULT_VALUE) int f1,
            @ContextData(typeUnMatch = TypeUnMacthPolicy.DEFAULT_VALUE_AND_TRACE) int f2) {
    }

    private static Method getMethod(String name, Class<?>... parameterTypes) throws NoSuchMethodException {
        return InjectUtilForTypeUnMatchTest.class.getMethod(name, parameterTypes);
    }
}
