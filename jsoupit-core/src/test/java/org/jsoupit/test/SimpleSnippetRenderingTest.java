package org.jsoupit.test;

import org.jsoupit.template.render.Renderer;
import org.jsoupit.test.infra.BaseTest;
import org.jsoupit.test.infra.SimpleCase;

public class SimpleSnippetRenderingTest extends BaseTest {

    public static class TagEmbed {
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
