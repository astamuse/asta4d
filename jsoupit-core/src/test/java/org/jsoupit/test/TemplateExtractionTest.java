package org.jsoupit.test;

import org.jsoupit.test.infra.BaseTest;
import org.jsoupit.test.infra.SimpleCase;

public class TemplateExtractionTest extends BaseTest {

    public void loadTempateWithInjection() {
        new SimpleCase("TemplateWithInjection.html");
    }

    public void loadTempateWithEmbed() {
        new SimpleCase("TemplateWithEmbed.html");
    }
}
