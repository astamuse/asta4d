package com.astamuse.asta4d.test.infra;

import java.io.IOException;
import java.io.InputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.testng.Assert;

import com.astamuse.asta4d.Page;

public class SimpleCase {

    public SimpleCase(String templateFileName) {
        this(templateFileName, templateFileName);
    }

    public SimpleCase(String templateFileName, String confirmFileName) {
        String ostr = null;
        String cstr = null;
        try {
            Page originPage = new Page("/com/astamuse/asta4d/test/templates/" + templateFileName);
            ostr = revert2comparableString(originPage);

            cstr = revert2comparableString("/com/astamuse/asta4d/test/confirms/" + confirmFileName);

            Assert.assertEquals(ostr, cstr);

        } catch (Throwable t) {
            output(templateFileName + ":rendering result", ostr);
            output(confirmFileName + ":expected result", cstr);
            throw new RuntimeException("verify failed", t);
        }
    }

    private String revert2comparableString(Page page) {
        String s = page.output();
        Document doc = Jsoup.parse(s);
        return doc.outerHtml();
    }

    private String revert2comparableString(String path) throws IOException {
        try (InputStream in = SimpleCase.class.getResourceAsStream(path)) {
            Document doc = Jsoup.parse(in, "utf-8", "");
            return doc.outerHtml();
        }
    }

    private void output(String title, String page) {
        System.out.println(String.format("===============%s===================", title));
        if (page == null) {
            System.out.println("##null##");
        } else {
            System.out.println(page);
        }
        System.out.println("===============================================================");
    }

}
