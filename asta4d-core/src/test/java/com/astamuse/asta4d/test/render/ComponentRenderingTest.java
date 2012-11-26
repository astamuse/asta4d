package com.astamuse.asta4d.test.render;

import com.astamuse.asta4d.Component;
import com.astamuse.asta4d.Component.AttributesRequire;
import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.test.render.infra.BaseTest;
import com.astamuse.asta4d.test.render.infra.SimpleCase;

public class ComponentRenderingTest extends BaseTest {

    public static class Page {
        public Renderer render(final String ctype) throws Exception {
            return Renderer.create("span", new Component("/ComponentRenderingTest_component.html", new AttributesRequire() {
                @Override
                protected void prepareAttributes() {
                    this.add("value", ctype);
                }

            }).toElement());
        }
    }

    public static class Comp {
        public Renderer render(final String value) throws Exception {
            return Renderer.create(".value", value);
        }
    }

    public void testComponent() {
        new SimpleCase("ComponentRenderingTest.html");
    }

}
