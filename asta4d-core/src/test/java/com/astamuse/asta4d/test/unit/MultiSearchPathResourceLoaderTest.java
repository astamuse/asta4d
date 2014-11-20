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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

import org.testng.annotations.Test;

import com.astamuse.asta4d.util.MultiSearchPathResourceLoader;

public class MultiSearchPathResourceLoaderTest {

    @Test
    public void searchForOneName() {
        StringResourceLoaderForTest loader = new StringResourceLoaderForTest(new String[] { "foo", "bar", "baz" });
        String resource = loader.searchResource("/", "bar");
        assertEquals("bar", resource);
    }

    @Test
    public void searchForMultiName() {
        StringResourceLoaderForTest loader = new StringResourceLoaderForTest(new String[] { "foo", "bar", "baz" });
        String resource = loader.searchResource("/", "qux", "baz");
        assertEquals("baz", resource);
    }

    @Test
    public void searchForMultiName_NotFound() {
        StringResourceLoaderForTest loader = new StringResourceLoaderForTest(new String[] { "foo", "bar", "baz" });
        String resource = loader.searchResource("/", "qux", "quux");
        assertNull(resource);
    }

    @Test
    public void searchForMultiPath() {
        StringResourceLoaderForTest loader = new StringResourceLoaderForTest(new String[] { "foo", "bar", "baz", "path1/bar", "path2/qux" });
        loader.setSearchPathList("path1", "path2");
        String resource = loader.searchResource("/", "qux");
        assertEquals("path2/qux", resource);
    }

    @Test
    public void searchForMultiPath_FindMultiResource() {
        StringResourceLoaderForTest loader = new StringResourceLoaderForTest(new String[] { "foo", "bar", "baz", "path1/bar", "path2/qux" });
        loader.setSearchPathList("path1", "path2");
        String resource = loader.searchResource("/", "bar");
        assertEquals("bar", resource);
    }

    @Test
    public void searchForMultiPath_NotFound() {
        StringResourceLoaderForTest loader = new StringResourceLoaderForTest(new String[] { "foo", "bar", "baz", "path1/bar", "path2/qux" });
        loader.setSearchPathList("path1", "path2");
        String resource = loader.searchResource("/", "quux", "foobar");
        assertNull(resource);
    }

    private static class StringResourceLoaderForTest extends MultiSearchPathResourceLoader<String> {

        private final String[] resources;

        public StringResourceLoaderForTest(String[] resources) {
            this.resources = resources;
        }

        @Override
        protected String loadResource(String name) {
            for (String resource : resources) {
                if (resource.equals(name)) {
                    return resource;
                }
            }
            return null;
        }
    }
}
