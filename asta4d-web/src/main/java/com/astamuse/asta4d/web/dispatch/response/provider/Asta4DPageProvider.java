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

import com.astamuse.asta4d.Page;
import com.astamuse.asta4d.extnode.ExtNodeConstants;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;

public class Asta4DPageProvider implements ContentProvider {

    /**
     * This attribute hash been deprecated and you should use afd:bodyonly in template file or just create a template file without body
     * tag(also without html and head tags).
     * 
     * @see ExtNodeConstants#ATTR_BODY_ONLY_WITH_NS
     */
    @Deprecated
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

    @SuppressWarnings("deprecation")
    @Override
    public void produce(UrlMappingRule currentRule, HttpServletResponse response) throws Exception {
        response.setContentType(page.getContentType());
        if (currentRule.hasAttribute(AttrBodyOnly)) {
            page.outputBodyOnly(response.getOutputStream());
        } else {
            page.output(response.getOutputStream());
        }
    }

}
