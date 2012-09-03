package org.jsoupit.test;

import java.io.IOException;

import org.jsoupit.test.infra.BaseTest;
import org.jsoupit.test.infra.SimpleCase;

public class TemplateExtractionTest extends BaseTest {

    public void loadTempateWithInjection() throws IOException {
        new SimpleCase("TemplateWithInjection.html");
    }

    public void loadTempateWithEmbed() throws IOException {
        new SimpleCase("TemplateWithEmbed.html");
    }
}
