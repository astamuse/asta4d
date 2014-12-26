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

import java.util.ResourceBundle;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.astamuse.asta4d.test.render.infra.BaseTest;
import com.astamuse.asta4d.util.i18n.pattern.JDKResourceBundleMessagePatternRetriever;

@Test
public class JDKResourceBundleMessagePatternRetrieverTest extends BaseTest {

    private static final String EXISTING_FILE = "com/astamuse/asta4d/test/unit/JDKResourceBundleMessagePatternRetrieverTest";
    private static final String NOT_EXISTING_FILE = "com/astamuse/asta4d/test/unit/JDKResourceBundleMessagePatternRetrieverTest_NotExisting";
    private static final String KEY_NOT_EXISTING_FILE = "com/astamuse/asta4d/test/unit/JDKResourceBundleMessagePatternRetrieverTest_KeyNotExisting";

    @BeforeMethod
    public void beforeMethod() {
        ResourceBundle.clearCache();
    }

    public void testSingleResourceFileExistingKey() {
        JDKResourceBundleMessagePatternRetriever retriever = new JDKResourceBundleMessagePatternRetriever();
        retriever.setResourceNames(EXISTING_FILE);
        Assert.assertEquals(retriever.retrieve(null, "test"), "testxxx");
    }

    public void testSingleResourceFileNotExistingKey() {
        JDKResourceBundleMessagePatternRetriever retriever = new JDKResourceBundleMessagePatternRetriever();
        retriever.setResourceNames(EXISTING_FILE);
        Assert.assertNull(retriever.retrieve(null, "test_not_existing"));
    }

    public void testMultiResourceFileExistingKey() {
        JDKResourceBundleMessagePatternRetriever retriever = new JDKResourceBundleMessagePatternRetriever();
        retriever.setResourceNames(NOT_EXISTING_FILE, EXISTING_FILE);
        Assert.assertEquals(retriever.retrieve(null, "test"), "testxxx");
    }

    public void testMultiResourceFileNotExistingKey() {
        JDKResourceBundleMessagePatternRetriever retriever = new JDKResourceBundleMessagePatternRetriever();
        retriever.setResourceNames(NOT_EXISTING_FILE, EXISTING_FILE);
        Assert.assertNull(retriever.retrieve(null, "test_not_existing"));
    }

    public void testMultiResourceFileExistingKey2() {
        JDKResourceBundleMessagePatternRetriever retriever = new JDKResourceBundleMessagePatternRetriever();
        retriever.setResourceNames(NOT_EXISTING_FILE, EXISTING_FILE, KEY_NOT_EXISTING_FILE);
        Assert.assertEquals(retriever.retrieve(null, "test"), "testxxx");
    }

    public void testMultiResourceFileNotExistingKey2() {
        JDKResourceBundleMessagePatternRetriever retriever = new JDKResourceBundleMessagePatternRetriever();
        retriever.setResourceNames(NOT_EXISTING_FILE, EXISTING_FILE, KEY_NOT_EXISTING_FILE);
        Assert.assertNull(retriever.retrieve(null, "test_not_existing"));
    }

}
