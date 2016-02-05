package com.astamuse.asta4d.web.form.flow.base;

/**
 * Use this interface to indicate the representing input step when a single form class is to be used in different input steps.
 * 
 * @author e-ryu
 *
 */
public interface StepRepresentableForm {
    public String[] retrieveRepresentingSteps();
}
