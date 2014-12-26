/*
 * Copyright 2014 astamuse company,Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
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
