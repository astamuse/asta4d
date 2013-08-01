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

package com.astamuse.asta4d.web.dispatch.request.transformer;

import java.net.HttpURLConnection;

import com.astamuse.asta4d.Page;
import com.astamuse.asta4d.web.dispatch.request.ResultTransformer;
import com.astamuse.asta4d.web.dispatch.response.provider.Asta4DPageProvider;
import com.astamuse.asta4d.web.dispatch.response.provider.RedirectDescriptor;
import com.astamuse.asta4d.web.dispatch.response.provider.RedirectTargetProvider;
import com.astamuse.asta4d.web.util.DeclareInstanceUtil;

public class DefaultStringTransformer implements ResultTransformer {

    @Override
    public Object transformToContentProvider(Object result) {
        if (result instanceof String) {
            String target = result.toString();
            if (target.startsWith("redirect:")) {// redirect
                String path = target.substring("redirect:".length());
                String possibleStatus = path.substring(0, path.indexOf(":"));
                int status = HttpURLConnection.HTTP_MOVED_TEMP;
                try {
                    status = Integer.parseInt(possibleStatus);
                    path = path.substring(possibleStatus.length());
                } catch (NumberFormatException nfe) {
                    // do nothing
                }
                RedirectDescriptor rd = new RedirectDescriptor(status, path, null);
                RedirectTargetProvider provider = DeclareInstanceUtil.createInstance(RedirectTargetProvider.class);
                provider.setDescriptor(rd);
                return provider;
            } else {// asta4d page
                try {
                    Page page = Page.buildFromPath(result.toString());
                    if (page == null) {
                        return new TemplateNotFoundException(result.toString());
                    } else {
                        Asta4DPageProvider provider = DeclareInstanceUtil.createInstance(Asta4DPageProvider.class);
                        provider.setPage(page);
                        return provider;
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            return null;
        }
    }

}
