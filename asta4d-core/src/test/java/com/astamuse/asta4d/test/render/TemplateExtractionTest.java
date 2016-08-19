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

package com.astamuse.asta4d.test.render;

import org.testng.annotations.Test;

import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.template.TemplateException;
import com.astamuse.asta4d.test.render.infra.BaseTest;
import com.astamuse.asta4d.test.render.infra.SimpleCase;

public class TemplateExtractionTest extends BaseTest {

    @Test
    public void loadTempateWithExtension() throws Throwable {
        new SimpleCase("TemplateWithExtension.html");
    }

    public static class RenderException {
        public Renderer render() {
            throw new RuntimeException("this method should not be invoked");
        }
    }

    @Test
    public void loadTempateWithClear() throws Throwable {
        new SimpleCase("TemplateWithClear.html");
    }

    @Test
    public void loadTempateWithComment() throws Throwable {
        new SimpleCase("TemplateWithComment.html");
    }

    public static class MetaRender {
        public Renderer render() {
            return Renderer.create("meta", "mm", "0");
        }
    }

    @Test
    public void loadTempateWithSpecialHeadTags() throws Throwable {
        new SimpleCase("TemplateWithSpecialHeadTags.html");
    }

    @Test
    public void loadTempateWithEmbed() throws Throwable {
        new SimpleCase("TemplateWithEmbed.html");
    }

    @Test(expectedExceptions = TemplateException.class, expectedExceptionsMessageRegExp = ".+does not exist\\.")
    public void loadTempateWithEmbedNotFound() throws Throwable {
        new SimpleCase("TemplateWithEmbedNotFound.html");
    }

    @Test
    public void loadTempateWithEmbedBaseFolder() throws Throwable {
        new SimpleCase("TemplateWithEmbedBaseFolder.html");
    }

    @Test
    public void loadTempateWithExtensionAndEmbedMerge() throws Throwable {
        new SimpleCase("TemplateWithExtensionAndEmbedMerge.html");
    }

    @Test
    public void loadTemplateWithThreeLevelExtension() throws Throwable {
        new SimpleCase("ThreeLevelExtension_Child.html");
    }

    @Test
    public void loadTemplateWithoutBodyTag() throws Throwable {
        new SimpleCase("TemplateWithoutBodyTag.html");
    }

    @Test
    public void loadTemplateWithBodyOnlyAttr() throws Throwable {
        new SimpleCase("TemplateWithBodyOnlyAttr.html");
    }

    @Test
    public void loadTemplateWithBodyOnlyMeta() throws Throwable {
        new SimpleCase("TemplateWithBodyOnlyMeta.html");
    }
}
