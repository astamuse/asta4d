package org.jsoupit.test;

import java.io.IOException;

import org.jsoupit.template.render.Renderer;
import org.jsoupit.test.infra.BaseTest;
import org.jsoupit.test.infra.SimpleCase;

public class SimpleSnippetRenderingTest extends BaseTest {

    public static class TagEmbed {
        public Renderer render() {
            return Renderer.create("span", "wow");
        }
    }

    public void testTagEmbed() throws IOException {
        new SimpleCase("SimpleSnippet_TagEmbed.html");
    }

    public void testSnippetTag() throws IOException {
        new SimpleCase("SimpleSnippet_SnippetTag.html");
    }
}
