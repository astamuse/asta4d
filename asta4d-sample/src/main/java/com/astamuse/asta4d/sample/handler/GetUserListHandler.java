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

package com.astamuse.asta4d.sample.handler;

import java.util.Arrays;
import java.util.List;

import com.astamuse.asta4d.web.dispatch.request.RequestHandler;

public class GetUserListHandler {

    @RequestHandler
    public List<String> queryUserList() {
        return Arrays.asList("otani", "ryu", "mizuhara");
    }
}
