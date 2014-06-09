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

package com.astamuse.asta4d.extnode;

import com.astamuse.asta4d.Configuration;
import com.astamuse.asta4d.util.SelectorUtil;

public class ExtNodeConstants {

    public final static String ID_PREFIX = Configuration.getConfiguration().getTagNameSpace();

    private final static String addNS(String name) {
        return ID_PREFIX + ":" + name;
    }

    public final static String BLOCK_NODE_NAME = "block";
    public final static String BLOCK_NODE_TAG = addNS(BLOCK_NODE_NAME);
    public final static String BLOCK_NODE_TAG_SELECTOR = SelectorUtil.tag(BLOCK_NODE_TAG);
    public final static String BLOCK_NODE_ATTR_OVERRIDE = "override";
    public final static String BLOCK_NODE_ATTR_APPEND = "append";
    public final static String BLOCK_NODE_ATTR_INSERT = "insert";

    public final static String EXTENSION_NODE_NAME = "extension";
    public final static String EXTENSION_NODE_TAG = addNS(EXTENSION_NODE_NAME);
    public final static String EXTENSION_NODE_TAG_SELECTOR = SelectorUtil.tag(EXTENSION_NODE_TAG);
    public final static String EXTENSION_NODE_ATTR_PARENT = "parent";

    public final static String EMBED_NODE_NAME = "embed";
    public final static String EMBED_NODE_TAG = addNS(EMBED_NODE_NAME);
    public final static String EMBED_NODE_TAG_SELECTOR = SelectorUtil.tag(EMBED_NODE_TAG);

    public final static String EMBED_NODE_ATTR_TARGET = "target";
    public final static String EMBED_NODE_ATTR_BLOCK = "block";
    public final static String EMBED_NODE_ATTR_STATIC = "static";

    public final static String SNIPPET_NODE_NAME = "snippet";
    public final static String SNIPPET_NODE_TAG = addNS(SNIPPET_NODE_NAME);
    public final static String SNIPPET_NODE_TAG_SELECTOR = SelectorUtil.tag(SNIPPET_NODE_TAG);

    public final static String SNIPPET_NODE_ATTR_RENDER = "render";
    public final static String SNIPPET_NODE_ATTR_RENDER_WITH_NS = addNS(SNIPPET_NODE_ATTR_RENDER);

    public final static String SNIPPET_NODE_ATTR_PARALLEL = "parallel";
    public final static String SNIPPET_NODE_ATTR_PARALLEL_WITH_NS = addNS(SNIPPET_NODE_ATTR_PARALLEL);

    public final static String SNIPPET_NODE_ATTR_TYPE = "type";
    public final static String SNIPPET_NODE_ATTR_TYPE_USERDEFINE = "userdefine";
    public final static String SNIPPET_NODE_ATTR_TYPE_FAKE = "fake";

    public final static String SNIPPET_NODE_ATTR_STATUS = "status";
    public final static String SNIPPET_NODE_ATTR_STATUS_READY = "ready";
    public final static String SNIPPET_NODE_ATTR_STATUS_WAITING = "waiting";
    public final static String SNIPPET_NODE_ATTR_STATUS_FINISHED = "finished";

    public final static String SNIPPET_NODE_ATTR_BLOCK = "block";

    public final static String GROUP_NODE_NAME = "group";
    public final static String GROUP_NODE_TAG = addNS(GROUP_NODE_NAME);
    public final static String GROUP_NODE_TAG_SELECTOR = SelectorUtil.tag(GROUP_NODE_TAG);

    public final static String GROUP_NODE_ATTR_TYPE = "type";
    public final static String GROUP_NODE_ATTR_TYPE_USERDEFINE = "userdefine";
    public final static String GROUP_NODE_ATTR_TYPE_FAKE = "fake";
    public final static String GROUP_NODE_ATTR_TYPE_EMBED_WRAPPER = "embed_wrapper";

    public final static String COMMENT_NODE_NAME = "comment";
    public final static String COMMENT_NODE_TAG = addNS(COMMENT_NODE_NAME);
    public final static String COMMENT_NODE_TAG_SELECTOR = SelectorUtil.tag(COMMENT_NODE_TAG);

    public final static String MSG_NODE_NAME = "msg";
    public final static String MSG_NODE_TAG = addNS(MSG_NODE_NAME);

    public final static String MSG_NODE_ATTR_KEY = "key";
    public final static String MSG_NODE_ATTRVALUE_TEXT_PREFIX = "text:";
    public final static String MSG_NODE_ATTRVALUE_HTML_PREFIX = "html:";
    public final static String MSG_NODE_ATTR_PARAM_PREFIX = "p";
    public final static String MSG_NODE_ATTR_LOCALE = "locale";
    public final static String MSG_NODE_ATTR_EXTERNALIZE = "externalize";

    public final static String ATTR_SNIPPET_REF = "snippet-ref";
    public final static String ATTR_DOC_REF = "doc-ref";

    public final static String ATTR_CLEAR = "clear";
    public final static String ATTR_CLEAR_WITH_NS = addNS(ATTR_CLEAR);

    public final static String ATTR_DATAREF_PREFIX = "dataref-";
    public final static String ATTR_DATAREF_PREFIX_WITH_NS = addNS(ATTR_DATAREF_PREFIX);

    //@formatter:off
    public final static String[] ASTA4D_IN_HEAD_NODE_TAGS = { 
        BLOCK_NODE_TAG,
        EMBED_NODE_TAG,
        SNIPPET_NODE_TAG,
        GROUP_NODE_TAG,
        COMMENT_NODE_TAG,
        MSG_NODE_TAG 
    };
    //@formatter:on
}
