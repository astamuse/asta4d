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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.astamuse.asta4d.Configuration;
import com.astamuse.asta4d.test.render.infra.BaseTest;
import com.astamuse.asta4d.util.collection.ListConvertUtil;
import com.astamuse.asta4d.util.collection.ParallelRecursivePolicy;
import com.astamuse.asta4d.util.collection.RowConvertor;
import com.astamuse.asta4d.util.collection.RowConvertorBuilder;

public class ParallelListConvertingTest extends BaseTest {

    @BeforeClass
    public void setDefaultLocale() {
        Locale.setDefault(Locale.ROOT);
    }

    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = ".*Recursive parallel list converting is forbidden.*")
    public void testForException() {
        tryConvert(ParallelRecursivePolicy.EXCEPTION);
    }

    @Test
    public void testForCurrentThread() {
        tryConvert(ParallelRecursivePolicy.CURRENT_THREAD);
    }

    @Test
    public void testForNewThread() {
        tryConvert(ParallelRecursivePolicy.NEW_THREAD);
    }

    private void tryConvert(final ParallelRecursivePolicy policy) {
        Configuration.getConfiguration().setRecursivePolicyForParallelListConverting(policy);

        List<Integer> list = new ArrayList<>();
        list.add(1);

        ListConvertUtil.transform(list, new RowConvertor<Integer, List<Long>>() {

            public boolean isParallel() {
                return true;
            }

            @Override
            public List<Long> convert(int rowIndex, Integer obj) {
                List<Long> subList = new ArrayList<>();
                subList.add(Thread.currentThread().getId());
                return ListConvertUtil.transform(subList, RowConvertorBuilder.parallel(internalObj -> {
                    Long cid = Thread.currentThread().getId();
                    if (policy == ParallelRecursivePolicy.CURRENT_THREAD) {
                        Assert.assertEquals(cid, internalObj);
                    } else {
                        Assert.assertNotEquals(cid, internalObj);
                    }
                    return cid;
                }));
            }
        });
    }

    @DataProvider(name = "data")
    public Object[][] getNumberLimitOfParallelTestData() {
        //@formatter:off
        return new Object[][] {
            {Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9)},
            {Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8)},
            {Arrays.asList(1, 2, 3, 4, 5, 6, 7)},
        };
        //@formatter:on
    }

    /* we have to warm up the test at first, or the measured time may over 350ms due to the overhead of thread splitting*/
    @Test(dataProvider = "data", dependsOnMethods = "testNumberLimitOfParallelWarmingup")
    public void testNumberLimitOfParallel(List<Integer> list) {
        Configuration.getConfiguration().setNumberLimitOfParallelListConverting(3);
        long start = System.currentTimeMillis();
        List<Integer> rList = ListConvertUtil.transform(list, RowConvertorBuilder.parallel((i) -> {
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return i;
        }));
        long end = System.currentTimeMillis();
        long timeUsed = end - start;
        if (timeUsed < 200) {
            throw new AssertionError("Time used is less than 200 milliseconds with only " + timeUsed + " milliseconds.");
        }
        if (timeUsed > 350) {
            throw new AssertionError("Time used is over than 350 milliseconds and it takes " + timeUsed + " milliseconds.");
        }
        Assert.assertEquals(rList, list);
    }

    @Test(dataProvider = "data")
    public void testNumberLimitOfParallelWarmingup(List<Integer> list) {
        Configuration.getConfiguration().setNumberLimitOfParallelListConverting(3);
        for (int i = 0; i < 10_0000; i++) {
            List<Integer> rList = ListConvertUtil.transform(list, RowConvertorBuilder.parallel((k) -> {
                return k;
            }));
        }
    }

}
