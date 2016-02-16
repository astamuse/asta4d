package com.astamuse.asta4d.web.form.flow.base;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;

/**
 * Use this interface to indicate the representing input step when a single form class is to be used in different input steps.
 * 
 * @author e-ryu
 *
 */
public interface StepRepresentableForm {

    default void copyPropertiesFrom(Object from) {
        try {
            BeanUtils.copyProperties(this, from);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    default void copyPropertiesTo(Object target) {
        try {
            BeanUtils.copyProperties(target, this);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public String[] retrieveRepresentingSteps();
}
