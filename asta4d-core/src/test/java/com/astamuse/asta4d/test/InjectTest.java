package com.astamuse.asta4d.test;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.test.infra.BaseTest;
import com.astamuse.asta4d.test.infra.SimpleCase;

public class InjectTest extends BaseTest {

    public static class TestRender {
        public Renderer methodDefaultSearch(String av, String pv, String cv, String gv) {
            Renderer render = Renderer.create("#av-value", av);
            render.add("#pv-value", pv);
            render.add("#cv-value", cv);
            render.add("#gv-value", gv);
            return render;
        }
    }

    public void testMethodDefaultSearch() {
        Context context = Context.getCurrentThreadContext();
        context.setData(Context.SCOPE_DEFAULT, "pv", "pv-value at context");
        context.setData(Context.SCOPE_DEFAULT, "cv", "cv-value at context");
        context.setData(Context.SCOPE_GLOBAL, "cv", "cv-value at global");
        context.setData(Context.SCOPE_GLOBAL, "gv", "gv-value");
        new SimpleCase("Inject_testMethodDefaultSearch.html");
    }

}
