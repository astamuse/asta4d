package com.astamuse.asta4d.web.dispatch.request;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MultiResultHolder {

    private List<Object> resultList;

    public MultiResultHolder() {
        resultList = new LinkedList<>();
    }

    public void addResult(Object result) {
        resultList.add(result);
    }

    public List<Object> getResultList() {
        return Collections.unmodifiableList(resultList);
    }
}
