package org.jsoupit.test;

import java.io.IOException;

import org.jsoupit.test.infra.BaseTest;
import org.jsoupit.test.infra.SimpleCase;
import org.junit.Test;

public class TemplateExtraction extends BaseTest {

    @Test
    public void loadTempateWithInjection() throws IOException {
        new SimpleCase("TemplateWithInjection.html");
    }

    @Test
    public void loadTempateWithEmbed() throws IOException {

        new SimpleCase("TemplateWithEmbed.html");
    }
}
