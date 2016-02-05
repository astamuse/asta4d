package com.astamuse.asta4d.web.form.flow.base;

public interface LifecycleAwaredForm {

    /**
     * To be invoked after the form instance is retrieved, default to call {@link #rewriteAlways(String)}
     * 
     * @param step
     */
    default void rewriteAfterRetrieved(String step) {
        rewriteAlways(step);
    }

    /**
     * To be invoked before the form instance to be stored for next step, default to call {@link #rewriteAlways(String)}
     * 
     * @param step
     */
    default void rewriteBeforeStored(String step) {
        rewriteAlways(step);
    }

    /**
     * To be invoked at all the life cycle callback points, default to do nothing
     * 
     * @param step
     */
    default void rewriteAlways(String step) {

    }
}
