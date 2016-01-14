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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.data.InjectUtil;
import com.astamuse.asta4d.test.render.infra.BaseTest;

public class InjectUtilForMethodTest extends BaseTest {

    @Test
    public void stringNotFound() throws Exception {
        setData(null);
        Object[] params = InjectUtil.getMethodInjectParams(getMethod("requireString", String.class));
        assertEquals(params.length, 1);
        assertEquals(params[0], null);
    }

    @Test
    public void string2string() throws Exception {
        setData("value");
        Object[] params = InjectUtil.getMethodInjectParams(getMethod("requireString", String.class));
        assertEquals(params.length, 1);
        assertEquals(params[0], "value");
    }

    @Test
    public void string2StringArray() throws Exception {
        setData(new String[] { "aa", "bb" });
        Object[] params = InjectUtil.getMethodInjectParams(getMethod("requireString", String.class));
        assertEquals(params.length, 1);
        assertEquals(params[0], "aa");
    }

    @Test
    public void stringArray2String() throws Exception {
        setData("value");
        Object[] params = InjectUtil.getMethodInjectParams(getMethod("requireString", String[].class));
        assertEquals(params.length, 1);
        assertEquals(params[0], new String[] { "value" });
    }

    @Test
    public void stringArray2StringArray() throws Exception {
        setData(new String[] { "aa", "bb" });
        Object[] params = InjectUtil.getMethodInjectParams(getMethod("requireString", String[].class));
        assertEquals(params.length, 1);
        assertEquals(params[0], new String[] { "aa", "bb" });
    }

    @Test
    public void boolean2boolean() throws Exception {
        setData(true);
        Object[] params = InjectUtil.getMethodInjectParams(getMethod("requireBoolean", boolean.class));
        assertEquals(params.length, 1);
        assertTrue((Boolean) params[0]);
    }

    @Test
    public void boolean2booleanArray() throws Exception {
        setData(true);
        Object[] params = InjectUtil.getMethodInjectParams(getMethod("requireBoolean", boolean[].class));
        assertEquals(params.length, 1);
        assertEquals(params[0], new boolean[] { true });
    }

    @Test
    public void booleanArray2boolean() throws Exception {
        setData(new boolean[] { true, false });
        Object[] params = InjectUtil.getMethodInjectParams(getMethod("requireBoolean", boolean.class));
        assertEquals(params.length, 1);
        assertTrue((Boolean) params[0]);
    }

    @Test
    public void booleanArray2booleanArray() throws Exception {
        setData(new boolean[] { true, false });
        Object[] params = InjectUtil.getMethodInjectParams(getMethod("requireBoolean", boolean[].class));
        assertEquals(params.length, 1);
        assertEquals(params[0], new boolean[] { true, false });
    }

    @Test
    public void string2boolean() throws Exception {
        setData("true");
        Object[] params = InjectUtil.getMethodInjectParams(getMethod("requireBoolean", boolean.class));
        assertEquals(params.length, 1);
        assertTrue((Boolean) params[0]);
    }

    @Test
    public void string2booleanArray() throws Exception {
        setData("true");
        Object[] params = InjectUtil.getMethodInjectParams(getMethod("requireBoolean", boolean.class));
        assertEquals(params.length, 1);
        assertTrue((Boolean) params[0]);
    }

    @Test
    public void stringArray2boolean() throws Exception {
        setData(new String[] { "true", "false" });
        Object[] params = InjectUtil.getMethodInjectParams(getMethod("requireBoolean", boolean.class));
        assertEquals(params.length, 1);
        assertTrue((Boolean) params[0]);
    }

    @Test
    public void stringArray2booleanArray() throws Exception {
        setData(new String[] { "true", "false" });
        Object[] params = InjectUtil.getMethodInjectParams(getMethod("requireBoolean", boolean[].class));
        assertEquals(params.length, 1);
        assertEquals(params[0], new boolean[] { true, false });
    }

    @Test
    public void int2int() throws Exception {
        setData(10);
        Object[] params = InjectUtil.getMethodInjectParams(getMethod("requireInt", int.class));
        assertEquals(params.length, 1);
        assertEquals(((Integer) params[0]).intValue(), 10);
    }

    @Test
    public void int2intArray() throws Exception {
        setData(15);
        Object[] params = InjectUtil.getMethodInjectParams(getMethod("requireInt", int[].class));
        assertEquals(params.length, 1);
        assertEquals(params[0], new int[] { 15 });
    }

    @Test
    public void intArray2int() throws Exception {
        setData(new int[] { 10, 20 });
        Object[] params = InjectUtil.getMethodInjectParams(getMethod("requireInt", int.class));
        assertEquals(params.length, 1);
        assertEquals(((Integer) params[0]).intValue(), 10);
    }

    @Test
    public void intArray2intArray() throws Exception {
        setData(new int[] { 10, 20 });
        Object[] params = InjectUtil.getMethodInjectParams(getMethod("requireInt", int[].class));
        assertEquals(params.length, 1);
        assertEquals(params[0], new int[] { 10, 20 });
    }

    @Test
    public void string2int() throws Exception {
        setData("15");
        Object[] params = InjectUtil.getMethodInjectParams(getMethod("requireInt", int.class));
        assertEquals(params.length, 1);
        assertEquals(((Integer) params[0]).intValue(), 15);
    }

    @Test
    public void string2intArray() throws Exception {
        setData("15");
        Object[] params = InjectUtil.getMethodInjectParams(getMethod("requireInt", int[].class));
        assertEquals(params.length, 1);
        assertEquals(params[0], new int[] { 15 });
    }

    @Test
    public void stringArray2int() throws Exception {
        setData(new String[] { "10", "20" });
        Object[] params = InjectUtil.getMethodInjectParams(getMethod("requireInt", int.class));
        assertEquals(params.length, 1);
        assertEquals(((Integer) params[0]).intValue(), 10);
    }

    @Test
    public void stringArray2intArray() throws Exception {
        setData(new String[] { "10", "20" });
        Object[] params = InjectUtil.getMethodInjectParams(getMethod("requireInt", int[].class));
        assertEquals(params.length, 1);
        assertEquals(params[0], new int[] { 10, 20 });
    }

    @Test
    public void long2long() throws Exception {
        setData(15L);
        Object[] params = InjectUtil.getMethodInjectParams(getMethod("requireLong", long.class));
        assertEquals(params.length, 1);
        assertEquals(((Long) params[0]).longValue(), 15L);
    }

    @Test
    public void long2longArray() throws Exception {
        setData(15L);
        Object[] params = InjectUtil.getMethodInjectParams(getMethod("requireLong", long[].class));
        assertEquals(params.length, 1);
        assertEquals(params[0], new long[] { 15L });
    }

    @Test
    public void longArray2long() throws Exception {
        setData(new long[] { 10L, 20L });
        Object[] params = InjectUtil.getMethodInjectParams(getMethod("requireLong", long.class));
        assertEquals(params.length, 1);
        assertEquals(((Long) params[0]).longValue(), 10L);
    }

    @Test
    public void longArray2longArray() throws Exception {
        setData(new long[] { 10L, 20L });
        Object[] params = InjectUtil.getMethodInjectParams(getMethod("requireLong", long[].class));
        assertEquals(params.length, 1);
        assertEquals(params[0], new long[] { 10L, 20L });
    }

    @Test
    public void string2long() throws Exception {
        setData("15");
        Object[] params = InjectUtil.getMethodInjectParams(getMethod("requireLong", long.class));
        assertEquals(params.length, 1);
        assertEquals(((Long) params[0]).longValue(), 15L);
    }

    @Test
    public void string2longArray() throws Exception {
        setData("15");
        Object[] params = InjectUtil.getMethodInjectParams(getMethod("requireLong", long[].class));
        assertEquals(params.length, 1);
        assertEquals(params[0], new long[] { 15L });
    }

    @Test
    public void stringArray2long() throws Exception {
        setData(new String[] { "10", "20" });
        Object[] params = InjectUtil.getMethodInjectParams(getMethod("requireLong", long.class));
        assertEquals(params.length, 1);
        assertEquals(((Long) params[0]).longValue(), 10L);
    }

    @Test
    public void stringArray2longArray() throws Exception {
        setData(new String[] { "10", "20" });
        Object[] params = InjectUtil.getMethodInjectParams(getMethod("requireLong", long[].class));
        assertEquals(params.length, 1);
        assertEquals(params[0], new long[] { 10L, 20L });
    }

    /**
     * There is a bug for lambda inside method
     * 
     * @throws Exception
     */
    @Test
    public void lambdaInside() throws Exception {
        setData(null);
        Object[] params = InjectUtil.getMethodInjectParams(getMethod("lambdaInside", String.class));
        assertEquals(params.length, 1);
        assertEquals(params[0], null);
    }

    @Test(enabled = false)
    public void requireString(String param) {
    }

    @Test(enabled = false)
    public void requireString(String[] param) {
    }

    @Test(enabled = false)
    public void requireBoolean(boolean param) {
    }

    @Test(enabled = false)
    public void requireBoolean(boolean[] param) {
    }

    @Test(enabled = false)
    public void requireInt(int param) {
    }

    @Test(enabled = false)
    public void requireInt(int[] param) {
    }

    @Test(enabled = false)
    public void requireLong(long param) {
    }

    @Test(enabled = false)
    public void requireLong(long[] param) {
    }

    @Test(enabled = false)
    public void lambdaInside(String param) {
        List<String> names = Arrays.asList("hoge hoge", "foo bar", "naoki", "kishida");
        names.stream().forEach(s -> System.out.println(s));
    }

    private static void setData(Object data) {
        Context context = Context.getCurrentThreadContext();
        context.setData("param", data);
    }

    private static Method getMethod(String name, Class<?>... parameterTypes) throws NoSuchMethodException {
        return InjectUtilForMethodTest.class.getMethod(name, parameterTypes);
    }
}
