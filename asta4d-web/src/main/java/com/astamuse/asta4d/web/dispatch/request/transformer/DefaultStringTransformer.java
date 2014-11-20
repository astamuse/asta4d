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
import com.astamuse.asta4d.template.TemplateNotFoundException;
import com.astamuse.asta4d.web.dispatch.request.ResultTransformer;
import com.astamuse.asta4d.web.dispatch.response.provider.Asta4DPageProvider;
import com.astamuse.asta4d.web.dispatch.response.provider.RedirectTargetProvider;
import com.astamuse.asta4d.web.util.bean.DeclareInstanceUtil;

public class DefaultStringTransformer implements ResultTransformer {

    @Override
    public Object transformToContentProvider(Object result) {
        if (result instanceof String) {
            String target = result.toString();
            if (target.startsWith("redirect:")) {// redirect
                String path = target.substring("redirect:".length());
                int status = HttpURLConnection.HTTP_MOVED_TEMP;

                int nextColonIndex = path.indexOf(":");
                if (nextColonIndex >= 0) {
                    String possibleStatus = path.substring(0, nextColonIndex);
                    if (possibleStatus.equalsIgnoreCase("p")) {
                        status = HttpURLConnection.HTTP_MOVED_PERM;
                    } else if (possibleStatus.equalsIgnoreCase("t")) {
                        status = HttpURLConnection.HTTP_MOVED_TEMP;
                    } else {
                        try {
                            status = Integer.parseInt(possibleStatus);
                        } catch (NumberFormatException nfe) {
                            // do nothing
                        }
                    }
                    path = path.substring(possibleStatus.length() + 1);
                }
                RedirectTargetProvider provider = DeclareInstanceUtil.createInstance(RedirectTargetProvider.class);
                provider.setStatus(status);
                provider.setTargetPath(path);
                return provider;
            } else {// asta4d page
                try {
                    Page page = Page.buildFromPath(result.toString());
                    Asta4DPageProvider provider = DeclareInstanceUtil.createInstance(Asta4DPageProvider.class);
                    provider.setPage(page);
                    return provider;
                } catch (TemplateNotFoundException tne) {
                    return tne;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            return null;
        }
    }

}
