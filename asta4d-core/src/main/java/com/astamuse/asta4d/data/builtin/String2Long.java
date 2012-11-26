package com.astamuse.asta4d.data.builtin;

import com.astamuse.asta4d.data.ArrayDataConvertor;

/**
 * Convert String to Long
 * 
 * @author e-ryu
 * 
 */
public class String2Long extends ArrayDataConvertor<String, Long> {

    @Override
    public Long convert(String s) {
        return Long.parseLong(s);
    }

}
