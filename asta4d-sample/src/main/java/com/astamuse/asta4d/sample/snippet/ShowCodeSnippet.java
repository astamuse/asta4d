/*
 * Copyright 2012 astamuse company,Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.astamuse.asta4d.sample.snippet;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

import com.astamuse.asta4d.render.GoThroughRenderer;
import com.astamuse.asta4d.render.Renderer;

public class ShowCodeSnippet {
    private static final String JAVA_PACKAGE = "/com/astamuse/asta4d/sample";
    private static final String VM_ARG = "asta4d.sample.source_location";
    private static final String SHOW_MARK = "@ShowCode:";

    public Renderer showCode(HttpServletRequest request, String file, String startMark, String endMark, String title) {
        Renderer render = new GoThroughRenderer();
        ServletContext servletContext = request.getSession().getServletContext();
        String contents = readFileByLines(servletContext, file, SHOW_MARK + startMark, SHOW_MARK + endMark);
        render.add("div", makeShowHtml(file, title, contents));

        return render;
    }

    private Element makeShowHtml(String file, String title, String contents) {

        // create the panel tag
        Element panel = new Element(Tag.valueOf("div"), "");
        panel.addClass("panel");
        panel.addClass("panel-default");

        Element heading = new Element(Tag.valueOf("div"), "");
        heading.addClass("panel-heading");

        Element body = new Element(Tag.valueOf("div"), "");
        body.addClass("panel-body");

        panel.appendChild(heading);
        panel.appendChild(body);

        // write title and file path
        String headStr = StringUtils.isEmpty(title) ? "" : title + ":";
        headStr += file;
        heading.appendText(headStr);

        // create the pre tag
        Element pre = new Element(Tag.valueOf("pre"), "");
        pre.addClass("prettyprint source");
        pre.attr("style", "overflow-x:auto");
        if (contents != null) {
            pre.appendChild(new Element(Tag.valueOf("span"), "").appendText(contents));
        }
        body.appendChild(pre);
        return panel;
    }

    private static String readFileByLines(ServletContext servletContext, String fileName, String startMark, String endMark) {
        String filePath = "";
        InputStream inputStream = null;
        BufferedReader reader = null;
        try {
            // read the file
            if (fileName.endsWith(".java")) {
                String source_location = System.getProperty(VM_ARG);
                if (source_location != null) {
                    filePath = source_location + JAVA_PACKAGE + fileName;
                    inputStream = new FileInputStream(filePath);
                } else {
                    filePath = "/WEB-INF/src" + JAVA_PACKAGE + fileName;
                    inputStream = servletContext.getResourceAsStream(filePath);
                }
            } else {
                inputStream = servletContext.getResourceAsStream(fileName);
            }

            if (inputStream == null) {
                return null;
            }

            // find the line that has the mark
            reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
            String line = null;
            String contents = "";
            int markStart = -1;
            while ((line = reader.readLine()) != null) {
                if (line.contains(endMark)) {
                    break;
                }
                if (markStart >= 0 && !line.contains(SHOW_MARK)) {
                    if (line.length() <= markStart) {
                        line = "";
                    } else {
                        line = line.substring(markStart);
                    }
                    contents = contents + line + "\n";
                }

                if (line.contains(startMark)) {
                    String trim = line.trim();
                    markStart = line.indexOf(trim);
                }
            }

            return contents;
        } catch (IOException e) {
            return null;

            // close
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e1) {
                }
            }
        }
    }
}
