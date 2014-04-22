/*
 * Copyright 2012 astamuse company,Ltd.
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

import java.util.LinkedList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.astamuse.asta4d.data.DataConvertor;
import com.astamuse.asta4d.data.DataConvertorInvoker;
import com.astamuse.asta4d.data.DataOperationException;
import com.astamuse.asta4d.test.render.infra.BaseTest;

public class DataConvertorInvokerTest extends BaseTest {

    public static class PA {
        String value;

        PA(String v) {
            this.value = v + "-PA";
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            PA other = (PA) obj;
            if (value == null) {
                if (other.value != null)
                    return false;
            } else if (!value.equals(other.value))
                return false;
            return true;
        }

    }

    public static class PB extends PA {

        String otherValue;

        PB(String v) {
            super(v);
            otherValue = v + "-PB";
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (!super.equals(obj))
                return false;
            PB other = (PB) obj;
            if (otherValue == null) {
                if (other.otherValue != null)
                    return false;
            } else if (!otherValue.equals(other.otherValue))
                return false;
            return true;
        }

    }

    public static class String2PB implements DataConvertor<String, PB> {
        @Override
        public PB convert(String obj) {
            PB ret = new PB(obj);
            return ret;
        }

    }

    public static class PA2String implements DataConvertor<PA, String> {
        @Override
        public String convert(PA obj) {
            return obj.value.substring(0, obj.value.indexOf("-PA"));
        }

    }

    public static class String2LongArray implements DataConvertor<String, Long[]> {
        @Override
        public Long[] convert(String obj) {
            String[] array = obj.split(",");
            Long[] ret = new Long[array.length];
            for (int i = 0; i < array.length; i++) {
                ret[i] = Long.parseLong(array[i]);
            }
            return ret;
        }
    }

    public static class StringArray2Long implements DataConvertor<String[], Long> {
        @Override
        public Long convert(String[] obj) {
            if (obj.length == 1) {
                return Long.parseLong(obj[0]);
            } else {
                return Long.parseLong(obj[1]);
            }
        }
    }

    private DataConvertorInvoker invoker;

    @BeforeClass
    public void prepareInvoker() {
        invoker = new DataConvertorInvoker();
        List<DataConvertor> list = new LinkedList<DataConvertor>();
        list.add(new String2PB());
        list.add(new PA2String());
        list.add(new String2LongArray());
        list.add(new StringArray2Long());
        invoker.setDataConvertorList(list);
    }

    @DataProvider(name = "test-data")
    public Object[][] getTestData() throws Exception {
        //@formatter:off
        return new Object[][] { 
                {String[].class, Integer[][].class, new String[]{"123", "456"}, null},
            //basic cases
            {String.class, Integer.class, "123", 123},
            {String.class, Integer[].class, "123", new Integer[]{123}},
            {String[].class, Integer[].class, new String[]{"123", "456"}, new Integer[]{123, 456}},
            {String[].class, Integer.class, new String[]{"123", "456"}, 123},
            //explicit array conversion
            {String.class, Long[].class, "123,456", new Long[]{123L, 456L}},
            {String[].class, Long.class, new String[]{"123","456"}, 456L},
            //recursive array conversion
            {String.class, Integer[][].class, "123", null},//don't support
            {String[].class, Integer[][].class, new String[]{"123", "456"}, null},//don't support
            {String.class, Long[][].class, "123,456", new Long[][]{{123L, 456L}}},
            {String[][].class, Integer.class, new String[][]{{"123","456"}}, 123},
            {String[][][].class, Integer.class, new String[][][]{{{"123","456"}}}, 123},
            //compatible type
            {String.class, PA.class, "123", new PA("123")},
            {PB.class, String.class, new PB("123"), "123"},
        };
        //@formatter:on
    }

    @Test(dataProvider = "test-data")
    public void testBasicConversion(Class srcType, Class targetType, Object data, Object expectedData) throws DataOperationException {
        Object ret = invoker.convertData(srcType, targetType, data);
        Assert.assertEquals(ret, expectedData);
    }
}