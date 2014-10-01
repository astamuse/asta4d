package com.astamuse.asta4d.web.test.form.field;

import org.testng.annotations.Test;

import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.util.collection.RowConvertor;
import com.astamuse.asta4d.web.form.field.OptionValueMap;
import com.astamuse.asta4d.web.form.field.OptionValuePair;
import com.astamuse.asta4d.web.form.field.impl.CheckBoxPrepareRenderer;
import com.astamuse.asta4d.web.form.field.impl.CheckBoxRenderer;
import com.astamuse.asta4d.web.test.WebTestBase;
import com.astamuse.asta4d.web.test.form.FormRenderCase;

public class CheckBoxTest extends WebTestBase {

    private static OptionValueMap createMap(String... values) {
        OptionValueMap map = OptionValueMap.build(values, new RowConvertor<String, OptionValuePair>() {
            @Override
            public OptionValuePair convert(int rowIndex, String obj) {
                return new OptionValuePair(obj, obj + ":displayvalue");
            }
        });
        return map;
    }

    public static class TestSnippet {

        @SuppressWarnings("deprecation")
        public Renderer normalEditValue() {
            FieldRenderBuilder builder = FieldRenderBuilder.of(CheckBoxRenderer.class);

            builder.addPrepare(new CheckBoxPrepareRenderer("nullvalue").setOptionData(createMap("x", "y", "z")).setInputIdByValue(true));
            builder.addValue("nullvalue", null);

            builder.addPrepare(new CheckBoxPrepareRenderer("nullvalue-2").setOptionData(createMap("x", "y", "z", ""))
                    .setInputIdByValue(true));
            builder.addValue("nullvalue-2", null);

            builder.addPrepare(new CheckBoxPrepareRenderer("emptyvalue").setOptionData(createMap("x", "y", "z"))
                    .setInputIdByValue(true));
            builder.addValue("emptyvalue", "");

            builder.addPrepare(new CheckBoxPrepareRenderer("emptyvalue-2").setOptionData(createMap("x", "y", "z", ""))
                    .setInputIdByValue(true));
            builder.addValue("emptyvalue-2", "");

            builder.addPrepare(new CheckBoxPrepareRenderer("rvalue").setOptionData(createMap("s", "t"))
                    .setLabelWrapperIndicatorAttr("radio-label-for").setInputIdByValue(true));
            builder.addValue("rvalue", "r");

            builder.addPrepare(new CheckBoxPrepareRenderer("rvalue-2").setOptionData(createMap("r", "s", "t", ""))
                    .setLabelWrapperIndicatorAttr("radio-label-for").setInputIdByValue(true));
            builder.addValue("rvalue-2", "r");

            builder.addPrepare(new CheckBoxPrepareRenderer("uvalue").setOptionData(createMap("v", "w")).setInputIdByValue(true));
            builder.addValue("uvalue", "u");

            builder.addPrepare(new CheckBoxPrepareRenderer("uvalue-2").setOptionData(createMap("u", "v", "w", "")).setInputIdByValue(
                    true));
            builder.addValue("uvalue-2", "u");

            builder.addPrepare(new CheckBoxPrepareRenderer("xvalue").setOptionData(createMap("y", "z")).setInputIdByValue(true));
            builder.addValue("xvalue", "x");

            builder.addPrepare(new CheckBoxPrepareRenderer("xvalue-2").setOptionData(createMap("x", "y", "z", "")).setInputIdByValue(
                    true));
            builder.addValue("xvalue-2", "x");

            builder.addPrepare(new CheckBoxPrepareRenderer("yvalue").setDuplicateSelector(".yvalue-wrapper")
                    .setOptionData(createMap("x", "z", "")).setInputIdByValue(true));
            builder.addValue("yvalue", "y");

            builder.addPrepare(new CheckBoxPrepareRenderer("yvalue-2").setDuplicateSelector(".yvalue-2-wrapper")
                    .setOptionData(createMap("x", "y", "z", "")).setInputIdByValue(true));
            builder.addValue("yvalue-2", "y");

            return builder.toRenderer(true);
        }

        @SuppressWarnings("deprecation")
        public Renderer normalEditMultiValue() {
            FieldRenderBuilder builder = FieldRenderBuilder.of(CheckBoxRenderer.class);

            builder.addPrepare(new CheckBoxPrepareRenderer("nullvalue").setOptionData(createMap("x", "y", "z")).setInputIdByValue(true));
            builder.addValue("nullvalue", new Object[] { "a", null });

            builder.addPrepare(new CheckBoxPrepareRenderer("nullvalue-2").setOptionData(createMap("x", "y", "z", ""))
                    .setInputIdByValue(true));
            builder.addValue("nullvalue-2", new Object[] { "a", null });

            builder.addPrepare(new CheckBoxPrepareRenderer("emptyvalue").setOptionData(createMap("x", "y", "z"))
                    .setInputIdByValue(true));
            builder.addValue("emptyvalue", new Object[] { "a", "" });

            builder.addPrepare(new CheckBoxPrepareRenderer("emptyvalue-2").setOptionData(createMap("x", "y", "z", ""))
                    .setInputIdByValue(true));
            builder.addValue("emptyvalue-2", new Object[] { "a", "" });

            builder.addPrepare(new CheckBoxPrepareRenderer("rvalue").setOptionData(createMap("s", "t"))
                    .setLabelWrapperIndicatorAttr("radio-label-for").setInputIdByValue(true));
            builder.addValue("rvalue", new Object[] { "r", "x" });

            builder.addPrepare(new CheckBoxPrepareRenderer("rvalue-2").setOptionData(createMap("r", "s", "t", ""))
                    .setLabelWrapperIndicatorAttr("radio-label-for").setInputIdByValue(true));
            builder.addValue("rvalue-2", new Object[] { "r", "t" });

            builder.addPrepare(new CheckBoxPrepareRenderer("uvalue").setOptionData(createMap("v", "w")).setInputIdByValue(true));
            builder.addValue("uvalue", new Object[] { "u", "x" });

            builder.addPrepare(new CheckBoxPrepareRenderer("uvalue-2").setOptionData(createMap("u", "v", "w", "")).setInputIdByValue(
                    true));
            builder.addValue("uvalue-2", new Object[] { "u", "w" });

            builder.addPrepare(new CheckBoxPrepareRenderer("xvalue").setOptionData(createMap("y", "z")).setInputIdByValue(true));
            builder.addValue("xvalue", new Object[] { "x", "t" });

            builder.addPrepare(new CheckBoxPrepareRenderer("xvalue-2").setOptionData(createMap("x", "y", "z", "")).setInputIdByValue(
                    true));
            builder.addValue("xvalue-2", new Object[] { "x", "z" });

            builder.addPrepare(new CheckBoxPrepareRenderer("yvalue").setDuplicateSelector(".yvalue-wrapper")
                    .setOptionData(createMap("x", "z", "")).setInputIdByValue(true));
            builder.addValue("yvalue", new Object[] { "y", "t" });

            builder.addPrepare(new CheckBoxPrepareRenderer("yvalue-2").setDuplicateSelector(".yvalue-2-wrapper")
                    .setOptionData(createMap("x", "y", "z", "")).setInputIdByValue(true));
            builder.addValue("yvalue-2", new Object[] { "x", "y" });

            return builder.toRenderer(true);
        }

        public Renderer normalDisplayValue() {
            FieldRenderBuilder builder = FieldRenderBuilder.of(CheckBoxRenderer.class);

            builder.addPrepare(new CheckBoxPrepareRenderer("nullvalue").setOptionData(createMap("x", "y", "z")).setInputIdByValue(true));
            builder.addValue("nullvalue", null);

            builder.addPrepare(new CheckBoxPrepareRenderer("nullvalue-2").setOptionData(createMap("x", "y", "z", ""))
                    .setInputIdByValue(true));
            builder.addValue("nullvalue-2", null);

            builder.addPrepare(new CheckBoxPrepareRenderer("emptyvalue").setOptionData(createMap("x", "y", "z"))
                    .setInputIdByValue(true));
            builder.addValue("emptyvalue", "");

            builder.addPrepare(new CheckBoxPrepareRenderer("emptyvalue-2").setOptionData(createMap("x", "y", "z", ""))
                    .setInputIdByValue(true));
            builder.addValue("emptyvalue-2", "");

            builder.addPrepare(new CheckBoxPrepareRenderer("rvalue").setOptionData(createMap("s", "t"))
                    .setLabelWrapperIndicatorAttr("radio-label-for").setInputIdByValue(true));
            builder.addValue("rvalue", "r");

            builder.addPrepare(new CheckBoxPrepareRenderer("rvalue-2").setOptionData(createMap("r", "s", "t", ""))
                    .setLabelWrapperIndicatorAttr("radio-label-for").setInputIdByValue(true));
            builder.addValue("rvalue-2", "r");

            builder.addPrepare(new CheckBoxPrepareRenderer("svalue").setOptionData(createMap("r", "t"))
                    .setLabelWrapperIndicatorAttr("radio-label-for").setInputIdByValue(true));
            builder.addValue("svalue", "s");

            builder.addPrepare(new CheckBoxPrepareRenderer("svalue-2").setOptionData(createMap("r", "s", "t", ""))
                    .setLabelWrapperIndicatorAttr("radio-label-for").setInputIdByValue(true));
            builder.addValue("svalue-2", "s");

            builder.addPrepare(new CheckBoxPrepareRenderer("uvalue").setOptionData(createMap("v", "w")).setInputIdByValue(true));
            builder.addValue("uvalue", "u");

            builder.addPrepare(new CheckBoxPrepareRenderer("uvalue-2").setOptionData(createMap("u", "v", "w", "")).setInputIdByValue(
                    true));
            builder.addValue("uvalue-2", "u");

            builder.addPrepare(new CheckBoxPrepareRenderer("wvalue").setOptionData(createMap("x", "y")).setInputIdByValue(true));
            builder.addValue("wvalue", "w");

            builder.addPrepare(new CheckBoxPrepareRenderer("wvalue-2").setOptionData(createMap("w", "x", "y", "z")).setInputIdByValue(
                    true));
            builder.addValue("wvalue-2", "w");

            builder.addPrepare(new CheckBoxPrepareRenderer("xvalue").setOptionData(createMap("y", "z")).setInputIdByValue(true));
            builder.addValue("xvalue", "x");

            builder.addPrepare(new CheckBoxPrepareRenderer("xvalue-2").setOptionData(createMap("x", "y", "z", "")).setInputIdByValue(
                    true));
            builder.addValue("xvalue-2", "x");

            builder.addPrepare(new CheckBoxPrepareRenderer("yvalue").setDuplicateSelector(".yvalue-wrapper")
                    .setOptionData(createMap("x", "z", "")).setInputIdByValue(true));
            builder.addValue("yvalue", "y");

            builder.addPrepare(new CheckBoxPrepareRenderer("yvalue-2").setDuplicateSelector(".yvalue-2-wrapper")
                    .setOptionData(createMap("x", "y", "z", "")).setInputIdByValue(true));
            builder.addValue("yvalue-2", "y");

            builder.addPrepare(new CheckBoxPrepareRenderer("zvalue").setDuplicateSelector(".zvalue-wrapper")
                    .setOptionData(createMap("x", "y", "")).setInputIdByValue(true));
            builder.addValue("zvalue", "z");

            builder.addPrepare(new CheckBoxPrepareRenderer("zvalue-2").setDuplicateSelector(".zvalue-2-wrapper")
                    .setOptionData(createMap("x", "y", "z", "")).setInputIdByValue(true));
            builder.addValue("zvalue-2", "z");

            return builder.toRenderer(false);
        }

        public Renderer normalDisplayMultiValue() {
            FieldRenderBuilder builder = FieldRenderBuilder.of(CheckBoxRenderer.class);

            builder.addPrepare(new CheckBoxPrepareRenderer("nullvalue").setOptionData(createMap("x", "y", "z")).setInputIdByValue(true));
            builder.addValue("nullvalue", new Object[] { "a", null });

            builder.addPrepare(new CheckBoxPrepareRenderer("nullvalue-2").setOptionData(createMap("x", "y", "z", ""))
                    .setInputIdByValue(true));
            builder.addValue("nullvalue-2", new Object[] { "a", null });

            builder.addPrepare(new CheckBoxPrepareRenderer("emptyvalue").setOptionData(createMap("x", "y", "z"))
                    .setInputIdByValue(true));
            builder.addValue("emptyvalue", new Object[] { "a", "" });

            builder.addPrepare(new CheckBoxPrepareRenderer("emptyvalue-2").setOptionData(createMap("x", "y", "z", ""))
                    .setInputIdByValue(true));
            builder.addValue("emptyvalue-2", new Object[] { "a", "" });

            builder.addPrepare(new CheckBoxPrepareRenderer("rvalue").setOptionData(createMap("s", "t"))
                    .setLabelWrapperIndicatorAttr("radio-label-for").setInputIdByValue(true));
            builder.addValue("rvalue", new Object[] { "r", "x" });

            builder.addPrepare(new CheckBoxPrepareRenderer("rvalue-2").setOptionData(createMap("r", "s", "t", ""))
                    .setLabelWrapperIndicatorAttr("radio-label-for").setInputIdByValue(true));
            builder.addValue("rvalue-2", new Object[] { "r", "t" });

            builder.addPrepare(new CheckBoxPrepareRenderer("svalue").setOptionData(createMap("r", "t"))
                    .setLabelWrapperIndicatorAttr("radio-label-for").setInputIdByValue(true));
            builder.addValue("svalue", new Object[] { "s", "x" });

            builder.addPrepare(new CheckBoxPrepareRenderer("svalue-2").setOptionData(createMap("r", "s", "t", ""))
                    .setLabelWrapperIndicatorAttr("radio-label-for").setInputIdByValue(true));
            builder.addValue("svalue-2", new Object[] { "s", "t", "b" });

            builder.addPrepare(new CheckBoxPrepareRenderer("uvalue").setOptionData(createMap("v", "w")).setInputIdByValue(true));
            builder.addValue("uvalue", new Object[] { "u", "x" });

            builder.addPrepare(new CheckBoxPrepareRenderer("uvalue-2").setOptionData(createMap("u", "v", "w", "")).setInputIdByValue(
                    true));
            builder.addValue("uvalue-2", new Object[] { "u", "w" });

            builder.addPrepare(new CheckBoxPrepareRenderer("wvalue").setOptionData(createMap("x", "y")).setInputIdByValue(true));
            builder.addValue("wvalue", new Object[] { "w", "x" });

            builder.addPrepare(new CheckBoxPrepareRenderer("wvalue-2").setOptionData(createMap("w", "x", "y", "z")).setInputIdByValue(
                    true));
            builder.addValue("wvalue-2", new Object[] { "w", "x" });

            builder.addPrepare(new CheckBoxPrepareRenderer("xvalue").setOptionData(createMap("y", "z")).setInputIdByValue(true));
            builder.addValue("xvalue", new Object[] { "x", "y" });

            builder.addPrepare(new CheckBoxPrepareRenderer("xvalue-2").setOptionData(createMap("x", "y", "z", "")).setInputIdByValue(
                    true));
            builder.addValue("xvalue-2", new Object[] { "x", "z" });

            builder.addPrepare(new CheckBoxPrepareRenderer("yvalue").setDuplicateSelector(".yvalue-wrapper")
                    .setOptionData(createMap("x", "z", "")).setInputIdByValue(true));
            builder.addValue("yvalue", new Object[] { "y", "a" });

            builder.addPrepare(new CheckBoxPrepareRenderer("yvalue-2").setDuplicateSelector(".yvalue-2-wrapper")
                    .setOptionData(createMap("x", "y", "z", "")).setInputIdByValue(true));
            builder.addValue("yvalue-2", new Object[] { "y", "z" });

            builder.addPrepare(new CheckBoxPrepareRenderer("zvalue").setDuplicateSelector(".zvalue-wrapper")
                    .setOptionData(createMap("x", "y", "")).setInputIdByValue(true));
            builder.addValue("zvalue", new Object[] { "s", "x" });

            builder.addPrepare(new CheckBoxPrepareRenderer("zvalue-2").setDuplicateSelector(".zvalue-2-wrapper")
                    .setOptionData(createMap("x", "y", "z", "")).setInputIdByValue(true));
            builder.addValue("zvalue-2", new Object[] { "z", null });

            return builder.toRenderer(false);
        }

        public Renderer staticOptionEditValue() {
            FieldRenderBuilder builder = FieldRenderBuilder.of(CheckBoxRenderer.class);

            builder.addValue("nullvalue", null);

            builder.addValue("nullvalue-2", null);

            builder.addValue("emptyvalue", "");

            builder.addValue("emptyvalue-2", "");

            builder.addPrepare(new CheckBoxPrepareRenderer("rvalue").setLabelWrapperIndicatorAttr("radio-label-for").setInputIdByValue(
                    true));
            builder.addValue("rvalue", "r");

            builder.addPrepare(new CheckBoxPrepareRenderer("rvalue-2").setLabelWrapperIndicatorAttr("radio-label-for")
                    .setInputIdByValue(true));
            builder.addValue("rvalue-2", "r");

            builder.addValue("xvalue", "x");

            builder.addValue("xvalue-2", "x");

            builder.addPrepare(new CheckBoxPrepareRenderer("yvalue").setDuplicateSelector(".yvalue-wrapper"));
            builder.addValue("yvalue", "y");

            builder.addPrepare(new CheckBoxPrepareRenderer("yvalue-2").setDuplicateSelector(".yvalue-2-wrapper"));
            builder.addValue("yvalue-2", "y");

            return builder.toRenderer(true);
        }

        public Renderer staticOptionEditMultiValue() {
            FieldRenderBuilder builder = FieldRenderBuilder.of(CheckBoxRenderer.class);

            builder.addValue("nullvalue", new Object[] { "a", null });

            builder.addValue("nullvalue-2", new Object[] { "a", null });

            builder.addValue("emptyvalue", new Object[] { "a", "" });

            builder.addValue("emptyvalue-2", new Object[] { "a", "" });

            builder.addPrepare(new CheckBoxPrepareRenderer("rvalue").setLabelWrapperIndicatorAttr("radio-label-for").setInputIdByValue(
                    true));
            builder.addValue("rvalue", new Object[] { "r", "s" });

            builder.addPrepare(new CheckBoxPrepareRenderer("rvalue-2").setLabelWrapperIndicatorAttr("radio-label-for")
                    .setInputIdByValue(true));
            builder.addValue("rvalue-2", new Object[] { "r", "s" });

            builder.addValue("xvalue", new Object[] { "x", "b" });

            builder.addValue("xvalue-2", new Object[] { "x", "z", "b" });

            builder.addPrepare(new CheckBoxPrepareRenderer("yvalue").setDuplicateSelector(".yvalue-wrapper"));
            builder.addValue("yvalue", new Object[] { "y", "s" });

            builder.addPrepare(new CheckBoxPrepareRenderer("yvalue-2").setDuplicateSelector(".yvalue-2-wrapper"));
            builder.addValue("yvalue-2", new Object[] { "y", "z" });

            return builder.toRenderer(true);
        }

        public Renderer staticOptionDisplayValue() {
            FieldRenderBuilder builder = FieldRenderBuilder.of(CheckBoxRenderer.class);

            builder.addValue("nullvalue", null);

            builder.addValue("nullvalue-2", null);

            builder.addValue("emptyvalue", "");

            builder.addValue("emptyvalue-2", "");

            builder.addPrepare(new CheckBoxPrepareRenderer("rvalue").setLabelWrapperIndicatorAttr("radio-label-for").setInputIdByValue(
                    true));
            builder.addValue("rvalue", "r");

            builder.addPrepare(new CheckBoxPrepareRenderer("rvalue-2").setLabelWrapperIndicatorAttr("radio-label-for")
                    .setInputIdByValue(true));
            builder.addValue("rvalue-2", "r");

            builder.addPrepare(new CheckBoxPrepareRenderer("svalue").setLabelWrapperIndicatorAttr("radio-label-for").setInputIdByValue(
                    true));
            builder.addValue("svalue", "s");

            builder.addPrepare(new CheckBoxPrepareRenderer("svalue-2").setLabelWrapperIndicatorAttr("radio-label-for")
                    .setInputIdByValue(true));
            builder.addValue("svalue-2", "s");

            builder.addValue("wvalue", "w");

            builder.addValue("wvalue-2", "w");

            builder.addValue("xvalue", "x");

            builder.addValue("xvalue-2", "x");

            builder.addPrepare(new CheckBoxPrepareRenderer("yvalue").setDuplicateSelector(".yvalue-wrapper"));
            builder.addValue("yvalue", "y");

            builder.addPrepare(new CheckBoxPrepareRenderer("yvalue-2").setDuplicateSelector(".yvalue-2-wrapper"));
            builder.addValue("yvalue-2", "y");

            return builder.toRenderer(false);
        }

        public Renderer staticOptionDisplayMultiValue() {
            FieldRenderBuilder builder = FieldRenderBuilder.of(CheckBoxRenderer.class);

            builder.addValue("nullvalue", new Object[] { "a", null });

            builder.addValue("nullvalue-2", new Object[] { "a", null });

            builder.addValue("emptyvalue", new Object[] { "a", "" });

            builder.addValue("emptyvalue-2", new Object[] { "a", "" });

            builder.addPrepare(new CheckBoxPrepareRenderer("rvalue").setLabelWrapperIndicatorAttr("radio-label-for").setInputIdByValue(
                    true));
            builder.addValue("rvalue", new Object[] { "a", "r" });

            builder.addPrepare(new CheckBoxPrepareRenderer("rvalue-2").setLabelWrapperIndicatorAttr("radio-label-for")
                    .setInputIdByValue(true));
            builder.addValue("rvalue-2", new Object[] { "r", "t" });

            builder.addPrepare(new CheckBoxPrepareRenderer("svalue").setLabelWrapperIndicatorAttr("radio-label-for").setInputIdByValue(
                    true));
            builder.addValue("svalue", new Object[] { "s", "a" });

            builder.addPrepare(new CheckBoxPrepareRenderer("svalue-2").setLabelWrapperIndicatorAttr("radio-label-for")
                    .setInputIdByValue(true));
            builder.addValue("svalue-2", new Object[] { "s", "t" });

            builder.addValue("wvalue", new Object[] { "w", "a" });

            builder.addValue("wvalue-2", new Object[] { "w", "z", "a" });

            builder.addValue("xvalue", new Object[] { "x", "y" });

            builder.addValue("xvalue-2", new Object[] { "x", "y" });

            builder.addPrepare(new CheckBoxPrepareRenderer("yvalue").setDuplicateSelector(".yvalue-wrapper"));
            builder.addValue("yvalue", new Object[] { "y", "z" });

            builder.addPrepare(new CheckBoxPrepareRenderer("yvalue-2").setDuplicateSelector(".yvalue-2-wrapper"));
            builder.addValue("yvalue-2", new Object[] { "y", "z" });

            return builder.toRenderer(false);
        }

        public Renderer duplicatedElement() {
            FieldRenderBuilder builder = FieldRenderBuilder.of(CheckBoxRenderer.class);

            builder.addPrepare(new CheckBoxPrepareRenderer("xvalue").setOptionData(createMap("x", "y")));
            builder.addValue("xvalue", "x");

            return builder.toRenderer(true);
        }

        public Renderer emptyIdElement() {
            FieldRenderBuilder builder = FieldRenderBuilder.of(CheckBoxRenderer.class);

            builder.addPrepare(new CheckBoxPrepareRenderer("xvalue").setOptionData(createMap("x", "y")));
            builder.addValue("xvalue", "x");

            return builder.toRenderer(true);
        }

        public Renderer duplicatedElementInDuplicator() {
            FieldRenderBuilder builder = FieldRenderBuilder.of(CheckBoxRenderer.class);

            builder.addPrepare(new CheckBoxPrepareRenderer("xvalue").setOptionData(createMap("x", "y")).setDuplicateSelector(
                    ".radio-wrapper"));
            builder.addValue("xvalue", "x");

            return builder.toRenderer(true);
        }

        public Renderer emptyIdElementInDuplicator() {
            FieldRenderBuilder builder = FieldRenderBuilder.of(CheckBoxRenderer.class);

            builder.addPrepare(new CheckBoxPrepareRenderer("xvalue").setOptionData(createMap("x", "y")).setDuplicateSelector(
                    ".radio-wrapper"));
            builder.addValue("xvalue", "x");

            return builder.toRenderer(true);
        }
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "duplicateSelector .+ and labelWrapperIndicatorAttr .+ cannot be specified at same time\\.")
    public void testDuplicatorAndLabelWrapperConflict() {
        new CheckBoxPrepareRenderer("yvalue").setDuplicateSelector("xx").setLabelWrapperIndicatorAttr("xx").preRender("ewrwe", "sdfa");
    }

    public void testNormalEdit() {
        new FormRenderCase("/CheckBox_normalEdit.html");
    }

    public void testNormalEditMultiValue() {
        new FormRenderCase("/CheckBox_normalEditMultiValue.html");
    }

    public void testNormalDisplay() {
        new FormRenderCase("/CheckBox_normalDisplay.html");
    }

    public void testNormalDisplayMultiValue() {
        new FormRenderCase("/CheckBox_normalDisplayMultiValue.html");
    }

    public void testStaticOptionEdit() {
        new FormRenderCase("/CheckBox_staticOptionEdit.html");
    }

    public void testStaticOptionEditMultiValue() {
        new FormRenderCase("/CheckBox_staticOptionEditMultiValue.html");
    }

    public void testStaticOptionDisplay() {
        new FormRenderCase("/CheckBox_staticOptionDisplay.html");
    }

    public void testStaticOptionDisplayMultiValue() {
        new FormRenderCase("/CheckBox_staticOptionDisplayMultiValue.html");
    }

    @Test(expectedExceptions = Exception.class, expectedExceptionsMessageRegExp = ".*The target of selector\\[\\[.+\\]\\] must be unique.+")
    public void testDuplicatedElement() {
        new FormRenderCase("/CheckBox_duplicatedElement.html");
    }

    @Test(expectedExceptions = Exception.class, expectedExceptionsMessageRegExp = ".*A checkbox input element must have id value being configured:.+")
    public void testEmptyIdElement() {
        new FormRenderCase("/CheckBox_emptyIdElement.html");
    }

    @Test(expectedExceptions = Exception.class, expectedExceptionsMessageRegExp = ".*The target of selector\\[\\[.+\\]\\] \\(inside duplicator:.+\\) must be unique.+")
    public void testDuplicatedElementInDuplicator() {
        new FormRenderCase("/CheckBox_duplicatedElementInDuplicator.html");
    }

    @Test(expectedExceptions = Exception.class, expectedExceptionsMessageRegExp = ".*A checkbox input element \\(inside duplicator:.+\\) must have id value being configured:.+")
    public void testEmptyIdElementInDuplicator() {
        new FormRenderCase("/CheckBox_emptyIdElementInDuplicator.html");
    }

}
