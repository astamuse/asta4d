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

package com.astamuse.asta4d.data;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.astamuse.asta4d.Configuration;
import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.util.i18n.ParamMapResourceBundleHelper;
import com.astamuse.asta4d.util.i18n.ResourceBundleHelper;

/**
 * A default implementation of {@link ContextDataFinder}. It will search data in a given order (if scope is not specified) and try to apply
 * predefined {@link ContextDataFinder} list to convert data to appropriate type.
 * 
 * @author e-ryu
 * 
 */
public class DefaultContextDataFinder implements ContextDataFinder {

    private final static String ByTypeScope = DefaultContextDataFinder.class.getName() + "#findByType";

    private List<String> dataSearchScopeOrder = getDefaultScopeOrder();

    private final static List<String> getDefaultScopeOrder() {
        List<String> list = new ArrayList<>();
        list.add(Context.SCOPE_ATTR);
        list.add(Context.SCOPE_DEFAULT);
        list.add(Context.SCOPE_GLOBAL);
        return list;
    }

    public List<String> getDataSearchScopeOrder() {
        return dataSearchScopeOrder;
    }

    public void setDataSearchScopeOrder(List<String> dataSearchScopeOrder) {
        this.dataSearchScopeOrder = dataSearchScopeOrder;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public ContextDataHolder findDataInContext(Context context, String scope, String name, Class<?> targetType)
            throws DataOperationException {
        ContextDataHolder dataHolder = findByType(context, scope, name, targetType);

        if (dataHolder != null) {
            return dataHolder;
        }

        if (StringUtils.isEmpty(scope)) {
            dataHolder = findDataByScopeOrder(context, 0, name);
        } else {
            dataHolder = context.getDataHolder(scope, name);
        }

        if (dataHolder == null) {
            return null;
        }

        Object data = dataHolder.getValue();
        DataConvertorInvoker dataConvertorInvoker = Configuration.getConfiguration().getDataConvertorInvoker();
        dataHolder.setValue(dataHolder.getScope(), dataHolder.getName(), dataConvertorInvoker.convert(data, targetType));
        return dataHolder;
    }

    @SuppressWarnings("rawtypes")
    private ContextDataHolder findByType(Context context, String scope, String name, Class<?> targetType) {
        if (Context.class.isAssignableFrom(targetType)) {
            return new ContextDataHolder<>(Context.class.getName(), ByTypeScope, context);
        }

        if (targetType.equals(ResourceBundleHelper.class)) {
            return new ContextDataHolder<>(ResourceBundleHelper.class.getName(), ByTypeScope, new ResourceBundleHelper());
        }

        if (targetType.equals(ParamMapResourceBundleHelper.class)) {
            return new ContextDataHolder<>(ParamMapResourceBundleHelper.class.getName(), ByTypeScope, new ParamMapResourceBundleHelper());
        }

        return null;
    }

    @SuppressWarnings("rawtypes")
    private ContextDataHolder findDataByScopeOrder(Context context, int scopeIndex, String name) {
        if (scopeIndex >= dataSearchScopeOrder.size()) {
            return null;
        } else {
            String searchScope = dataSearchScopeOrder.get(scopeIndex);
            ContextDataHolder<?> holder = context.getDataHolder(searchScope, name);
            if (holder == null) {
                holder = findDataByScopeOrder(context, scopeIndex + 1, name);
            }
            return holder;
        }
    }

}
