package com.astamuse.asta4d.data.builtin;

import com.astamuse.asta4d.data.DataConvertor;

/**
 * Convert String to Integer
 * 
 * @author e-ryu
 * 
 */
public class String2Int implements DataConvertor<String, Integer> {

    @Override
    public Integer convert(String s) {
        return Integer.parseInt(s);
    }

}
