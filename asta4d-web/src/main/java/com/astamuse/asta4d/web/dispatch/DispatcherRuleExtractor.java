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

package com.astamuse.asta4d.web.dispatch;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.astamuse.asta4d.web.WebApplicationContext;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingResult;
import com.astamuse.asta4d.web.dispatch.mapping.UrlMappingRule;

/**
 * 
 * Implementation of this interface should respect the return value of
 * {@link WebApplicationContext#getAccessURI()} before retrieve the real access
 * uri from request directly.
 * 
 * @author e-ryu
 * 
 */
public interface DispatcherRuleExtractor {

    /**
     * 
     * Implementation of this method should respect the return value of
     * {@link WebApplicationContext#getAccessURI()} before retrieve the real
     * access uri from request directly.
     * 
     * @param request
     * @param ruleList
     * @return
     */
    public UrlMappingResult findMappedRule(HttpServletRequest request, List<UrlMappingRule> ruleList);

}
