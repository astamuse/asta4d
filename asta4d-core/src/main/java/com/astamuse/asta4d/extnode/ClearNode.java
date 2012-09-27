package com.astamuse.asta4d.extnode;

/**
 * 
 * A ClearNode will be removed after rendering. There is no warranty about when
 * a ClearNode will be removed but it was warranted that a ClearNode will be
 * eventually removed at the last of rendering process. <br>
 * Further, a formal html element with attribute "afd:clear" will be treated as
 * a ClearNode too.
 * 
 * @author e-ryu
 * 
 */
public class ClearNode extends GroupNode {

    public ClearNode() {
        super();
        this.attr(ExtNodeConstants.ATTR_CLEAR, "");
    }

}
