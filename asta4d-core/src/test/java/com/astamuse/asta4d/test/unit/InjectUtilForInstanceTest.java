package com.astamuse.asta4d.test.unit;

import static org.testng.Assert.assertEquals;

import java.lang.reflect.Method;

import org.testng.annotations.Test;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.data.ContextDataHolder;
import com.astamuse.asta4d.data.InjectUtil;
import com.astamuse.asta4d.data.annotation.ContextData;
import com.astamuse.asta4d.data.annotation.ContextDataSet;
import com.astamuse.asta4d.test.render.infra.BaseTest;

public class InjectUtilForInstanceTest extends BaseTest {

    @ContextDataSet(singletonInContext = false)
    public static class TestSet {
        @ContextData
        private long f1;

        @ContextData
        public ContextDataHolder<Long> f2 = new ContextDataHolder<>(Long.class);

        private String f3;

        public long getF1() {
            return f1;
        }

        @ContextData
        public String getF3() {
            return f3;
        }

        public void setF3(String f3) {
            this.f3 = f3;
        }
    }

    @ContextDataSet(singletonInContext = true)
    public static class TestSingletonSet {
        @ContextData
        private long f1;
        @ContextData
        public ContextDataHolder<Long> f2 = new ContextDataHolder<>(Long.class);

        public long getF1() {
            return f1;
        }
    }

    public static class TestCls {
        @ContextData
        private TestSet myset;

        @ContextData
        private TestSingletonSet mySingletonSet;
    }

    @Test
    public void contextSetAndInjectableOnMethod() throws Exception {
        Context ctx = Context.getCurrentThreadContext();
        ctx.setData("f1", "6678");
        ctx.setData("f2", "12345");
        Object[] params = InjectUtil.getMethodInjectParams(getMethod("requireHolder", TestSet.class));
        TestSet set = (TestSet) params[0];
        assertEquals(set.getF1(), 6678L);
        assertEquals(set.f2.getValue().longValue(), 12345L);

    }

    @Test(enabled = false)
    public void requireHolder(TestSet holder) {
    }

    @Test
    public void contextSetAndInjectableOnInstance() throws Exception {
        Context ctx = Context.getCurrentThreadContext();
        ctx.setData("f1", "6678");
        ctx.setData("f2", "12345");
        ctx.setData("f3", "QQQ");

        TestCls tc = new TestCls();
        InjectUtil.injectToInstance(tc);

        assertEquals(tc.myset.getF1(), 6678L);
        assertEquals(tc.myset.f2.getValue().longValue(), 12345L);
        assertEquals(tc.myset.getF3(), "QQQ");

        assertEquals(tc.mySingletonSet.getF1(), 6678L);
        assertEquals(tc.mySingletonSet.f2.getValue().longValue(), 12345L);

        TestCls tc2 = new TestCls();
        InjectUtil.injectToInstance(tc2);

        assertEquals(tc.myset == tc2.myset, false);// new instance
        assertEquals(tc.mySingletonSet == tc2.mySingletonSet, true);// the same instance

    }

    @Test
    public void retriveContextSetInScopeFirst() throws Exception {
        Context ctx = Context.getCurrentThreadContext();
        ctx.setData("f1", "6678");
        ctx.setData("f2", "12345");

        // retrieve a set at first
        TestCls tc = new TestCls();
        InjectUtil.injectToInstance(tc);
        TestSet set = tc.myset;

        // save the set into the context
        ctx.setData("myset", set);

        // retrieve again
        tc = new TestCls();
        InjectUtil.injectToInstance(tc);

        // should be the same instance
        assertEquals(tc.myset == set, true);

    }

    private static Method getMethod(String name, Class<?>... parameterTypes) throws NoSuchMethodException {
        return InjectUtilForInstanceTest.class.getMethod(name, parameterTypes);
    }
}
