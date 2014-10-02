package com.astamuse.asta4d.web.test.form.field;

import org.testng.annotations.Test;

import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.web.form.field.impl.HiddenRenderer;
import com.astamuse.asta4d.web.test.WebTestBase;
import com.astamuse.asta4d.web.test.form.FormRenderCase;

public class HiddenTest extends WebTestBase {
    public static class TestSnippet {
        public Renderer normalEditValue() {
            FieldRenderBuilder builder = FieldRenderBuilder.of(HiddenRenderer.class);
            builder.addValue("nullvalue", null);
            builder.addValue("emptyvalue", "");
            builder.addValue("xvalue", "x");
            return builder.toRenderer(true);
        }

        public Renderer normalDisplayValue() {
            FieldRenderBuilder builder = FieldRenderBuilder.of(HiddenRenderer.class);
            builder.addValue("nullvalue", null);
            builder.addValue("emptyvalue", "");
            builder.addValue("xvalue", "x");
            return builder.toRenderer(false);
        }
    }

    @Test
    public void test() {
        new FormRenderCase("/Hidden.html");
    }
}
