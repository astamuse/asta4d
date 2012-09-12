package com.astamuse.asta4d.test;

import com.astamuse.asta4d.test.infra.BaseTest;
import com.astamuse.asta4d.test.infra.SimpleCase;

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
