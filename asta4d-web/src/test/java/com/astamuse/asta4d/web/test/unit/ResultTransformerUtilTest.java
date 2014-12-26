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
package com.astamuse.asta4d.web.test.unit;

import java.util.LinkedList;

import org.testng.annotations.Test;

import com.astamuse.asta4d.web.dispatch.request.ResultTransformer;
import com.astamuse.asta4d.web.dispatch.request.ResultTransformerUtil;

@Test
public class ResultTransformerUtilTest {

    private static final String MSG_NULL_RESULT = "Cannot recognize the result null\\. "
            + "Maybe a default ResultTransformer is neccessory"
            + "\\(Usually a non result default forward/rediredt declaration of current url rule is missing\\)\\.";

    @Test(expectedExceptions = UnsupportedOperationException.class, expectedExceptionsMessageRegExp = MSG_NULL_RESULT)
    public void testNullResult() {
        ResultTransformerUtil.transform(null, new LinkedList<ResultTransformer>());
    }

    private static final String MSG_NO_TRANSFORMER = "Cannot recognize the result :\\[java\\.lang\\.String:xxx\\]\\. "
            + "Maybe a ResultTransformer is neccessory"
            + "\\(Usually a result specified forward/rediredt declaration of current url rule is missing\\)\\.";

    @Test(expectedExceptions = UnsupportedOperationException.class, expectedExceptionsMessageRegExp = MSG_NO_TRANSFORMER)
    public void testNoTransformer() {
        ResultTransformerUtil.transform("xxx", new LinkedList<ResultTransformer>());
    }

}
