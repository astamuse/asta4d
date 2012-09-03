package org.jsoupit.misc.spring.mvc.controller;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.jsoupit.misc.spring.mvc.controller.annotation.PathVarRewrite;
import org.jsoupit.misc.spring.mvc.controller.annotation.SubController;
import org.jsoupit.template.Context;
import org.jsoupit.web.WebApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

//TODO need to cache the mapped result
@Controller
public abstract class GenericControllerBase extends UrlMappingRuleHolder {

    private AntPathMatcher pathMatcher = new AntPathMatcher();

    private class ExtractedUriInfo {
        MappingRule rule;
        Map<String, String> params = new HashMap<>();
    }

    public GenericControllerBase() {

    }

    @RequestMapping(value = "/**")
    public String doService(Model model, HttpServletRequest request) throws Exception {
        ExtractedUriInfo info = extractURI(request);
        WebApplicationContext context = (WebApplicationContext) Context.getCurrentThreadContext();
        processPathVar(context, info.params, info.rule.getPathVarRewritter());
        return invokeController(context, info.rule.getController());
    }

    private ExtractedUriInfo extractURI(HttpServletRequest request) throws UnsupportedEncodingException {
        // TODO maybe we need to judge the encoding via header info?
        String uri = URLDecoder.decode(request.getRequestURI(), "UTF-8");
        String contextPath = request.getContextPath();
        uri = uri.substring(contextPath.length());

        RequestMethod method = RequestMethod.valueOf(request.getMethod());

        List<MappingRule> rules = getSortedRuleList();

        ExtractedUriInfo info = new ExtractedUriInfo();
        String srcUrl;
        for (MappingRule rule : rules) {
            // TODO we need support all method matching
            if (method != rule.getMethod()) {
                continue;
            }
            srcUrl = rule.getSourceUrl();
            if (pathMatcher.match(srcUrl, uri)) {
                info.params.putAll(pathMatcher.extractUriTemplateVariables(srcUrl, uri));
                info.rule = rule;
                break;
            }
        }

        return info;
    }

    private void processPathVar(WebApplicationContext context, Map<String, String> pathVarMap, Object pathVarRewritter)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        if (pathVarRewritter != null) {
            Method[] methodList = pathVarRewritter.getClass()
                    .getMethods();
            for (Method method : methodList) {
                if (method.isAnnotationPresent(PathVarRewrite.class)) {
                    method.invoke(pathVarRewritter, pathVarMap);
                    break;
                }
            }
        }

        Iterator<Entry<String, String>> it = pathVarMap.entrySet()
                .iterator();
        Entry<String, String> entry;
        while (it.hasNext()) {
            entry = it.next();
            context.setData(WebApplicationContext.SCOPE_PATHVAR, entry.getKey(), entry.getValue());
        }
    }

    private String invokeController(WebApplicationContext context, Object controller) throws InvocationTargetException,
            IllegalAccessException, IllegalArgumentException {
        Method[] methodList = controller.getClass()
                .getMethods();
        Method m = null;
        for (Method method : methodList) {
            if (method.isAnnotationPresent(SubController.class)) {
                m = method;
                break;
            }
        }

        if (m == null) {
            throw new InvocationTargetException(new RuntimeException("Controller method not found:" + controller.getClass()
                    .getName()));
        }

        // TODO param injection
        Object result = m.invoke(controller);
        return result.toString();
    }

}
