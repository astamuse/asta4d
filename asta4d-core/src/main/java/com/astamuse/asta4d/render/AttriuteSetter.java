package com.astamuse.asta4d.render;

import org.jsoup.nodes.Element;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.extnode.ExtNodeConstants;
import com.astamuse.asta4d.util.IdGenerator;

/**
 * 
 * @author e-ryu
 * 
 */
public class AttriuteSetter implements ElementSetter {

    private static enum ActionType {
        SET {
            @Override
            protected void configure(Element elem, String attrName, Object attrValue) {
                if (attrValue instanceof String) {
                    elem.removeAttr(ExtNodeConstants.ATTR_DATAREF_PREFIX_WITH_NS + attrName);
                    elem.attr(attrName, attrValue.toString());
                } else {
                    String dataRefId = attrName + "_" + IdGenerator.createId();
                    Context context = Context.getCurrentThreadContext();
                    context.setData(Context.SCOPE_EXT_ATTR, dataRefId, attrValue);
                    elem.removeAttr(attrName);
                    elem.attr(ExtNodeConstants.ATTR_DATAREF_PREFIX_WITH_NS + attrName, dataRefId);
                }
            }
        },
        REMOVE {
            @Override
            protected void configure(Element elem, String attrName, Object attrValue) {
                elem.removeAttr(ExtNodeConstants.ATTR_DATAREF_PREFIX_WITH_NS + attrName);
                elem.removeAttr(attrName);
            }
        },
        ADDCLASS {
            @Override
            protected void configure(Element elem, String attrName, Object attrValue) {
                elem.addClass(attrValue.toString());
            }
        },
        REMOVECLASS {
            @Override
            protected void configure(Element elem, String attrName, Object attrValue) {
                elem.removeClass(attrValue.toString());
            }
        };

        protected abstract void configure(Element elem, String attrName, Object attrValue);
    }

    private String attrName;

    private Object attrValue;

    private ActionType actionType;

    public AttriuteSetter(String attr, Object value) {
        super();
        if (attr.equalsIgnoreCase("+class")) {
            this.actionType = ActionType.ADDCLASS;
            this.attrName = "class";
        } else if (attr.equalsIgnoreCase("-class")) {
            if (value == null) {
                this.actionType = ActionType.REMOVE;
            } else {
                this.actionType = ActionType.REMOVECLASS;
            }
            this.attrName = "class";
        } else {
            if (attr.startsWith("-")) {
                this.actionType = ActionType.REMOVE;
                this.attrName = attr.substring(1);
            } else {
                if (attr.startsWith("+")) {
                    this.attrName = attr.substring(1);
                } else {
                    this.attrName = attr;
                }
                if (value == null) {
                    this.actionType = ActionType.REMOVE;
                } else {
                    this.actionType = ActionType.SET;
                }
            }
        }

        this.attrValue = value == null ? "null" : value;
        if (actionType == ActionType.ADDCLASS || actionType == ActionType.REMOVECLASS) {
            if (!(attrValue instanceof String)) {
                throw new IllegalArgumentException("Only String type is allowed in class setting but found unexpected value type : " +
                        attrValue.getClass().getName());
            }
        }
    }

    @Override
    public void set(Element elem) {
        actionType.configure(elem, attrName, attrValue);
    }

    @Override
    public String toString() {
        return actionType + " attribute " + attrName + " for value [" + attrValue + "]";
    }

}
