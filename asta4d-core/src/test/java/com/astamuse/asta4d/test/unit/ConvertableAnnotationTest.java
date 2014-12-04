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

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.astamuse.asta4d.test.render.infra.BaseTest;
import com.astamuse.asta4d.util.annotation.AnnotationConvertor;
import com.astamuse.asta4d.util.annotation.ConvertableAnnotation;
import com.astamuse.asta4d.util.annotation.ConvertableAnnotationRetriever;

public class ConvertableAnnotationTest extends BaseTest {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public static @interface FinalAnnotationA {
        public int value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @ConvertableAnnotation(ConvertAA.class)
    public static @interface AnnotationAA {
        public String value();
    }

    public static class ConvertAA implements AnnotationConvertor<AnnotationAA, FinalAnnotationA> {

        @Override
        public FinalAnnotationA convert(final AnnotationAA originalAnnotation) {
            return new FinalAnnotationA() {

                @Override
                public Class<? extends Annotation> annotationType() {
                    return FinalAnnotationA.class;
                }

                @Override
                public int value() {
                    return Integer.parseInt(originalAnnotation.value());
                }
            };
        }

    }

    @AnnotationAA("45")
    public static class HA {

    }

    @Test
    public void testOneLayerConvert() {
        FinalAnnotationA fa = ConvertableAnnotationRetriever.retrieveAnnotation(FinalAnnotationA.class, HA.class.getAnnotations());
        Assert.assertEquals(fa.value(), 45);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public static @interface FinalAnnotationB {
        public String value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @ConvertableAnnotation(ConvertBB.class)
    public static @interface AnnotationBB {
        public int value();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @ConvertableAnnotation(ConvertBBB.class)
    public static @interface AnnotationBBB {
        public String value();
    }

    public static class ConvertBBB implements AnnotationConvertor<AnnotationBBB, AnnotationBB> {

        @Override
        public AnnotationBB convert(final AnnotationBBB originalAnnotation) {
            return new AnnotationBB() {

                @Override
                public Class<? extends Annotation> annotationType() {
                    return AnnotationBB.class;
                }

                @Override
                public int value() {
                    return Integer.parseInt(originalAnnotation.value());
                }
            };
        }

    }

    public static class ConvertBB implements AnnotationConvertor<AnnotationBB, FinalAnnotationB> {

        @Override
        public FinalAnnotationB convert(final AnnotationBB originalAnnotation) {
            return new FinalAnnotationB() {

                @Override
                public Class<? extends Annotation> annotationType() {
                    return FinalAnnotationB.class;
                }

                @Override
                public String value() {
                    // TODO Auto-generated method stub
                    return "v-" + originalAnnotation.value();
                }
            };
        }

    }

    @AnnotationBBB("90")
    public static class HB {

    }

    public void testTwoLayerConvert() {
        FinalAnnotationB fb = ConvertableAnnotationRetriever.retrieveAnnotation(FinalAnnotationB.class, HB.class.getAnnotations());
        Assert.assertEquals(fb.value(), "v-90");
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public static @interface FinalAnnotationC {

    }

    public void testNotFound() {
        FinalAnnotationC fc = ConvertableAnnotationRetriever.retrieveAnnotation(FinalAnnotationC.class, HB.class.getAnnotations());
        Assert.assertNull(fc);
    }
}
