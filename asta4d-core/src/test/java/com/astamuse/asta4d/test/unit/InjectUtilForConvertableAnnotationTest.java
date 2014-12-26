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

import static org.testng.Assert.assertEquals;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import org.testng.annotations.Test;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.data.ContextDataHolder;
import com.astamuse.asta4d.data.ContextDataSetFactory;
import com.astamuse.asta4d.data.DefaultContextDataSetFactory;
import com.astamuse.asta4d.data.InjectUtil;
import com.astamuse.asta4d.data.TypeUnMacthPolicy;
import com.astamuse.asta4d.data.annotation.ContextData;
import com.astamuse.asta4d.data.annotation.ContextDataSet;
import com.astamuse.asta4d.test.render.infra.BaseTest;
import com.astamuse.asta4d.util.annotation.AnnotationConvertor;
import com.astamuse.asta4d.util.annotation.ConvertableAnnotation;

public class InjectUtilForConvertableAnnotationTest extends BaseTest {

    private final static String MyDataScope = "MyDataScope";

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @ConvertableAnnotation(MySetConvertor.class)
    public static @interface MySet {
    }

    public static class MySetConvertor implements AnnotationConvertor<MySet, ContextDataSet> {

        @Override
        public ContextDataSet convert(MySet originalAnnotation) {
            return new ContextDataSet() {

                @Override
                public Class<? extends Annotation> annotationType() {
                    return ContextDataSet.class;
                }

                @Override
                public Class<? extends ContextDataSetFactory> factory() {
                    return DefaultContextDataSetFactory.class;
                }

                @Override
                public boolean singletonInContext() {
                    return false;
                }
            };
        }

    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
    @ConvertableAnnotation(MyDataConvertor.class)
    public static @interface MyData {
        public String name() default "";
    }

    public static class MyDataConvertor implements AnnotationConvertor<MyData, ContextData> {

        @Override
        public ContextData convert(final MyData originalAnnotation) {
            return new ContextData() {

                @Override
                public Class<? extends Annotation> annotationType() {
                    return ContextData.class;
                }

                @Override
                public String name() {
                    return originalAnnotation.name();
                }

                @Override
                public String scope() {
                    return MyDataScope;
                }

                @Override
                public TypeUnMacthPolicy typeUnMatch() {
                    return TypeUnMacthPolicy.DEFAULT_VALUE;
                }
            };
        }

    }

    @MySet
    public static class TestSet {
        @MyData
        private long f1;
        @ContextData
        public ContextDataHolder<Long> f2 = new ContextDataHolder<>(Long.class);

        public long getF1() {
            return f1;
        }
    }

    public static class TestCls {
        @ContextData
        private TestSet myset;
    }

    @Test
    public void contextSetAndInjectableOnMethod() throws Exception {
        Context ctx = Context.getCurrentThreadContext();
        ctx.setData(MyDataScope, "f1", "6678");
        ctx.setData("f2", "12345");
        Object[] params = InjectUtil.getMethodInjectParams(getMethod("requireHolder", TestSet.class));
        TestSet set = (TestSet) params[0];
        assertEquals(set.getF1(), 6678L);
        assertEquals(set.f2.getValue().longValue(), 12345L);

    }

    @Test
    public void contextSetAndInjectableOnInstance() throws Exception {
        Context ctx = Context.getCurrentThreadContext();
        ctx.setData(MyDataScope, "f1", "6678");
        ctx.setData("f2", "12345");

        TestCls tc = new TestCls();
        InjectUtil.injectToInstance(tc);
        TestSet set = tc.myset;
        assertEquals(set.getF1(), 6678L);
        assertEquals(set.f2.getValue().longValue(), 12345L);
    }

    @Test(enabled = false)
    public void requireHolder(TestSet holder) {
    }

    private static Method getMethod(String name, Class<?>... parameterTypes) throws NoSuchMethodException {
        return InjectUtilForConvertableAnnotationTest.class.getMethod(name, parameterTypes);
    }
}
