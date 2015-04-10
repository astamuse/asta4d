package com.astamuse.asta4d.util;

import java.lang.reflect.AccessibleObject;

import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.Paranamer;

public class Java8ParanamerBytecodeScanWrapper extends BytecodeReadingParanamer {

    public Java8ParanamerBytecodeScanWrapper() {
        super();
    }

    @Override
    public String[] lookupParameterNames(AccessibleObject methodOrConstructor) {
        return lookupParameterNames(methodOrConstructor, true);
    }

    @Override
    public String[] lookupParameterNames(AccessibleObject methodOrConstructor, boolean throwExceptionIfMissing) {
        try {
            return super.lookupParameterNames(methodOrConstructor, throwExceptionIfMissing);
        } catch (Exception ex) {
            return Paranamer.EMPTY_NAMES;
        }
    }

}
