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
package com.astamuse.asta4d.web.test.form;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.astamuse.asta4d.web.WebApplicationContext;
import com.astamuse.asta4d.web.form.field.FormFieldPrepareRenderer;
import com.astamuse.asta4d.web.form.flow.base.FormFlowConstants;
import com.astamuse.asta4d.web.form.flow.base.FormFlowTraceData;
import com.astamuse.asta4d.web.form.flow.classical.ClassicalMultiStepFormFlowHandlerTrait;
import com.astamuse.asta4d.web.form.flow.classical.ClassicalMultiStepFormFlowSnippetTrait;
import com.astamuse.asta4d.web.form.validation.FormValidationMessage;
import com.astamuse.asta4d.web.test.WebTestBase;

public class MultiStepFormTest extends WebTestBase {

    private static final String FAKE_TRACE_MAP_ID = "FAKE_TRACE_MAP_ID";

    private static FormFlowTraceData savedTraceData = null;

    private static TestForm savedForm = null;

    public static class TestHandler implements ClassicalMultiStepFormFlowHandlerTrait<TestForm> {

        private List<FormValidationMessage> msgList = new LinkedList<>();

        @Override
        public Class<TestForm> getFormCls() {
            return TestForm.class;
        }

        @Override
        public String getTemplateBasePath() {
            return "/testform/";
        }

        @Override
        public void updateForm(TestForm form) {
            savedForm = form;
        }

        @Override
        public TestForm createInitForm() {
            TestForm form = new TestForm();
            form.subForm = new SubForm();
            form.subArray = new SubArray[0];
            form.subArray2 = new SubArray2[0];
            form.subArrayLength = 0;
            form.subArrayLength2 = 0;
            return form;
        }

        @Override
        public String storeTraceData(String currentStep, String renderTargetStep, String traceId, FormFlowTraceData traceData) {
            savedTraceData = traceData;
            return FAKE_TRACE_MAP_ID;
        }

        @Override
        public FormFlowTraceData retrieveTraceData(String traceId) {
            return savedTraceData;
        }

        @Override
        public void clearStoredTraceData(String traceId) {
            savedTraceData = null;
        }

        @Override
        public void outputValidationMessage(FormValidationMessage msg) {
            msgList.add(msg);
        }

        public void assertMessageSize(int expectedSize) {
            try {
                Assert.assertEquals(msgList.size(), expectedSize);
            } catch (AssertionError e) {
                throw new AssertionError(e.getMessage() + createExistingMessageInfo(), e);
            }
        }

        public void assertMessage(String name, String msgReg) {
            for (FormValidationMessage msg : msgList) {
                if (msg.getFieldName().equals(name) && msg.getMessage().matches(msgReg)) {
                    return;
                }
            }
            String errMsg = "expected msg as name[%s] and msg(reg)[%s] but not found.";
            errMsg = String.format(errMsg, name, msgReg);
            errMsg += createExistingMessageInfo();
            throw new AssertionError(errMsg);
        }

        private String createExistingMessageInfo() {
            String s = "Existing messages:\n";
            for (FormValidationMessage msg : msgList) {
                s += msg.toString() + "\n";
            }
            return s;
        }
    }

    public static class TestSnippet implements ClassicalMultiStepFormFlowSnippetTrait {
        private static Map<String, Integer> formCounterMap = new HashMap<>();

        public TestSnippet() {
            formCounterMap.clear();
        }

        public static void assertFormCounterSize(int expectedSize) {
            Assert.assertEquals(formCounterMap.size(), expectedSize);
        }

        public static void assertFormCounter(Class formCls, Integer expectedCount) {
            try {
                Assert.assertEquals(formCounterMap.get(formCls.getName()), expectedCount);
            } catch (AssertionError e) {
                throw new AssertionError(formCls.getName() + ":" + e.getMessage(), e);
            }
        }

        @Override
        public List<FormFieldPrepareRenderer> retrieveFieldPrepareRenderers(String renderTargetStep, Object form) {
            Integer count = formCounterMap.get(form.getClass().getName());
            if (count == null) {
                count = 1;
            } else {
                count = count + 1;
            }
            formCounterMap.put(form.getClass().getName(), count);
            return ClassicalMultiStepFormFlowSnippetTrait.super.retrieveFieldPrepareRenderers(renderTargetStep, form);
        }
    }

    private Enumeration<String> requestParametersEnum(Map<String, String[]> map) {
        return Collections.enumeration(map.keySet());
    }

    private void mockRequestParameter(HttpServletRequest request, Map<String, String[]> map) {
        when(request.getParameterNames()).thenReturn(requestParametersEnum(map));
        for (Entry<String, String[]> entry : map.entrySet()) {
            when(request.getParameterValues(entry.getKey())).thenReturn(entry.getValue());
        }
    }

    @BeforeMethod
    public void before() {
        super.initContext();
        WebApplicationContext context = WebApplicationContext.getCurrentThreadWebApplicationContext();

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        context.setRequest(request);
        context.setResponse(response);

        HttpSession session = mock(HttpSession.class);

        when(request.getCookies()).thenReturn(new Cookie[0]);
        when(request.getHeaderNames()).thenReturn(Collections.emptyEnumeration());
        when(request.getSession(true)).thenReturn(session);
    }

    private void initParams(Map<String, String[]> map) throws Exception {

        WebApplicationContext context = WebApplicationContext.getCurrentThreadWebApplicationContext();
        HttpServletRequest request = context.getRequest();

        mockRequestParameter(request, map);

    }

    private Map<String, String[]> requestParameters_init = new HashMap<String, String[]>();

    @Test
    public void testInitStep() throws Throwable {

        initParams(requestParameters_init);

        TestHandler handler = new TestHandler();

        Assert.assertEquals(handler.handle(), "/testform/input.html");

        Assert.assertNull(savedForm);

        new FormRenderCase("MultiStepForm_initInput.html");

        TestSnippet.assertFormCounterSize(4);
        TestSnippet.assertFormCounter(TestForm.class, 1);
        TestSnippet.assertFormCounter(SubForm.class, 1);
        TestSnippet.assertFormCounter(SubArray.class, 1);
        TestSnippet.assertFormCounter(SubArray2.class, 1);

    }

    private Map<String, String[]> requestParameters_inputWithoutTraceMap = new HashMap<String, String[]>() {
        {
            put("id", new String[] { "1" });
            put("data", new String[] { "data-content" });
            put("step-current", new String[] { "input" });
            put("step-failed", new String[] { "input" });
            put("step-success", new String[] { "confirm" });
        }
    };

    @Test(dependsOnMethods = "testInitStep")
    public void testInputWithoutTraceMap() throws Throwable {

        initParams(requestParameters_inputWithoutTraceMap);

        TestHandler handler = new TestHandler();

        Assert.assertEquals(handler.handle(), "/testform/input.html");

        Assert.assertNull(savedForm);

        new FormRenderCase("MultiStepForm_inputWithoutTraceMap.html");

        TestSnippet.assertFormCounterSize(4);
        TestSnippet.assertFormCounter(TestForm.class, 1);
        TestSnippet.assertFormCounter(SubForm.class, 1);
        TestSnippet.assertFormCounter(SubArray.class, 1);
        TestSnippet.assertFormCounter(SubArray2.class, 1);
    }

    private Map<String, String[]> requestParameters_inputWithTypeUnMatchError = new HashMap<String, String[]>() {
        {
            put("id", new String[] { "ss" });
            put("data", new String[] { "data-content" });
            put("subData", new String[] { "sub data" });
            put("year-0", new String[] { "sub array year" });
            put("subArrayLength", new String[] { "1" });
            put("subArrayLength2", new String[] { "0" });
            put("step-current", new String[] { "input" });
            put("step-failed", new String[] { "input" });
            put("step-success", new String[] { "confirm" });
        }
    };

    private static final String IntegerTypeUnMatch = ".+ Integer is expected but value\\[.+\\] found\\.";

    @Test(dependsOnMethods = "testInputWithoutTraceMap")
    public void testInputWithTypeUnMatchError() throws Throwable {

        initParams(requestParameters_inputWithTypeUnMatchError);

        TestHandler handler = new TestHandler();

        Assert.assertEquals(handler.handle(), "/testform/input.html");

        Assert.assertNull(savedForm);

        handler.assertMessageSize(4);
        handler.assertMessage("id", IntegerTypeUnMatch);
        handler.assertMessage("subData", IntegerTypeUnMatch);
        handler.assertMessage("year-0", IntegerTypeUnMatch);
        handler.assertMessage("subArray2", MsgNotEmpty);

        new FormRenderCase("MultiStepForm_inputWithTypeUnMatchError.html");

        TestSnippet.assertFormCounterSize(4);
        TestSnippet.assertFormCounter(TestForm.class, 1);
        TestSnippet.assertFormCounter(SubForm.class, 1);
        TestSnippet.assertFormCounter(SubArray.class, 2);
        TestSnippet.assertFormCounter(SubArray2.class, 1);
    }

    private Map<String, String[]> requestParameters_inputWithValueValidationError = new HashMap<String, String[]>() {
        {
            put("id", new String[] { "77" });
            put("data", new String[] { "" });
            // put("subData", new String[] { "sub data" });
            put("year-0", new String[] { "2002" });
            put("year-1", new String[] { "2003" });
            put("subArrayLength", new String[] { "2" });
            put("subArrayLength2", new String[] { "0" });
            put("step-current", new String[] { "input" });
            put("step-failed", new String[] { "input" });
            put("step-success", new String[] { "confirm" });
        }
    };

    private static final String MsgNotEmpty = ".+ may not be empty";

    private static final String MsgNotNULL = ".+ may not be null";

    private static final String MsgMax = ".+ must be less than or equal to [0-9]+";

    @Test(dependsOnMethods = "testInputWithTypeUnMatchError")
    public void testInputWithValueValidationError() throws Throwable {

        initParams(requestParameters_inputWithValueValidationError);

        TestHandler handler = new TestHandler();

        Assert.assertEquals(handler.handle(), "/testform/input.html");

        Assert.assertNull(savedForm);

        handler.assertMessageSize(6);
        handler.assertMessage("id", MsgMax);
        handler.assertMessage("data", MsgNotEmpty);
        handler.assertMessage("subData", MsgNotNULL);
        handler.assertMessage("year-0", MsgMax);
        handler.assertMessage("year-1", MsgMax);
        handler.assertMessage("subArray2", MsgNotEmpty);

        new FormRenderCase("MultiStepForm_inputWithValueValidationError.html");

        TestSnippet.assertFormCounterSize(4);
        TestSnippet.assertFormCounter(TestForm.class, 1);
        TestSnippet.assertFormCounter(SubForm.class, 1);
        TestSnippet.assertFormCounter(SubArray.class, 3);
        TestSnippet.assertFormCounter(SubArray2.class, 1);
    }

    private Map<String, String[]> requestParameters_goToConfirm = new HashMap<String, String[]>() {
        {
            put("id", new String[] { "22" });
            put("data", new String[] { "data-content" });
            put("subData", new String[] { "123" });
            put("year-0", new String[] { "1998" });
            put("year-1", new String[] { "1999" });
            put("subArrayLength", new String[] { "2" });
            put("age-0", new String[] { "88" });
            put("subArrayLength2", new String[] { "1" });
            put("step-current", new String[] { "input" });
            put("step-failed", new String[] { "input" });
            put("step-success", new String[] { "confirm" });
        }
    };

    @Test(dependsOnMethods = "testInputWithValueValidationError")
    public void testGoToConfirm() throws Throwable {

        initParams(requestParameters_goToConfirm);

        TestHandler handler = new TestHandler();

        Assert.assertEquals(handler.handle(), "/testform/confirm.html");

        Assert.assertNull(savedForm);

        handler.assertMessageSize(0);

        new FormRenderCase("MultiStepForm_goToConfirm.html");

        TestSnippet.assertFormCounterSize(4);
        TestSnippet.assertFormCounter(TestForm.class, 1);
        TestSnippet.assertFormCounter(SubForm.class, 1);
        TestSnippet.assertFormCounter(SubArray.class, 2);
        TestSnippet.assertFormCounter(SubArray2.class, 1);

        TestSnippet.formCounterMap.clear();

        new FormRenderCase("MultiStepForm_goToConfirm_withDisplay.html");

        TestSnippet.assertFormCounterSize(4);
        TestSnippet.assertFormCounter(TestForm.class, 1);
        TestSnippet.assertFormCounter(SubForm.class, 1);
        TestSnippet.assertFormCounter(SubArray.class, 2);
        TestSnippet.assertFormCounter(SubArray2.class, 1);
    }

    private Map<String, String[]> requestParameters_exit = new HashMap<String, String[]>() {
        {
            put("step-exit", new String[] { "exit" });
        }
    };

    @Test(dependsOnMethods = "testGoToConfirm")
    public void testExit() throws Throwable {

        initParams(requestParameters_exit);

        TestHandler handler = new TestHandler();

        Assert.assertNull(handler.handle());
        Assert.assertNull(savedForm);
        Assert.assertNull(savedTraceData);
        handler.assertMessageSize(0);
    }

    @Test(dependsOnMethods = "testExit")
    public void testInitAgain() throws Throwable {
        testInitStep();
    }

    @Test(dependsOnMethods = "testInitAgain")
    public void testGoConfirmAgain() throws Throwable {
        testGoToConfirm();
    }

    private Map<String, String[]> requestParameters_goBack = new HashMap<String, String[]>() {
        {
            put("step-current", new String[] { "confirm" });
            put("step-failed", new String[] { "input" });
            put("step-back", new String[] { "input" });
            put(FormFlowConstants.FORM_FLOW_TRACE_ID_QUERY_PARAM, new String[] { FAKE_TRACE_MAP_ID });
        }
    };

    @Test(dependsOnMethods = "testGoConfirmAgain")
    public void testGoBack() throws Throwable {
        initParams(requestParameters_goBack);

        TestHandler handler = new TestHandler();

        Assert.assertEquals(handler.handle(), "/testform/input.html");

        Assert.assertNull(savedForm);

        handler.assertMessageSize(0);

        new FormRenderCase("MultiStepForm_goBack.html");

        TestSnippet.assertFormCounterSize(4);
        TestSnippet.assertFormCounter(TestForm.class, 1);
        TestSnippet.assertFormCounter(SubForm.class, 1);
        TestSnippet.assertFormCounter(SubArray.class, 3);
        TestSnippet.assertFormCounter(SubArray2.class, 2);
    }

    @Test(dependsOnMethods = "testGoBack")
    public void testGoConfirmAgainAgain() throws Throwable {
        testGoToConfirm();
    }

    private Map<String, String[]> requestParameters_complete = new HashMap<String, String[]>() {
        {
            put("step-current", new String[] { "confirm" });
            put("step-failed", new String[] { "input" });
            put("step-success", new String[] { "complete" });
            put(FormFlowConstants.FORM_FLOW_TRACE_ID_QUERY_PARAM, new String[] { FAKE_TRACE_MAP_ID });
        }
    };

    @Test(dependsOnMethods = "testGoConfirmAgainAgain")
    public void testComplete() throws Throwable {

        initParams(requestParameters_complete);

        TestHandler handler = new TestHandler();

        Assert.assertEquals(handler.handle(), "/testform/complete.html");

        Assert.assertNull(savedTraceData);

        handler.assertMessageSize(0);

        Assert.assertEquals(savedForm.id.intValue(), 22);
        Assert.assertEquals(savedForm.data, "data-content");
        Assert.assertEquals(savedForm.subForm.subData.intValue(), 123);
        Assert.assertEquals(savedForm.subArray.length, 2);
        Assert.assertEquals(savedForm.subArray[0].year.intValue(), 1998);
        Assert.assertEquals(savedForm.subArray[1].year.intValue(), 1999);
        Assert.assertEquals(savedForm.subArray2.length, 1);
        Assert.assertEquals(savedForm.subArray2[0].age.intValue(), 88);

        // should be same as confirm
        new FormRenderCase("MultiStepForm_complete.html");

        new FormRenderCase("MultiStepForm_complete_withDisplay.html");
    }
}
