/*
 * Copyright 2012 astamuse company,Ltd.
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

package com.astamuse.asta4d.web.dispatch.request;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.astamuse.asta4d.web.dispatch.response.provider.ContentProvider;
import com.astamuse.asta4d.web.dispatch.response.provider.SerialProvider;

public class ResultTransformerUtil {

    private final static Logger logger = LoggerFactory.getLogger(ResultTransformerUtil.class);

    public final static ContentProvider transform(Object result, List<ResultTransformer> transformerList) {

        if (result instanceof ContentProvider) {
            return (ContentProvider) result;
        }

        ContentProvider cp = null;
        Object before, after;
        before = result;
        ResultTransformer resultTransformer;
        int size = transformerList.size();
        for (int i = 0; i < size; i++) {

            if (before instanceof MultiResultHolder) {
                List<ResultTransformer> subList = transformerList.subList(i, size);
                return transformMultiResult((MultiResultHolder) before, subList);
            }

            resultTransformer = transformerList.get(i);
            try {
                after = resultTransformer.transformToContentProvider(before);
                if (after instanceof Exception) {
                    logger.error("Error occured on result transform.", (Exception) after);
                }
            } catch (Exception ex) {
                logger.error("Error occured on result transform.", ex);
                after = ex;
            }

            if (after == null) {
                continue;
            } else if (after instanceof ContentProvider) {
                cp = (ContentProvider) after;
                break;
            } else {
                before = after;
                continue;
            }
        }
        if (cp == null) {
            if (result == null) {
                String msg = "Cannot recognize the result null. Maybe a default ResultTransformer is neccessory(Usually a non result default forward/rediredt declaration of current url rule is missing).";
                throw new UnsupportedOperationException(msg);
            } else {
                String msg = "Cannot recognize the result :[%s:%s]. Maybe a ResultTransformer is neccessory(Usually a result specified forward/rediredt declaration of current url rule is missing).";
                msg = String.format(msg, result.getClass().getName(), result.toString());
                throw new UnsupportedOperationException(msg);
            }
        } else {
            return cp;
        }
    }

    private final static ContentProvider transformMultiResult(MultiResultHolder resultHolder, List<ResultTransformer> transformerList) {
        List<Object> resultList = resultHolder.getResultList();
        if (resultList == null || resultList.isEmpty()) {
            String msg = "MultiResultHolder should must hold some result but we got one with an empty list.";
            throw new UnsupportedOperationException(msg);
        }

        List<ContentProvider> cpList = new ArrayList<>(resultList.size());
        for (Object object : resultList) {
            cpList.add(transform(object, transformerList));
        }

        ContentProvider sp = new SerialProvider(cpList);
        return sp;
    }
}
