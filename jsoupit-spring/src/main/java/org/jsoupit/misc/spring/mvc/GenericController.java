package org.jsoupit.misc.spring.mvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.jsoupit.template.Context;
import org.jsoupit.web.WebApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
// @RequestMapping("/")
public class GenericController {

    private class ExtractedUriInfo {
        String templatePath = "";
        Map<String, String> params = new HashMap<>();
    }

    public GenericController() {
        System.out.println("generic controller created");
    }

    private AntPathMatcher pathMatcher = new AntPathMatcher();

    private String retrieveURI(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        return uri.substring(contextPath.length());
    }

    @RequestMapping(value = "/**", method = RequestMethod.GET)
    public String doGet(Model model, HttpServletRequest request) {
        String path = retrieveURI(request);
        ExtractedUriInfo info = extractURI(path);
        System.out.println("path=" + info.templatePath);
        System.out.println("params=" + buildParamString(info.params));

        WebApplicationContext context = (WebApplicationContext) Context.getCurrentThreadContext();
        Iterator<Entry<String, String>> it = info.params.entrySet().iterator();
        Entry<String, String> entry;
        while (it.hasNext()) {
            entry = it.next();
            context.setData(WebApplicationContext.SCOPE_PATHVAR, entry.getKey(), entry.getValue());
        }
        return info.templatePath;
    }

    private ExtractedUriInfo extractURI(String uri) {
        Map<String, String> uriMap = getURLMapping();
        List<Entry<String, String>> entries = new ArrayList<>(uriMap.entrySet());
        Collections.sort(entries, new Comparator<Entry<String, String>>() {
            @Override
            public int compare(Entry<String, String> e1, Entry<String, String> e2) {
                // TODO should we use unicode aware codePointCount
                // or should we use a more complex comparison logic?
                return e2.getKey().length() - e1.getKey().length();
            }
        });
        ExtractedUriInfo info = new ExtractedUriInfo();
        for (Entry<String, String> entry : entries) {
            if (pathMatcher.match(entry.getKey(), uri)) {
                info.params.putAll(pathMatcher.extractUriTemplateVariables(entry.getKey(), uri));
                info.templatePath = entry.getValue();
                break;
            }
        }

        return info;
    }

    public String buildParamString(Map<String, String> map) {
        return map.toString();
    }

    public Map<String, String> getURLMapping() {
        Map<String, String> sampleMap = new HashMap<String, String>() {
            {
                put("/", "/index.html");
                put("/tt", "/tt/index.html");
                put("/tt/{tid}", "/tt/showid.html");
                put("/tt/{tid}/ss", "/tt/ss/index.html");
                put("/a/{aid}/b/{bid}/c/{cid}/d/{did}", "/MyTestPage.html");
                put("/search/company", "/index_search.html");
                put("/search/company/q/{queryString}", "/search/result_company.html");
            }
        };
        return sampleMap;
    }

}
