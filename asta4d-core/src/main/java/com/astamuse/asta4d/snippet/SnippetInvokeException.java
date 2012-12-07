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

package com.astamuse.asta4d.snippet;

public class SnippetInvokeException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 115458825987835218L;

    public SnippetInvokeException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public SnippetInvokeException(SnippetDeclarationInfo declaration, String msg) {
        super(createMsg(declaration, msg));
    }

    public SnippetInvokeException(SnippetDeclarationInfo declaration) {
        super(createMsg(declaration, null));
    }

    public SnippetInvokeException(SnippetDeclarationInfo declaration, Throwable cause) {
        super(createMsg(declaration, null), cause);
    }

    public SnippetInvokeException(SnippetDeclarationInfo declaration, String msg, Throwable cause) {
        super(createMsg(declaration, msg), cause);
    }

    private static String createMsg(SnippetDeclarationInfo declaration, String msg) {
        return "error occured when execute snippet " + declaration.toString() + (msg == null ? "" : " detail:" + msg);
    }

}
