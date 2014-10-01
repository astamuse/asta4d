package com.astamuse.asta4d.web.test.form.field;

import org.testng.annotations.Test;

import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.util.collection.RowConvertor;
import com.astamuse.asta4d.web.form.field.OptionValueMap;
import com.astamuse.asta4d.web.form.field.OptionValuePair;
import com.astamuse.asta4d.web.form.field.impl.RadioBoxPrepareRenderer;
import com.astamuse.asta4d.web.form.field.impl.RadioBoxRenderer;
import com.astamuse.asta4d.web.test.WebTestBase;
import com.astamuse.asta4d.web.test.form.FormRenderCase;

public class RadioBoxTest extends WebTestBase {

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
            FieldRenderBuilder builder = FieldRenderBuilder.of(RadioBoxRenderer.class);

            builder.addPrepare(new RadioBoxPrepareRenderer("nullvalue").setOptionData(createMap("x", "y", "z")).setInputIdByValue(true));
            builder.addValue("nullvalue", null);

            builder.addPrepare(new RadioBoxPrepareRenderer("nullvalue-2").setOptionData(createMap("x", "y", "z", ""))
                    .setInputIdByValue(true));
            builder.addValue("nullvalue-2", null);

            builder.addPrepare(new RadioBoxPrepareRenderer("emptyvalue").setOptionData(createMap("x", "y", "z"))
                    .setInputIdByValue(true));
            builder.addValue("emptyvalue", "");

            builder.addPrepare(new RadioBoxPrepareRenderer("emptyvalue-2").setOptionData(createMap("x", "y", "z", ""))
                    .setInputIdByValue(true));
            builder.addValue("emptyvalue-2", "");

            builder.addPrepare(new RadioBoxPrepareRenderer("rvalue").setOptionData(createMap("s", "t"))
                    .setLabelWrapperIndicatorAttr("radio-label-for").setInputIdByValue(true));
            builder.addValue("rvalue", "r");

            builder.addPrepare(new RadioBoxPrepareRenderer("rvalue-2").setOptionData(createMap("r", "s", "t", ""))
                    .setLabelWrapperIndicatorAttr("radio-label-for").setInputIdByValue(true));
            builder.addValue("rvalue-2", "r");

            builder.addPrepare(new RadioBoxPrepareRenderer("uvalue").setOptionData(createMap("v", "w")).setInputIdByValue(true));
            builder.addValue("uvalue", "u");

            builder.addPrepare(new RadioBoxPrepareRenderer("uvalue-2").setOptionData(createMap("u", "v", "w", "")).setInputIdByValue(
                    true));
            builder.addValue("uvalue-2", "u");

            builder.addPrepare(new RadioBoxPrepareRenderer("xvalue").setOptionData(createMap("y", "z")).setInputIdByValue(true));
            builder.addValue("xvalue", "x");

            builder.addPrepare(new RadioBoxPrepareRenderer("xvalue-2").setOptionData(createMap("x", "y", "z", "")).setInputIdByValue(
                    true));
            builder.addValue("xvalue-2", "x");

            builder.addPrepare(new RadioBoxPrepareRenderer("yvalue").setDuplicateSelector(".yvalue-wrapper")
                    .setOptionData(createMap("x", "z", "")).setInputIdByValue(true));
            builder.addValue("yvalue", "y");

            builder.addPrepare(new RadioBoxPrepareRenderer("yvalue-2").setDuplicateSelector(".yvalue-2-wrapper")
                    .setOptionData(createMap("x", "y", "z", "")).setInputIdByValue(true));
            builder.addValue("yvalue-2", "y");

            return builder.toRenderer(true);
        }

        public Renderer normalDisplayValue() {
            FieldRenderBuilder builder = FieldRenderBuilder.of(RadioBoxRenderer.class);

            builder.addPrepare(new RadioBoxPrepareRenderer("nullvalue").setOptionData(createMap("x", "y", "z")).setInputIdByValue(true));
            builder.addValue("nullvalue", null);

            builder.addPrepare(new RadioBoxPrepareRenderer("nullvalue-2").setOptionData(createMap("x", "y", "z", ""))
                    .setInputIdByValue(true));
            builder.addValue("nullvalue-2", null);

            builder.addPrepare(new RadioBoxPrepareRenderer("emptyvalue").setOptionData(createMap("x", "y", "z"))
                    .setInputIdByValue(true));
            builder.addValue("emptyvalue", "");

            builder.addPrepare(new RadioBoxPrepareRenderer("emptyvalue-2").setOptionData(createMap("x", "y", "z", ""))
                    .setInputIdByValue(true));
            builder.addValue("emptyvalue-2", "");

            builder.addPrepare(new RadioBoxPrepareRenderer("rvalue").setOptionData(createMap("s", "t"))
                    .setLabelWrapperIndicatorAttr("radio-label-for").setInputIdByValue(true));
            builder.addValue("rvalue", "r");

            builder.addPrepare(new RadioBoxPrepareRenderer("rvalue-2").setOptionData(createMap("r", "s", "t", ""))
                    .setLabelWrapperIndicatorAttr("radio-label-for").setInputIdByValue(true));
            builder.addValue("rvalue-2", "r");

            builder.addPrepare(new RadioBoxPrepareRenderer("svalue").setOptionData(createMap("r", "t"))
                    .setLabelWrapperIndicatorAttr("radio-label-for").setInputIdByValue(true));
            builder.addValue("svalue", "s");

            builder.addPrepare(new RadioBoxPrepareRenderer("svalue-2").setOptionData(createMap("r", "s", "t", ""))
                    .setLabelWrapperIndicatorAttr("radio-label-for").setInputIdByValue(true));
            builder.addValue("svalue-2", "s");

            builder.addPrepare(new RadioBoxPrepareRenderer("uvalue").setOptionData(createMap("v", "w")).setInputIdByValue(true));
            builder.addValue("uvalue", "u");

            builder.addPrepare(new RadioBoxPrepareRenderer("uvalue-2").setOptionData(createMap("u", "v", "w", "")).setInputIdByValue(
                    true));
            builder.addValue("uvalue-2", "u");

            builder.addPrepare(new RadioBoxPrepareRenderer("wvalue").setOptionData(createMap("x", "y")).setInputIdByValue(true));
            builder.addValue("wvalue", "w");

            builder.addPrepare(new RadioBoxPrepareRenderer("wvalue-2").setOptionData(createMap("w", "x", "y", "z")).setInputIdByValue(
                    true));
            builder.addValue("wvalue-2", "w");

            builder.addPrepare(new RadioBoxPrepareRenderer("xvalue").setOptionData(createMap("y", "z")).setInputIdByValue(true));
            builder.addValue("xvalue", "x");

            builder.addPrepare(new RadioBoxPrepareRenderer("xvalue-2").setOptionData(createMap("x", "y", "z", "")).setInputIdByValue(
                    true));
            builder.addValue("xvalue-2", "x");

            builder.addPrepare(new RadioBoxPrepareRenderer("yvalue").setDuplicateSelector(".yvalue-wrapper")
                    .setOptionData(createMap("x", "z", "")).setInputIdByValue(true));
            builder.addValue("yvalue", "y");

            builder.addPrepare(new RadioBoxPrepareRenderer("yvalue-2").setDuplicateSelector(".yvalue-2-wrapper")
                    .setOptionData(createMap("x", "y", "z", "")).setInputIdByValue(true));
            builder.addValue("yvalue-2", "y");

            builder.addPrepare(new RadioBoxPrepareRenderer("zvalue").setDuplicateSelector(".zvalue-wrapper")
                    .setOptionData(createMap("x", "y", "")).setInputIdByValue(true));
            builder.addValue("zvalue", "z");

            builder.addPrepare(new RadioBoxPrepareRenderer("zvalue-2").setDuplicateSelector(".zvalue-2-wrapper")
                    .setOptionData(createMap("x", "y", "z", "")).setInputIdByValue(true));
            builder.addValue("zvalue-2", "z");

            return builder.toRenderer(false);
        }

        public Renderer staticOptionEditValue() {
            FieldRenderBuilder builder = FieldRenderBuilder.of(RadioBoxRenderer.class);

            builder.addValue("nullvalue", null);

            builder.addValue("nullvalue-2", null);

            builder.addValue("emptyvalue", "");

            builder.addValue("emptyvalue-2", "");

            builder.addPrepare(new RadioBoxPrepareRenderer("rvalue").setLabelWrapperIndicatorAttr("radio-label-for").setInputIdByValue(
                    true));
            builder.addValue("rvalue", "r");

            builder.addPrepare(new RadioBoxPrepareRenderer("rvalue-2").setLabelWrapperIndicatorAttr("radio-label-for")
                    .setInputIdByValue(true));
            builder.addValue("rvalue-2", "r");

            builder.addValue("xvalue", "x");

            builder.addValue("xvalue-2", "x");

            builder.addPrepare(new RadioBoxPrepareRenderer("yvalue").setDuplicateSelector(".yvalue-wrapper"));
            builder.addValue("yvalue", "y");

            builder.addPrepare(new RadioBoxPrepareRenderer("yvalue-2").setDuplicateSelector(".yvalue-2-wrapper"));
            builder.addValue("yvalue-2", "y");

            return builder.toRenderer(true);
        }

        public Renderer staticOptionDisplayValue() {
            FieldRenderBuilder builder = FieldRenderBuilder.of(RadioBoxRenderer.class);

            builder.addValue("nullvalue", null);

            builder.addValue("nullvalue-2", null);

            builder.addValue("emptyvalue", "");

            builder.addValue("emptyvalue-2", "");

            builder.addPrepare(new RadioBoxPrepareRenderer("rvalue").setLabelWrapperIndicatorAttr("radio-label-for").setInputIdByValue(
                    true));
            builder.addValue("rvalue", "r");

            builder.addPrepare(new RadioBoxPrepareRenderer("rvalue-2").setLabelWrapperIndicatorAttr("radio-label-for")
                    .setInputIdByValue(true));
            builder.addValue("rvalue-2", "r");

            builder.addPrepare(new RadioBoxPrepareRenderer("svalue").setLabelWrapperIndicatorAttr("radio-label-for").setInputIdByValue(
                    true));
            builder.addValue("svalue", "s");

            builder.addPrepare(new RadioBoxPrepareRenderer("svalue-2").setLabelWrapperIndicatorAttr("radio-label-for")
                    .setInputIdByValue(true));
            builder.addValue("svalue-2", "s");

            builder.addValue("wvalue", "w");

            builder.addValue("wvalue-2", "w");

            builder.addValue("xvalue", "x");

            builder.addValue("xvalue-2", "x");

            builder.addPrepare(new RadioBoxPrepareRenderer("yvalue").setDuplicateSelector(".yvalue-wrapper"));
            builder.addValue("yvalue", "y");

            builder.addPrepare(new RadioBoxPrepareRenderer("yvalue-2").setDuplicateSelector(".yvalue-2-wrapper"));
            builder.addValue("yvalue-2", "y");

            return builder.toRenderer(false);
        }

        public Renderer duplicatedElement() {
            FieldRenderBuilder builder = FieldRenderBuilder.of(RadioBoxRenderer.class);

            builder.addPrepare(new RadioBoxPrepareRenderer("xvalue").setOptionData(createMap("x", "y")));
            builder.addValue("xvalue", "x");

            return builder.toRenderer(true);
        }

        public Renderer emptyIdElement() {
            FieldRenderBuilder builder = FieldRenderBuilder.of(RadioBoxRenderer.class);

            builder.addPrepare(new RadioBoxPrepareRenderer("xvalue").setOptionData(createMap("x", "y")));
            builder.addValue("xvalue", "x");

            return builder.toRenderer(true);
        }

        public Renderer duplicatedElementInDuplicator() {
            FieldRenderBuilder builder = FieldRenderBuilder.of(RadioBoxRenderer.class);

            builder.addPrepare(new RadioBoxPrepareRenderer("xvalue").setOptionData(createMap("x", "y")).setDuplicateSelector(
                    ".radio-wrapper"));
            builder.addValue("xvalue", "x");

            return builder.toRenderer(true);
        }

        public Renderer emptyIdElementInDuplicator() {
            FieldRenderBuilder builder = FieldRenderBuilder.of(RadioBoxRenderer.class);

            builder.addPrepare(new RadioBoxPrepareRenderer("xvalue").setOptionData(createMap("x", "y")).setDuplicateSelector(
                    ".radio-wrapper"));
            builder.addValue("xvalue", "x");

            return builder.toRenderer(true);
        }
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "duplicateSelector .+ and labelWrapperIndicatorAttr .+ cannot be specified at same time\\.")
    public void testDuplicatorAndLabelWrapperConflict() {
        new RadioBoxPrepareRenderer("yvalue").setDuplicateSelector("xx").setLabelWrapperIndicatorAttr("xx").preRender("ewrwe", "sdfa");
    }

    public void testNormalEdit() {
        new FormRenderCase("/RadioBox_normalEdit.html");
    }

    public void testNormalDisplay() {
        new FormRenderCase("/RadioBox_normalDisplay.html");
    }

    public void testStaticOptionEdit() {
        new FormRenderCase("/RadioBox_staticOptionEdit.html");
    }

    public void testStaticOptionDisplay() {
        new FormRenderCase("/RadioBox_staticOptionDisplay.html");
    }

    @Test(expectedExceptions = Exception.class, expectedExceptionsMessageRegExp = ".*The target of selector\\[\\[.+\\]\\] must be unique.+")
    public void testDuplicatedElement() {
        new FormRenderCase("/RadioBox_duplicatedElement.html");
    }

    @Test(expectedExceptions = Exception.class, expectedExceptionsMessageRegExp = ".*A radio input element must have id value being configured:.+")
    public void testEmptyIdElement() {
        new FormRenderCase("/RadioBox_emptyIdElement.html");
    }

    @Test(expectedExceptions = Exception.class, expectedExceptionsMessageRegExp = ".*The target of selector\\[\\[.+\\]\\] \\(inside duplicator:.+\\) must be unique.+")
    public void testDuplicatedElementInDuplicator() {
        new FormRenderCase("/RadioBox_duplicatedElementInDuplicator.html");
    }

    @Test(expectedExceptions = Exception.class, expectedExceptionsMessageRegExp = ".*A radio input element \\(inside duplicator:.+\\) must have id value being configured:.+")
    public void testEmptyIdElementInDuplicator() {
        new FormRenderCase("/RadioBox_emptyIdElementInDuplicator.html");
    }

}
