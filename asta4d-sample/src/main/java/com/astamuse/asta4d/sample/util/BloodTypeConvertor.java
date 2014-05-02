package com.astamuse.asta4d.sample.util;

import com.astamuse.asta4d.data.convertor.DataConvertor;
import com.astamuse.asta4d.sample.newform.MyForm.BloodType;

public class BloodTypeConvertor implements DataConvertor<String, BloodType> {

    @Override
    public BloodType convert(String obj) {
        return BloodType.valueOf(obj.toUpperCase());
    }

}
