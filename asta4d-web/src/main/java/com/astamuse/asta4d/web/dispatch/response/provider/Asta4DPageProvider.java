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

import com.astamuse.asta4d.Page;
import com.astamuse.asta4d.web.dispatch.response.writer.Asta4DPageWriter;
import com.astamuse.asta4d.web.dispatch.response.writer.ContentWriter;

public class Asta4DPageProvider implements ContentProvider<Page> {

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

    @Override
    public Page produce() throws Exception {
        return page;
    }

    @Override
    public Class<? extends ContentWriter<Page>> getContentWriter() {
        return Asta4DPageWriter.class;
    }

}
