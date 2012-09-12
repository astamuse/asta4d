package org.jsoupit.test;

import org.jsoupit.test.infra.BaseTest;
import org.jsoupit.test.infra.SimpleCase;

public class TemplateExtractionTest extends BaseTest {

    public void loadTempateWithExtension() {
        new SimpleCase("TemplateWithExtension.html");
    }

    public void loadTempateWithEmbed() {
        new SimpleCase("TemplateWithEmbed.html");
    }

    public void loadTempateWithEmbedBaseFolder() {
        new SimpleCase("TemplateWithEmbedBaseFolder.html");
    }

    public void loadTempateWithExtensionAndEmbedMerge() {
        new SimpleCase("TemplateWithExtensionAndEmbedMerge.html");
    }
}
