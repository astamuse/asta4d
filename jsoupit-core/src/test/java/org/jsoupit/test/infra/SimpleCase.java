package org.jsoupit.test.infra;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoupit.Page;
import org.testng.Assert;

public class SimpleCase {

    public SimpleCase(String templateFileName) throws IOException {
        Page originPage = null;
        Page resultPage = null;
        try {
            originPage = new Page("/org/jsoupit/test/templates/" + templateFileName);
            resultPage = new Page("/org/jsoupit/test/confirms/" + templateFileName);

            String ostr = revert2comparableString(originPage);
            String rstr = revert2comparableString(resultPage);

            Assert.assertEquals(ostr, rstr);

        } catch (Throwable t) {
            output(templateFileName + ":rendering result", originPage);
            output(templateFileName + ":expected result", resultPage);
            throw new RuntimeException("verify failed", t);
        }
    }

    private String revert2comparableString(Page page) {
        String s = page.output();
        Document doc = Jsoup.parse(s);
        return doc.outerHtml();
    }

    private void output(String title, Page page) {
        System.out.println(String.format("===============%s===================", title));
        if (page == null) {
            System.out.println("##null##");
        } else {
            System.out.println(page.output());
        }
        System.out.println("===============================================================");
    }

}
