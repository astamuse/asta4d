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

package com.astamuse.asta4d.web.dispatch.response.provider;

import javax.servlet.http.HttpServletResponse;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.astamuse.asta4d.Page;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;

public class Asta4DPageProvider implements ContentProvider {

    public final static String AttrBodyOnly = Asta4DPageProvider.class.getName() + "##bodyOnly";

    private Page page;

    public Asta4DPageProvider() {
        this.page = null;
    }

    public Asta4DPageProvider(Page page) {
        this.page = page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    @Override
    public boolean isContinuable() {
        return false;
    }

    protected final static String DEFAULT_CONTENT_TYPE = "text/html; charset=UTF-8";

    protected String getContentType(Document doc) {
        Elements elems = doc.select("meta[http-equiv=Content-Type]");
        if (elems.size() == 0) {
            return DEFAULT_CONTENT_TYPE;
        } else {
            return elems.get(0).attr("content");
        }
    }

    @Override
    public void produce(UrlMappingRule currentRule, HttpServletResponse response) throws Exception {
        Document doc = page.getRenderedDocument();
        response.setContentType(getContentType(doc));

        // TODO we should try to retrieve the content type
        if (currentRule.hasAttribute(AttrBodyOnly)) {
            response.getOutputStream().write(doc.body().html().getBytes("UTF-8"));
        } else {
            response.getOutputStream().write(doc.outerHtml().getBytes("UTF-8"));
        }
    }

}
