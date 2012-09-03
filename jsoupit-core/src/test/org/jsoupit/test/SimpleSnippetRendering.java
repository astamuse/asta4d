package org.jsoupit.test;

import java.io.IOException;

import org.jsoupit.template.render.Renderer;
import org.jsoupit.test.infra.BaseTest;
import org.jsoupit.test.infra.SimpleCase;
import org.junit.Test;

public class SimpleSnippetRendering extends BaseTest {

    public static class TagEmbed {
        public Renderer render() {
            return Renderer.create("span", "wow");
        }
    }

    @Test
    public void testTagEmbed() throws IOException {
        new SimpleCase("SimpleSnippet_TagEmbed.html");
    }

    @Test
    public void testSnippetTag() throws IOException {
        new SimpleCase("SimpleSnippet_SnippetTag.html");
    }
}
