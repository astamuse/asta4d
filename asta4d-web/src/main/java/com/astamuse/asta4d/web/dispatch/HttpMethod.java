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

public enum HttpMethod {
    GET, HEAD, POST, PUT, DELETE, OPTIONS, TRACE, CONNECT, PATCH, UNKNOWN;
    public static HttpMethod getMethod(String m) {
        try {
            return HttpMethod.valueOf(m.toUpperCase());
        } catch (IllegalArgumentException e) {
            return HttpMethod.UNKNOWN;
        }
    }

    public static class ExtendHttpMethod {
        private String method;

        public ExtendHttpMethod(String method) {
            this.method = method.toUpperCase();
        }

        public static ExtendHttpMethod of(String method) {
            return new ExtendHttpMethod(method);
        }

        public String getMethod() {
            return this.method;
        }

        @Override
        public int hashCode() {
            return method.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            ExtendHttpMethod other = (ExtendHttpMethod) obj;
            if (method == null) {
                if (other.method != null)
                    return false;
            } else if (!method.equals(other.method))
                return false;
            return true;
        }

    }
}
