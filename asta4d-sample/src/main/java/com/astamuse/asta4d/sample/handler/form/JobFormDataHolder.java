package com.astamuse.asta4d.sample.handler.form;

import com.astamuse.asta4d.data.annotation.ContextDataSet;
import com.astamuse.asta4d.web.annotation.QueryParam;

@ContextDataSet
public class JobFormDataHolder {

    @QueryParam
    private Integer[] year;

    @QueryParam
    private String[] description;

    public Integer[] getYear() {
        return year;
    }

    public void setYear(Integer[] year) {
        this.year = year;
    }

    public String[] getDescription() {
        return description;
    }

    public void setDescription(String[] description) {
        this.description = description;
    }

}
