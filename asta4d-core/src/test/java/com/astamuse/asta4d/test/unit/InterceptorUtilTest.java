package com.astamuse.asta4d.test.unit;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import com.astamuse.asta4d.interceptor.base.ExceptionHandler;
import com.astamuse.asta4d.interceptor.base.Executor;
import com.astamuse.asta4d.interceptor.base.GenericInterceptor;
import com.astamuse.asta4d.interceptor.base.InterceptorUtil;

public class InterceptorUtilTest {

    @Test
    public void succeedAllProcess() throws Exception {
        TestHolder holder = new TestHolder();
        List<TestIntercepter> interceptorList = new ArrayList<>();
        interceptorList.add(new TestIntercepter("Intercepter1"));
        interceptorList.add(new TestIntercepter("Intercepter2"));
        interceptorList.add(new TestIntercepter("Intercepter3"));
        TestExecutor executor = new TestExecutor();
        InterceptorUtil.executeWithInterceptors(holder, interceptorList, executor);
        assertEquals(holder.getExecutedBeforeProcessNames(), new String[] { "Intercepter1", "Intercepter2", "Intercepter3" });
        assertEquals(holder.getExecutedAfterProcessNames(), new String[] { "Intercepter3", "Intercepter2", "Intercepter1" });
    }

    @Test
    public void breakBeforeProcess() throws Exception {
        TestHolder holder = new TestHolder();
        List<TestIntercepter> interceptorList = new ArrayList<>();
        interceptorList.add(new TestIntercepter("Intercepter1"));
        interceptorList.add(new TestIntercepter("Intercepter2", false, false));
        interceptorList.add(new TestIntercepter("Intercepter3"));
        TestExecutor executor = new TestExecutor();
        InterceptorUtil.executeWithInterceptors(holder, interceptorList, executor);
        assertEquals(holder.getExecutedBeforeProcessNames(), new String[] { "Intercepter1", "Intercepter2" });
        assertEquals(holder.getExecutedAfterProcessNames(), new String[] { "Intercepter1" });
    }

    @Test
    public void failureMainProcess() throws Exception {
        TestHolder holder = new TestHolder();
        List<TestIntercepter> interceptorList = new ArrayList<>();
        interceptorList.add(new TestIntercepter("Intercepter1"));
        interceptorList.add(new TestIntercepter("Intercepter2"));
        interceptorList.add(new TestIntercepter("Intercepter3"));
        TestExecutor executor = new TestExecutor(true);
        try {
            InterceptorUtil.executeWithInterceptors(holder, interceptorList, executor);
            fail("No exceptions occurred.");
        } catch (TestException e) {
            assertEquals(holder.getExecutedBeforeProcessNames(), new String[] { "Intercepter1", "Intercepter2", "Intercepter3" });
            assertEquals(holder.getExecutedAfterProcessNames(), new String[] { "Intercepter3", "Intercepter2", "Intercepter1" });
        }
    }

    @Test
    public void failureBeforeProcess() throws Exception {
        TestHolder holder = new TestHolder();
        List<TestIntercepter> interceptorList = new ArrayList<>();
        interceptorList.add(new TestIntercepter("Intercepter1"));
        interceptorList.add(new TestIntercepter("Intercepter2", true, true));
        interceptorList.add(new TestIntercepter("Intercepter3"));
        TestExecutor executor = new TestExecutor();
        try {
            InterceptorUtil.executeWithInterceptors(holder, interceptorList, executor);
            fail("No exceptions occurred.");
        } catch (TestException e) {
            assertEquals(holder.getExecutedBeforeProcessNames(), new String[] { "Intercepter1", "Intercepter2" });
            assertEquals(holder.getExecutedAfterProcessNames(), new String[] { "Intercepter1" });
        }
    }

    private static class TestHolder {

        private final List<String> executedBeforeProcessNames = new ArrayList<>();
        private final List<String> executedAfterProcessNames = new ArrayList<>();

        public void addBeforeProcessName(String name) {
            executedBeforeProcessNames.add(name);
        }

        public void addAfterProcessName(String name) {
            executedAfterProcessNames.add(name);
        }

        public String[] getExecutedBeforeProcessNames() {
            return executedBeforeProcessNames.toArray(new String[executedBeforeProcessNames.size()]);
        }

        public String[] getExecutedAfterProcessNames() {
            return executedAfterProcessNames.toArray(new String[executedAfterProcessNames.size()]);
        }
    }

    public static class TestExecutor implements Executor<TestHolder> {

        private final boolean failure;

        public TestExecutor() {
            this(false);
        }

        public TestExecutor(boolean failure) {
            this.failure = failure;
        }

        @Override
        public void execute(TestHolder executionHolder) throws Exception {
            if (this.failure) {
                throw new TestException();
            }
        }
    }

    public static class TestIntercepter implements GenericInterceptor<TestHolder> {

        private final String name;
        private final boolean beforeResult;
        private final boolean failureBefore;

        public TestIntercepter(String name) {
            this(name, true, false);
        }

        public TestIntercepter(String name, boolean beforeResult, boolean failBefore) {
            this.name = name;
            this.beforeResult = beforeResult;
            this.failureBefore = failBefore;
        }

        @Override
        public boolean beforeProcess(TestHolder executionHolder) throws Exception {
            executionHolder.addBeforeProcessName(name);
            if (failureBefore) {
                throw new TestException();
            }
            return beforeResult;
        }

        @Override
        public void afterProcess(TestHolder executionHolder, ExceptionHandler exceptionHandler) {
            executionHolder.addAfterProcessName(name);
        }
    }

    @SuppressWarnings("serial")
    public static class TestException extends Exception {
    }
}
