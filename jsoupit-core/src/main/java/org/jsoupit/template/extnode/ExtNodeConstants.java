package org.jsoupit.template.extnode;

public class ExtNodeConstants {

    public final static String ID_PREFIX = "jsoupit";

    private final static String buildTag(String name) {
        return ID_PREFIX + ":" + name;
    }

    private final static String buildTagSelector(String name) {
        return ID_PREFIX + "|" + name;
    }

    @SuppressWarnings("unused")
    private final static String buildClass(String name) {
        return ID_PREFIX + "-" + name;
    }

    @SuppressWarnings("unused")
    private final static String buildClassSelector(String name) {
        return "." + ID_PREFIX + "-" + name;
    }

    public final static String BLOCK_NODE_NAME = "block";
    public final static String BLOCK_NODE_TAG = buildTag(BLOCK_NODE_NAME);
    public final static String BLOCK_NODE_TAG_SELECTOR = buildTagSelector(BLOCK_NODE_NAME);
    public final static String BLOCK_NODE_ATTR_OVERRIDE = "override";
    public final static String BLOCK_NODE_ATTR_APPEND = "append";
    public final static String BLOCK_NODE_ATTR_INSERT = "insert";

    public final static String EXTENSION_NODE_NAME = "extension";
    public final static String EXTENSION_NODE_TAG = buildTag(EXTENSION_NODE_NAME);
    public final static String EXTENSION_NODE_TAG_SELECTOR = buildTagSelector(EXTENSION_NODE_NAME);
    public final static String EXTENSION_NODE_ATTR_PARENT = "parent";

    public final static String EMBED_NODE_NAME = "embed";
    public final static String EMBED_NODE_TAG = buildTag(EMBED_NODE_NAME);
    public final static String EMBED_NODE_TAG_SELECTOR = buildTagSelector(EMBED_NODE_NAME);
    public final static String EMBED_NODE_ATTR_TARGET = "target";
    public final static String EMBED_NODE_ATTR_BLOCK = "block";

    public final static String SNIPPET_NODE_NAME = "snippet";
    public final static String SNIPPET_NODE_TAG = buildTag(SNIPPET_NODE_NAME);
    public final static String SNIPPET_NODE_TAG_SELECTOR = buildTagSelector(SNIPPET_NODE_NAME);
    public final static String SNIPPET_NODE_ATTR_RENDER = "render";

    public final static String SNIPPET_NODE_ATTR_REFID = "refid";

    public final static String SNIPPET_NODE_ATTR_TYPE = "type";
    public final static String SNIPPET_NODE_ATTR_TYPE_USERDEFINE = "userdefine";
    public final static String SNIPPET_NODE_ATTR_TYPE_FAKE = "fake";

    public final static String SNIPPET_NODE_ATTR_STATUS = "status";
    // public final static String SNIPPET_NODE_ATTR_STATUS_WAITING = "waiting";
    public final static String SNIPPET_NODE_ATTR_STATUS_READY = "ready";
    public final static String SNIPPET_NODE_ATTR_STATUS_FINISHED = "finished";

    public final static String SNIPPET_NODE_ATTR_BLOCK = "block";

    public final static String CLEAR_NODE_NAME = "ClearNode";
    public final static String CLEAR_NODE_TAG = buildTag(CLEAR_NODE_NAME);
    public final static String CLEAR_NODE_TAG_SELECTOR = buildTagSelector(CLEAR_NODE_NAME);

    public final static String GOTHROGH_NODE_TAG = buildTag("GoThroughNode");

    public final static String GROUP_NODE_NAME = "GroupNode";
    public final static String GROUP_NODE_TAG = buildTag(GROUP_NODE_NAME);
    public final static String GROUP_NODE_TAG_SELECTOR = buildTagSelector(GROUP_NODE_NAME);

}
