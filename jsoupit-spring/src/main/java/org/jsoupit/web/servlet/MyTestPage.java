package org.jsoupit.web.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoupit.template.RenderUtil;
import org.jsoupit.template.render.ElementSetter;
import org.jsoupit.template.render.EmptyRenderer;
import org.jsoupit.template.render.GoThroughRenderer;
import org.jsoupit.template.render.Renderer;

public class MyTestPage {

    public MyTestPage() {
    }

    public Renderer render() {
        // do nothing renderer
        Renderer renderer = new GoThroughRenderer();

        // simple text rendering
        renderer.add("h1", "TEST SE");

        List<String> items = new ArrayList<String>();
        items.add("item 1");
        items.add("item 2");
        items.add("item 3");

        // list rendering
        renderer.add("li", items);

        // recursive rendering
        Renderer pr = Renderer.create("p", "here we are");
        renderer.add("h2", pr);

        // remove element
        renderer.add("h3 p", new EmptyRenderer());

        // set attr
        renderer.add("a#tt", "href", "http://www.com");
        renderer.add("a#tt", "www.com");

        // set class
        renderer.add("a#tt", "+class", "redlink");
        renderer.add("a#tt", "+class", "blink");

        final int renderFlag = 1;

        // retrieve value from element and customized rendering
        renderer.add("p#vv", new ElementSetter() {
            @Override
            public void set(Element elem) {
                String attr = renderFlag == 1 ? "v1" : "v2";
                String value = elem.attr(attr);
                elem.appendText(value);
            }
        });

        // render customized Element
        Element textArea = new Element(Tag.valueOf("textArea"), "");
        textArea.appendText("edit me");
        renderer.add("#editor", textArea);

        return renderer;
    }

    public void output(OutputStream output) throws IOException {
        InputStream input = MyTestPage.class.getResourceAsStream("MyTestPage.html");
        Document doc = Jsoup.parse(input, "UTF-8", "");
        RenderUtil.apply(doc, render());
        output.write(doc.outerHtml().getBytes("UTF-8"));
    }

}
