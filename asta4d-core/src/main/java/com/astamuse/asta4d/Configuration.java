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

package com.astamuse.asta4d;

import java.util.ArrayList;
import java.util.List;

import com.astamuse.asta4d.data.ContextDataFinder;
import com.astamuse.asta4d.data.DataTypeTransformer;
import com.astamuse.asta4d.data.DefaultContextDataFinder;
import com.astamuse.asta4d.data.DefaultDataTypeTransformer;
import com.astamuse.asta4d.interceptor.PageInterceptor;
import com.astamuse.asta4d.snippet.DefaultSnippetInvoker;
import com.astamuse.asta4d.snippet.SnippetInvoker;
import com.astamuse.asta4d.snippet.extract.DefaultSnippetExtractor;
import com.astamuse.asta4d.snippet.extract.SnippetExtractor;
import com.astamuse.asta4d.snippet.resolve.DefaultSnippetResolver;
import com.astamuse.asta4d.snippet.resolve.SnippetResolver;
import com.astamuse.asta4d.template.FileTemplateResolver;
import com.astamuse.asta4d.template.TemplateResolver;
import com.astamuse.asta4d.util.collection.ParallelRecursivePolicy;
import com.astamuse.asta4d.util.concurrent.DefaultExecutorServiceFactory;
import com.astamuse.asta4d.util.concurrent.ExecutorServiceFactory;
import com.astamuse.asta4d.util.i18n.I18nMessageHelper;
import com.astamuse.asta4d.util.i18n.OrderedValueI18nMessageHelper;

public class Configuration {

    private TemplateResolver templateResolver = new FileTemplateResolver();

    private SnippetInvoker snippetInvoker = new DefaultSnippetInvoker();

    private SnippetResolver snippetResolver = new DefaultSnippetResolver();

    private SnippetExtractor snippetExtractor = new DefaultSnippetExtractor();

    private List<PageInterceptor> pageInterceptorList = new ArrayList<>();

    private ContextDataFinder contextDataFinder = new DefaultContextDataFinder();

    private DataTypeTransformer dataTypeTransformer = new DefaultDataTypeTransformer();

    private I18nMessageHelper i18nMessageHelper = new OrderedValueI18nMessageHelper();

    private boolean cacheEnable = true;

    private boolean skipSnippetExecution = false;

    private boolean outputAsPrettyPrint = false;

    private boolean blockParallelListRendering = false;

    private ExecutorServiceFactory snippetExecutorFactory = new DefaultExecutorServiceFactory("asta4d-snippet", 200);

    private ExecutorServiceFactory listExecutorFactory = new DefaultExecutorServiceFactory("asta4d-list", 600);

    private ParallelRecursivePolicy parallelRecursivePolicyForListRendering = ParallelRecursivePolicy.EXCEPTION;

    private List<String> clearNodeClasses = new ArrayList<>();

    private String tagNameSpace = "afd";

    private boolean saveCallstackInfoOnRendererCreation = false;

    private static Configuration instance;

    public final static Configuration getConfiguration() {
        return instance;
    }

    public final static void setConfiguration(Configuration configuration) {
        instance = configuration;
    }

    public TemplateResolver getTemplateResolver() {
        return templateResolver;
    }

    public void setTemplateResolver(TemplateResolver templateResolver) {
        this.templateResolver = templateResolver;
    }

    public SnippetInvoker getSnippetInvoker() {
        return snippetInvoker;
    }

    public void setSnippetInvoker(SnippetInvoker snippetInvoker) {
        this.snippetInvoker = snippetInvoker;
    }

    public SnippetResolver getSnippetResolver() {
        return snippetResolver;
    }

    public void setSnippetResolver(SnippetResolver snippetResolver) {
        this.snippetResolver = snippetResolver;
    }

    public SnippetExtractor getSnippetExtractor() {
        return snippetExtractor;
    }

    public void setSnippetExtractor(SnippetExtractor snippetExtractor) {
        this.snippetExtractor = snippetExtractor;
    }

    public List<PageInterceptor> getPageInterceptorList() {
        return pageInterceptorList;
    }

    public void setPageInterceptorList(List<PageInterceptor> pageInterceptorList) {
        this.pageInterceptorList = pageInterceptorList;
    }

    public ContextDataFinder getContextDataFinder() {
        return contextDataFinder;
    }

    public void setContextDataFinder(ContextDataFinder contextDataFinder) {
        this.contextDataFinder = contextDataFinder;
    }

    public DataTypeTransformer getDataTypeTransformer() {
        return dataTypeTransformer;
    }

    public void setDataTypeTransformer(DataTypeTransformer dataTypeTransformer) {
        this.dataTypeTransformer = dataTypeTransformer;
    }

    public I18nMessageHelper getI18nMessageHelper() {
        return i18nMessageHelper;
    }

    public void setI18nMessageHelper(I18nMessageHelper i18nMessageHelper) {
        this.i18nMessageHelper = i18nMessageHelper;
    }

    public boolean isCacheEnable() {
        return cacheEnable;
    }

    public void setCacheEnable(boolean cacheEnable) {
        this.cacheEnable = cacheEnable;
    }

    public boolean isSkipSnippetExecution() {
        return skipSnippetExecution;
    }

    public void setSkipSnippetExecution(boolean skipSnippetExecution) {
        this.skipSnippetExecution = skipSnippetExecution;
    }

    public ExecutorServiceFactory getSnippetExecutorFactory() {
        return snippetExecutorFactory;
    }

    public void setSnippetExecutorFactory(ExecutorServiceFactory snippetExecutorFactory) {
        this.snippetExecutorFactory = snippetExecutorFactory;
    }

    public ExecutorServiceFactory getListExecutorFactory() {
        return listExecutorFactory;
    }

    public void setListExecutorFactory(ExecutorServiceFactory listExecutorFactory) {
        this.listExecutorFactory = listExecutorFactory;
    }

    public ParallelRecursivePolicy getParallelRecursivePolicyForListRendering() {
        return parallelRecursivePolicyForListRendering;
    }

    public void setParallelRecursivePolicyForListRendering(ParallelRecursivePolicy parallelRecursivePolicyForListRendering) {
        this.parallelRecursivePolicyForListRendering = parallelRecursivePolicyForListRendering;
    }

    public boolean isOutputAsPrettyPrint() {
        return outputAsPrettyPrint;
    }

    public void setOutputAsPrettyPrint(boolean outputAsPrettyPrint) {
        this.outputAsPrettyPrint = outputAsPrettyPrint;
    }

    public boolean isBlockParallelListRendering() {
        return blockParallelListRendering;
    }

    public void setBlockParallelListRendering(boolean blockParallelListRendering) {
        this.blockParallelListRendering = blockParallelListRendering;
    }

    public List<String> getClearNodeClasses() {
        return clearNodeClasses;
    }

    public void setClearNodeClasses(List<String> clearNodeClasses) {
        this.clearNodeClasses = clearNodeClasses;
    }

    public String getTagNameSpace() {
        return tagNameSpace;
    }

    public void setTagNameSpace(String tagNameSpace) {
        this.tagNameSpace = tagNameSpace;
    }

    public boolean isSaveCallstackInfoOnRendererCreation() {
        return saveCallstackInfoOnRendererCreation;
    }

    public void setSaveCallstackInfoOnRendererCreation(boolean saveCallstackInfoOnRendererCreation) {
        this.saveCallstackInfoOnRendererCreation = saveCallstackInfoOnRendererCreation;
    }

}
