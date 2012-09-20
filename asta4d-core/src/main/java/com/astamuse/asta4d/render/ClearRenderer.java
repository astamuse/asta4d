package com.astamuse.asta4d.render;

import com.astamuse.asta4d.extnode.ExtNodeConstants;

public class ClearRenderer extends AttriuteSetter {

    public ClearRenderer() {
        super(ExtNodeConstants.ATTR_CLEAR_WITH_NS, "");
    }

    @Override
    public String toString() {
        return "ClearRenderer";
    }
}
