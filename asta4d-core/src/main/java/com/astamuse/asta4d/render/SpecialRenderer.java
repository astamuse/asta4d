package com.astamuse.asta4d.render;

import com.astamuse.asta4d.render.transformer.Transformer;

public enum SpecialRenderer {

    /**
     * Remove the target node.There is no warranty about when a cleared node will be removed but it was warranted that a cleared node will
     * be eventually removed at the last of rendering process. <br>
     * 
     * Further, a formal html element with attribute "afd:clear" will be treated as a cleared node too.
     * 
     */
    Clear {
        @Override
        Transformer<?> getTransformer() {
            return new ElementRemover(Clear);
        }

    };

    abstract Transformer<?> getTransformer();

    /**
     * we don't want to make the {@link #getTransformer()} visible to user, but in framework, we need a way to access this method across
     * packages.
     * 
     * @param sr
     * @return
     */
    public static Transformer<?> retrieveTransformer(SpecialRenderer sr) {
        return sr.getTransformer();
    }

}
