/*


 * [Copyright 2012 astamuse company,Ltd.
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

package com.astamuse.asta4d.test.render;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.data.annotation.ContextData;
import com.astamuse.asta4d.render.GoThroughRenderer;
import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.test.render.infra.BaseTest;
import com.astamuse.asta4d.test.render.infra.SimpleCase;

public class InjectTest extends BaseTest {

    public static class TestRender {

        public Renderer methodDefaultSearch(String av, String pv, String cv, String gv) {
            Renderer render = Renderer.create("#av-value", av);
            render.add("#pv-value", pv);
            render.add("#cv-value", cv);
            render.add("#gv-value", gv);
            return render;
        }

        public Renderer methodScopeSearch(@ContextData(scope = Context.SCOPE_DEFAULT) String av,
                @ContextData(scope = Context.SCOPE_DEFAULT) String pv, @ContextData(scope = Context.SCOPE_GLOBAL) String cv,
                @ContextData(scope = Context.SCOPE_GLOBAL) String gv) {
            Renderer render = Renderer.create("#av-value", av);
            render.add("#pv-value", pv);
            render.add("#cv-value", cv);
            render.add("#gv-value", gv);
            return render;
        }

        public Renderer methodNameSearch(@ContextData(name = "av-r") String av, @ContextData(name = "cv-r") String cv,
                @ContextData(name = "gv-r") String gv) {
            Renderer render = Renderer.create("#av-value", av);
            render.add("#cv-value", cv);
            render.add("#gv-value", gv);
            return render;
        }

        public Renderer methodTypeConvertor(int intvalue, long longvalue, boolean boolvalue) {
            Renderer render = Renderer.create("#intvalue", intvalue);
            render.add("#longvalue", longvalue);
            render.add("#boolvalue", boolvalue);
            return render;
        }
    }

    public static class InstaneDefaultSearchRender {

        @ContextData
        private String cv;

        private String gv;

        public Renderer render() {
            Renderer render = new GoThroughRenderer();
            render.add("#cv-value", cv);
            render.add("#gv-value", gv);
            return render;
        }

        public String getGv() {
            return gv;
        }

        @ContextData
        public void setGv(String gv) {
            this.gv = gv;
        }

    }

    public static class InstaneScopeSearchRender {

        @ContextData(scope = Context.SCOPE_GLOBAL)
        private String cv;

        private String gv;

        public Renderer render() {
            Renderer render = new GoThroughRenderer();
            render.add("#cv-value", cv);
            render.add("#gv-value", gv);
            return render;
        }

        public String getGv() {
            return gv;
        }

        @ContextData(scope = Context.SCOPE_GLOBAL)
        public void setGv(String gv) {
            this.gv = gv;
        }

    }

    public static class InstaneNameSearchRender {

        @ContextData(name = "cv-r")
        private String cv;

        private String gv;

        public Renderer render() {
            Renderer render = new GoThroughRenderer();
            render.add("#cv-value", cv);
            render.add("#gv-value", gv);
            return render;
        }

        public String getGv() {
            return gv;
        }

        @ContextData(name = "gv-r")
        public void setGv(String gv) {
            this.gv = gv;
        }

    }

    public static class InstanceTypeConvertorRender {

        @ContextData
        private int intvalue;

        @ContextData
        private long longvalue;

        @ContextData
        private boolean boolvalue;

        public Renderer render() {
            Renderer render = Renderer.create("#intvalue", intvalue);
            render.add("#longvalue", longvalue);
            render.add("#boolvalue", boolvalue);
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

    public void testMethodScopeSearch() {
        Context context = Context.getCurrentThreadContext();
        context.setData(Context.SCOPE_DEFAULT, "av", "av-value at context");
        context.setData(Context.SCOPE_DEFAULT, "pv", "pv-value at context");
        context.setData(Context.SCOPE_DEFAULT, "cv", "cv-value at context");
        context.setData(Context.SCOPE_GLOBAL, "cv", "cv-value at global");
        context.setData(Context.SCOPE_DEFAULT, "gv", "gv-value at context");
        context.setData(Context.SCOPE_GLOBAL, "gv", "gv-value at global");
        new SimpleCase("Inject_testMethodScopeSearch.html");
    }

    public void testMethodNameSearch() {
        Context context = Context.getCurrentThreadContext();
        context.setData(Context.SCOPE_DEFAULT, "cv", "cv-value");
        context.setData(Context.SCOPE_DEFAULT, "cv-r", "cv-value for name replace");
        context.setData(Context.SCOPE_GLOBAL, "gv-r", "gv-value for name replace");
        context.setData(Context.SCOPE_GLOBAL, "gv", "gv-value");
        new SimpleCase("Inject_testMethodNameSearch.html");
    }

    public void testMethodTypeConvertor() {
        Context context = Context.getCurrentThreadContext();
        context.setData(Context.SCOPE_DEFAULT, "intvalue", "222");
        context.setData(Context.SCOPE_DEFAULT, "longvalue", "333");
        context.setData(Context.SCOPE_DEFAULT, "boolvalue", "false");
        new SimpleCase("Inject_testMethodTypeConvertor.html");
    }

    public void testInstanceDefaultSearch() {
        Context context = Context.getCurrentThreadContext();
        context.setData(Context.SCOPE_DEFAULT, "cv", "cv-value at context");
        context.setData(Context.SCOPE_GLOBAL, "cv", "cv-value at global");
        context.setData(Context.SCOPE_GLOBAL, "gv", "gv-value");
        new SimpleCase("Inject_testInstanceDefaultSearch.html");
    }

    public void testInstanceScopeSearch() {
        Context context = Context.getCurrentThreadContext();
        context.setData(Context.SCOPE_DEFAULT, "cv", "cv-value at context");
        context.setData(Context.SCOPE_GLOBAL, "cv", "cv-value at global");
        context.setData(Context.SCOPE_DEFAULT, "gv", "gv-value at context");
        context.setData(Context.SCOPE_GLOBAL, "gv", "gv-value at global");
        new SimpleCase("Inject_testInstanceScopeSearch.html");
    }

    public void testInstanceNameSearch() {
        Context context = Context.getCurrentThreadContext();
        context.setData(Context.SCOPE_DEFAULT, "cv", "cv-value");
        context.setData(Context.SCOPE_DEFAULT, "cv-r", "cv-value for name replace");
        context.setData(Context.SCOPE_GLOBAL, "gv", "gv-value");
        context.setData(Context.SCOPE_GLOBAL, "gv-r", "gv-value for name replace");
        new SimpleCase("Inject_testInstanceNameSearch.html");
    }

    public void testInstanceTypeConvertor() {
        Context context = Context.getCurrentThreadContext();
        context.setData(Context.SCOPE_DEFAULT, "intvalue", "111");
        context.setData(Context.SCOPE_DEFAULT, "longvalue", "555");
        context.setData(Context.SCOPE_DEFAULT, "boolvalue", "true");
        new SimpleCase("Inject_testInstanceTypeConvertor.html");
    }

}
