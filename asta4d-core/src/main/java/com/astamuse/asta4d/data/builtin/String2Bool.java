package com.astamuse.asta4d.data.builtin;

import com.astamuse.asta4d.data.ArrayDataConvertor;

/**
 * Convert String to Boolean
 * 
 * @author e-ryu
 * 
 */
public class String2Bool extends ArrayDataConvertor<String, Boolean> {

    @Override
    public Boolean convert(String s) {
        if (s == null) {
            return false;
        } else {
            return Boolean.parseBoolean(s);
        }
    }

}
