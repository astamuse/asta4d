package com.astamuse.asta4d.web.dispatch.mapping;

import java.util.List;

import com.astamuse.asta4d.web.dispatch.request.ResultTransformer;

public interface ResultTransformerReorganizer {
    public List<ResultTransformer> reorganize(List<ResultTransformer> transformerList);
}
