package com.astamuse.asta4d.sample.handler.form;

import com.astamuse.asta4d.data.annotation.ContextDataSet;
import com.astamuse.asta4d.web.annotation.QueryParam;

@ContextDataSet
public class ExtraInfo {
    @QueryParam
    String action;
    @QueryParam
    Integer id;
}