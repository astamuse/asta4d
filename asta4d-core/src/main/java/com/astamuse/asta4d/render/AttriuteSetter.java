package com.astamuse.asta4d.render;

import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.extnode.ExtNodeConstants;
import com.astamuse.asta4d.util.IdGenerator;

/**
 * 
 * @author e-ryu
 * 
 */
public class AttriuteSetter implements ElementSetter {

    private final static Logger logger = LoggerFactory.getLogger(AttriuteSetter.class);

    private static enum ActionType {
        SET {
            @Override
            protected void configure(Element elem, String attrName, Object attrValue) {
                if (attrValue instanceof String) {
                    elem.attr(attrName, (String) attrValue);
                } else {
                    if (elem.hasAttr(ExtNodeConstants.DATAREF_ATTR_PREFIX_WITH_NS + attrName)) {
                        String orgValue = elem.attr(ExtNodeConstants.DATAREF_ATTR_PREFIX_WITH_NS + attrName);
                        logger.warn(String.format("override existed attribute(%s=\"%s\") for setting %s", attrName, orgValue, attrValue
                                .getClass().getName()));
                    }
                    String dataRefId = attrName + "_" + IdGenerator.createId();
                    Context context = Context.getCurrentThreadContext();
                    context.setData(Context.SCOPE_EXT_ATTR, dataRefId, attrValue);
                    elem.removeAttr(attrName);
                    elem.attr(ExtNodeConstants.DATAREF_ATTR_PREFIX_WITH_NS + attrName, dataRefId);
                }
            }
        },
        REMOVE {
            @Override
            protected void configure(Element elem, String attrName, Object attrValue) {
                boolean existAttr = elem.hasAttr(attrName);
                boolean existDataRefAttr = elem.hasAttr(ExtNodeConstants.DATAREF_ATTR_PREFIX_WITH_NS + attrName);
                if (!existAttr && existDataRefAttr) {
                    elem.removeAttr(ExtNodeConstants.DATAREF_ATTR_PREFIX_WITH_NS + attrName);
                } else {
                    elem.removeAttr(attrName);
                }
            }
        },
        ADDCLASS {
            @Override
            protected void configure(Element elem, String attrName, Object attrValue) {
                if (!(attrValue instanceof String)) {
                    throw new IllegalArgumentException("unexpected value type : " + attrValue.getClass().getName());
                }
                elem.addClass((String) attrValue);
            }
        },
        REMOVECLASS {
            @Override
            protected void configure(Element elem, String attrName, Object attrValue) {
                if (!(attrValue instanceof String)) {
                    throw new IllegalArgumentException("unexpected value type : " + attrValue.getClass().getName());
                }
                elem.removeClass((String) attrValue);
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
