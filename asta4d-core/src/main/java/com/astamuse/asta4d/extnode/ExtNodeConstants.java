package com.astamuse.asta4d.extnode;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.util.SelectorUtil;

public class ExtNodeConstants {

    public final static String ID_PREFIX = Context.getCurrentThreadContext().getConfiguration().getTagNameSpace();

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

    public final static String SNIPPET_NODE_NAME = "snippet";
    public final static String SNIPPET_NODE_TAG = addNS(SNIPPET_NODE_NAME);
    public final static String SNIPPET_NODE_TAG_SELECTOR = SelectorUtil.tag(SNIPPET_NODE_TAG);

    public final static String SNIPPET_NODE_ATTR_RENDER = "render";
    public final static String SNIPPET_NODE_ATTR_RENDER_WITH_NS = addNS(SNIPPET_NODE_ATTR_RENDER);

    public final static String SNIPPET_NODE_ATTR_TYPE = "type";
    public final static String SNIPPET_NODE_ATTR_TYPE_USERDEFINE = "userdefine";
    public final static String SNIPPET_NODE_ATTR_TYPE_FAKE = "fake";

    public final static String SNIPPET_NODE_ATTR_STATUS = "status";
    public final static String SNIPPET_NODE_ATTR_STATUS_READY = "ready";
    public final static String SNIPPET_NODE_ATTR_STATUS_FINISHED = "finished";

    public final static String SNIPPET_NODE_ATTR_BLOCK = "block";

    public final static String ATTR_REFID = "afdRefId";

    public final static String ATTR_CLEAR = "clear";
    public final static String ATTR_CLEAR_WITH_NS = addNS(ATTR_CLEAR);

    public final static String GROUP_NODE_NAME = "GroupNode";
    public final static String GROUP_NODE_TAG = addNS(GROUP_NODE_NAME);
    public final static String GROUP_NODE_TAG_SELECTOR = SelectorUtil.tag(GROUP_NODE_TAG);

    public final static String DATAREF_ATTR_PREFIX = "dataref-";
    public final static String DATAREF_ATTR_PREFIX_WITH_NS = addNS(DATAREF_ATTR_PREFIX);
}
