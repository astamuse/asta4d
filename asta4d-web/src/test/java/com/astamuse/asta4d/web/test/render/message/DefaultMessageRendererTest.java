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
package com.astamuse.asta4d.web.test.render.message;

import org.testng.annotations.Test;

import com.astamuse.asta4d.web.test.WebTestBase;
import com.astamuse.asta4d.web.test.render.base.WebRenderCase;
import com.astamuse.asta4d.web.util.message.DefaultMessageRenderingHelper;

public class DefaultMessageRendererTest extends WebTestBase {

    @Test
    public void existingMsgSelector() throws Throwable {
        DefaultMessageRenderingHelper msgHelper = DefaultMessageRenderingHelper.getConfiguredInstance();
        msgHelper.info("#msg-info", "iinnffoo1");
        msgHelper.info("#msg-info", "iinnffoo2");
        msgHelper.warn("#msg-warn", "warn-1");
        msgHelper.err("#msg-err", "err-1");
        msgHelper.err("#msg-err", "err-2");
        msgHelper.err("#msg-err", "err-3");
        new WebRenderCase("DefaultMessageRender_existingMsgSelector.html");
    }

    @Test
    public void notExistingMsgSelector() throws Throwable {
        DefaultMessageRenderingHelper msgHelper = DefaultMessageRenderingHelper.getConfiguredInstance();
        msgHelper.info("#msg-info-ne", "iinnffoo1");
        msgHelper.info("#msg-info-ne", "iinnffoo2");
        msgHelper.warn("#msg-warn-ne", "warn-1");
        msgHelper.err("#msg-err-ne", "err-1");
        msgHelper.err("#msg-err-ne", "err-2");
        msgHelper.err("#msg-err-ne", "err-3");
        new WebRenderCase("DefaultMessageRender_notExistingMsgSelector.html");
    }

    @Test
    public void someExistingMsgSelector() throws Throwable {
        DefaultMessageRenderingHelper msgHelper = DefaultMessageRenderingHelper.getConfiguredInstance();
        msgHelper.info("#msg-info-ne", "iinnffoo1");// in global
        msgHelper.info("#msg-info", "iinnffoo2");// in place
        msgHelper.warn("#msg-warn-ne", "warn-1");// in global
        msgHelper.err("#msg-err", "err-1");// in place
        msgHelper.err("#msg-err-ne", "err-2");// in global
        msgHelper.err("#msg-err-ne", "err-3");// in global
        new WebRenderCase("DefaultMessageRender_someExistingMsgSelector.html");
    }

    @Test
    public void someExistingMsgSelector2() throws Throwable {
        DefaultMessageRenderingHelper msgHelper = DefaultMessageRenderingHelper.getConfiguredInstance();
        msgHelper.info("#msg-info-ne", "iinnffoo1");// in global
        msgHelper.info("#msg-info", "iinnffoo2");// in place
        msgHelper.err("#msg-err-ne", "err-1");// in global
        msgHelper.err("#msg-err-ne", "err-2");// in global
        new WebRenderCase("DefaultMessageRender_someExistingMsgSelector2.html");
    }

}
