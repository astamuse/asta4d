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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.astamuse.asta4d.render.ElementNotFoundHandler;
import com.astamuse.asta4d.render.RenderUtil;
import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.test.render.infra.BaseTest;

public class ElementNotFoundHandlerOnDocumentTest extends BaseTest {

    @Test
    public void notFoundOnDocument() throws Exception {
        String html = "<html><body><span>x</span></body></html>";
        Document doc = Jsoup.parse(html);

        Renderer renderer = Renderer.create();
        renderer.add(new ElementNotFoundHandler("div") {
            @Override
            public Renderer alternativeRenderer() {
                return Renderer.create("span", "y");
            }
        });

        RenderUtil.apply(doc, renderer);
        Assert.assertEquals(doc.select("span").text(), "y");
    }
}
