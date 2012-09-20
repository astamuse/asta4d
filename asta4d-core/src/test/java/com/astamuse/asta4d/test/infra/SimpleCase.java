package com.astamuse.asta4d.test.infra;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.testng.Assert;

import com.astamuse.asta4d.Page;

public class SimpleCase {

    public SimpleCase(String templateFileName) {
        Page originPage = null;
        Page resultPage = null;
        String ostr = null;
        String rstr = null;
        try {
            originPage = new Page("/com/astamuse/asta4d/test/templates/" + templateFileName);
            ostr = revert2comparableString(originPage);

            resultPage = new Page("/com/astamuse/asta4d/test/confirms/" + templateFileName);
            rstr = revert2comparableString(resultPage);

            Assert.assertEquals(ostr, rstr);

        } catch (Throwable t) {
            output(templateFileName + ":rendering result", ostr);
            output(templateFileName + ":expected result", rstr);
            throw new RuntimeException("verify failed", t);
        }
    }

    private String revert2comparableString(Page page) {
        String s = page.output();
        Document doc = Jsoup.parse(s);
        return doc.outerHtml();
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
