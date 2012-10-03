package com.astamuse.asta4d.test.render;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.data.annotation.ContextData;
import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.snippet.InitializableSnippet;
import com.astamuse.asta4d.snippet.SnippetInvokeException;
import com.astamuse.asta4d.test.render.infra.BaseTest;
import com.astamuse.asta4d.test.render.infra.SimpleCase;

public class SimpleSnippetRenderingTest extends BaseTest {

    public static class TagEmbed {
        public Renderer render(String ctype) {
            return Renderer.create("span", ctype);
        }
    }

    public static class SnippetTag {
        public Renderer render() {
            return Renderer.create("span", "wow");
        }
    }

    public static class InitSnippet implements InitializableSnippet {

        @ContextData
        private String value;

        private String resolvedValue;

        private int count = 0;

        @Override
        public void init() throws SnippetInvokeException {
            resolvedValue = value + "-resolved";
            count++;
        }

        public Renderer render_1() {
            return Renderer.create(".value", resolvedValue).add(".count", count);
        }

        public Renderer render_2() {
            return Renderer.create(".value", resolvedValue).add(".count", count);
        }
    }

    public void testTagEmbed() {
        new SimpleCase("SimpleSnippet_TagEmbed.html");
    }

    public void testSnippetTag() {
        new SimpleCase("SimpleSnippet_SnippetTag.html");
    }

    public void testBasePackageSnippetSearch() {
        new SimpleCase("SimpleSnippet_BasePackage.html");
    }

    public void testSnippetInit() {
        Context.getCurrentThreadContext().setData("value", "fire");
        new SimpleCase("SimpleSnippet_SnippetInit.html");
    }

}
