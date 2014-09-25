package com.astamuse.asta4d.web.test.render.message;

import org.testng.annotations.Test;

import com.astamuse.asta4d.web.test.WebTestBase;
import com.astamuse.asta4d.web.test.render.base.WebRenderCase;
import com.astamuse.asta4d.web.util.message.DefaultMessageRenderingHelper;

public class DefaultMessageRendererTest extends WebTestBase {

    @Test
    public void existingMsgSelector() {
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
    public void notExistingMsgSelector() {
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
    public void someExistingMsgSelector() {
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
    public void someExistingMsgSelector2() {
        DefaultMessageRenderingHelper msgHelper = DefaultMessageRenderingHelper.getConfiguredInstance();
        msgHelper.info("#msg-info-ne", "iinnffoo1");// in global
        msgHelper.info("#msg-info", "iinnffoo2");// in place
        msgHelper.err("#msg-err-ne", "err-1");// in global
        msgHelper.err("#msg-err-ne", "err-2");// in global
        new WebRenderCase("DefaultMessageRender_someExistingMsgSelector2.html");
    }

}
