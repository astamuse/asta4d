package com.astamuse.asta4d.data.builtin;

import com.astamuse.asta4d.data.DataConvertor;

public class String2Long implements DataConvertor<String, Long> {

    @Override
    public Long convert(String s) {
        return Long.parseLong(s);
    }

}
