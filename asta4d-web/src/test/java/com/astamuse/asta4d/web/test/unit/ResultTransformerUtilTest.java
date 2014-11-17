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
