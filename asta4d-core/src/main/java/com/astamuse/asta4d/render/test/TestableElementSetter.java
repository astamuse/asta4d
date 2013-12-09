package com.astamuse.asta4d.render.test;

import com.astamuse.asta4d.render.ElementSetter;

public interface TestableElementSetter extends ElementSetter {
    /**
     * return a testable value for test purpose
     * 
     * @return
     */
    public Object retrieveTestableData();
}
