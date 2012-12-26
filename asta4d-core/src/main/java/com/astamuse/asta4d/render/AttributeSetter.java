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

package com.astamuse.asta4d.render;

import org.jsoup.nodes.Element;

import com.astamuse.asta4d.Context;
import com.astamuse.asta4d.extnode.ExtNodeConstants;
import com.astamuse.asta4d.util.IdGenerator;

/**
 * An AttributeSetter is used to set attribute value of an element. The
 * specified attribute name can be a plain text representing the target
 * attribute, and it can also be prefixed by an additional character: + or -.
 * <p>
 * There are several cases about how AttributeSetter perform its action by
 * different attribute name and value.<br>
 * 
 * <li>new AttriuteSetter("+class", value): call addClass(value) on target
 * Element, null value will be treated as "null".
 * 
 * <li>new AttriuteSetter("-class", value): call removeClass(value) on target
 * Element, null value will be treated as "null".
 * 
 * <li>new AttriuteSetter("class", value): call attr("class", value) on target
 * Element if value is not null, for a null value, removeAttr("class") will be
 * called.
 * 
 * <li>new AttriuteSetter("anyattr", value): call attr("anyattr", value) on
 * target Element if value is not null, for a null value, removeAttr("anyattr")
 * will be called.
 * 
 * <li>new AttriuteSetter("+anyattr", value): call attr("anyattr", value) on
 * target Element if value is not null, for a null value, removeAttr("anyattr")
 * will be called.
 * 
 * <li>new AttriuteSetter("-anyattr", value): call removeAttr("anyattr") on
 * target Element.
 * 
 * @author e-ryu
 * 
 */
public class AttributeSetter implements ElementSetter {

    private static enum ActionType {
        SET {
            @Override
            protected void configure(Element elem, String attrName, Object attrValue) {
                if (attrValue instanceof String) {
                    elem.removeAttr(ExtNodeConstants.ATTR_DATAREF_PREFIX_WITH_NS + attrName);
                    elem.attr(attrName, attrValue.toString());
                } else if (attrValue instanceof Number) {
                    elem.removeAttr(ExtNodeConstants.ATTR_DATAREF_PREFIX_WITH_NS + attrName);
                    elem.attr(attrName, attrValue.toString());
                } else if (attrValue instanceof Boolean) {
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
                // for remove all class definition, we have to clear the
                // classNames too.
                if (attrName.equals("class")) {
                    elem.classNames().clear();
                }

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

    /**
     * Constructor
     * 
     * @param attr
     *            attribute name
     * @param value
     *            attribute value
     */
    public AttributeSetter(String attr, Object value) {
        super();
        if (attr.equalsIgnoreCase("+class")) {
            this.actionType = ActionType.ADDCLASS;
            this.attrName = "class";
        } else if (attr.equalsIgnoreCase("-class")) {
            this.actionType = ActionType.REMOVECLASS;
            this.attrName = "class";
        } else if (attr.equalsIgnoreCase("class")) {
            if (value == null) {
                this.actionType = ActionType.REMOVE;
                this.attrName = "class";
            } else {
                this.actionType = ActionType.SET;
                this.attrName = "class";
            }
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
