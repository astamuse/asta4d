package com.astamuse.asta4d.web.dispatch.mapping.handy.rest;

import com.astamuse.asta4d.web.dispatch.request.ResultTransformer;
import com.astamuse.asta4d.web.dispatch.request.transformer.DefaultExceptionTransformer;
import com.astamuse.asta4d.web.dispatch.request.transformer.DefaultXmlTransformer;

public class XmlSupportRuleHelper {

    static ResultTransformer registeredTransformer = null;

    static final ResultTransformer FallbackXmlTransformer = new DefaultXmlTransformer();

    static final ResultTransformer ExceptionTransformer = new DefaultExceptionTransformer();

}
