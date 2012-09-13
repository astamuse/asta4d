package com.astamuse.asta4d.data.adapter;

import com.astamuse.asta4d.data.DataConvertor;

public class String2Int implements DataConvertor<String, Integer> {

    @Override
    public Integer convert(String s) {
        return Integer.parseInt(s);
    }

}
