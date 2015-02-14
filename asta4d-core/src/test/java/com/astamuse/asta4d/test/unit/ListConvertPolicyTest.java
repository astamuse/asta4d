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
import java.util.List;
import java.util.Locale;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.astamuse.asta4d.Configuration;
import com.astamuse.asta4d.test.render.infra.BaseTest;
import com.astamuse.asta4d.util.collection.ListConvertUtil;
import com.astamuse.asta4d.util.collection.ParallelRecursivePolicy;
import com.astamuse.asta4d.util.collection.RowConvertor;
import com.astamuse.asta4d.util.collection.RowConvertorBuilder;

public class ListConvertPolicyTest extends BaseTest {

    @BeforeClass
    public void setDefaultLocale() {
        Locale.setDefault(Locale.ROOT);
    }

    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = ".*Parallel list converting is forbidden.*")
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
        Configuration.getConfiguration().setParallelRecursivePolicyForListRendering(policy);

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

}
