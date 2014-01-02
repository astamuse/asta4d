package com.astamuse.asta4d.render.test;

import com.astamuse.asta4d.render.ElementSetter;
import com.astamuse.asta4d.render.transformer.Transformer;

/**
 * This interface is used by {@link RendererTester} to retrieve the testable data from a {@link Transformer}.<br>
 * 
 * A content that passed to transformer can implement this interface too. Especially for customized implementations of {@link ElementSetter}
 * , implementing this interface can afford better testability.
 * 
 * @author e-ryu
 * 
 */
public interface TestableRendering {
    /**
     * return a testable value for test purpose
     * 
     * @return
     */
    public Object retrieveTestableData();
}
