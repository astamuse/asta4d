package com.astamuse.asta4d.web.form.flow.ext;

import static com.astamuse.asta4d.render.SpecialRenderer.Clear;

import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.web.form.flow.base.BasicFormFlowSnippetTrait;

public interface SimpleFormFieldExcludeRenderable extends BasicFormFlowSnippetTrait {

    @Override
    default Renderer preRenderForm(String renderTargetStep, Object form, int[] indexes) {
        return formFieldExcludeRendering(renderTargetStep, form, indexes);
    }

    default Renderer formFieldExcludeRendering(String renderTargetStep, Object form, int[] indexes) {
        Renderer renderer = Renderer.create();
        if (form instanceof SimpleFormFieldExcludeDescprition) {
            SimpleFormFieldExcludeDescprition desc = (SimpleFormFieldExcludeDescprition) form;
            String[] fields = desc.getExcludeFields();
            for (String field : fields) {
                renderer.add(exludeField(field));
            }
        }
        return renderer;
    }

    default Renderer exludeField(String field) {
        Renderer renderer = Renderer.create();
        for (String s : clearExcludeFieldsSelectors(field)) {
            if (clearExcludeFields()) {
                renderer.add(s, Clear);
            } else {
                renderer.add(s, "display", "none");
            }
        }
        return renderer;
    }

    default String[] clearExcludeFieldsSelectors(String field) {
        return new String[] { "[name=" + field + "]", "#" + field + "-container" };
    }

    default boolean clearExcludeFields() {
        return true;
    }

}
