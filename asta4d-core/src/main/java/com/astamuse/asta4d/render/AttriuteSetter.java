package com.astamuse.asta4d.render;

import org.jsoup.nodes.Element;

/**
 * 
 * @author e-ryu
 * 
 */
public class AttriuteSetter implements ElementSetter {

    private static enum ActionType {
        SET, REMOVE, ADDCLASS, REMOVECLASS
    }

    private String attrName;

    private String attrValue;

    private ActionType actionType;

    public AttriuteSetter(String attr, String value) {
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
        switch (actionType) {
        case SET:
            elem.attr(attrName, attrValue);
            break;
        case REMOVE:
            elem.removeAttr(attrName);
            break;
        case ADDCLASS:
            elem.addClass(attrValue);
            break;
        case REMOVECLASS:
            elem.removeClass(attrValue);
            break;
        }
    }

    @Override
    public String toString() {
        return actionType + " attribute " + attrName + " for value [" + attrValue + "]";
    }

}
