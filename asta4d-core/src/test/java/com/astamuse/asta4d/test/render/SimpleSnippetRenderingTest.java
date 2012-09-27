package com.astamuse.asta4d.test.render;


import com.astamuse.asta4d.render.Renderer;
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

    public void testTagEmbed() {
        new SimpleCase("SimpleSnippet_TagEmbed.html");
    }

    public void testSnippetTag() {
        new SimpleCase("SimpleSnippet_SnippetTag.html");
    }

    public void testBasePackageSnippetSearch() {
        new SimpleCase("SimpleSnippet_BasePackage.html");
    }

}
