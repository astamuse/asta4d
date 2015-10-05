/*
 * Copyright 2015 astamuse company,Ltd.
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
package com.astamuse.asta4d.util.collection;

/**
 * 
 * The original {@link RowConvertor} requires the parameters order as (int, Object) which may confuse developers when using lambda express.
 * This interface affords an alternative parameters order with (Object, int) to make things simpler.
 * 
 * @author e-ryu
 *
 * @param <S>
 * @param <T>
 */
@FunctionalInterface
public interface AlternativeRowConvertor<S, T> {

    public T convert(S obj, int rowIndex);

    default public boolean isParallel() {
        return false;
    }

    default RowConvertor<S, T> toRowConvertor() {
        AlternativeRowConvertor<S, T> self = this;
        return new RowConvertor<S, T>() {
            @Override
            public T convert(int rowIndex, S obj) {
                return self.convert(obj, rowIndex);
            }

            public boolean isParallel() {
                return self.isParallel();
            }
        };
    }
}
