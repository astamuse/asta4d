package com.astamuse.asta4d.web.test.form.field;

import org.testng.annotations.Test;

import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.util.collection.RowConvertor;
import com.astamuse.asta4d.web.form.field.OptionValueMap;
import com.astamuse.asta4d.web.form.field.OptionValuePair;
import com.astamuse.asta4d.web.form.field.impl.RadioBoxDataPrepareRenderer;
import com.astamuse.asta4d.web.form.field.impl.RadioBoxRenderer;
import com.astamuse.asta4d.web.form.field.impl.SelectBoxRenderer;
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

            builder.addPrepare(new RadioBoxDataPrepareRenderer("nullvalue").setOptionData(createMap("x", "y", "z")).setInputIdByValue(true));
            builder.addValue("nullvalue", null);

            builder.addPrepare(new RadioBoxDataPrepareRenderer("nullvalue-2").setOptionData(createMap("x", "y", "z", ""))
                    .setInputIdByValue(true));
            builder.addValue("nullvalue-2", null);

            builder.addPrepare(new RadioBoxDataPrepareRenderer("emptyvalue").setOptionData(createMap("x", "y", "z"))
                    .setInputIdByValue(true));
            builder.addValue("emptyvalue", "");

            builder.addPrepare(new RadioBoxDataPrepareRenderer("emptyvalue-2").setOptionData(createMap("x", "y", "z", ""))
                    .setInputIdByValue(true));
            builder.addValue("emptyvalue-2", "");

            builder.addPrepare(new RadioBoxDataPrepareRenderer("xvalue").setOptionData(createMap("y", "z")).setInputIdByValue(true));
            builder.addValue("xvalue", "x");

            builder.addPrepare(new RadioBoxDataPrepareRenderer("xvalue-2").setOptionData(createMap("x", "y", "z", "")).setInputIdByValue(
                    true));
            builder.addValue("xvalue-2", "x");

            builder.addPrepare(new RadioBoxDataPrepareRenderer("yvalue-2").setDuplicateSelector(".yvalue-wrapper")
                    .setOptionData(createMap("x", "y", "z", "")).setInputIdByValue(true));
            builder.addValue("yvalue-2", "y");

            return builder.toRenderer(true);
        }

        public Renderer normalDisplayValue() {
            FieldRenderBuilder builder = FieldRenderBuilder.of(RadioBoxRenderer.class);

            builder.addPrepare(new RadioBoxDataPrepareRenderer("nullvalue").setOptionData(createMap("x", "y", "z")).setInputIdByValue(true));
            builder.addValue("nullvalue", null);

            builder.addPrepare(new RadioBoxDataPrepareRenderer("nullvalue-2").setOptionData(createMap("x", "y", "z", ""))
                    .setInputIdByValue(true));
            builder.addValue("nullvalue-2", null);

            builder.addPrepare(new RadioBoxDataPrepareRenderer("emptyvalue").setOptionData(createMap("x", "y", "z"))
                    .setInputIdByValue(true));
            builder.addValue("emptyvalue", "");

            builder.addPrepare(new RadioBoxDataPrepareRenderer("emptyvalue-2").setOptionData(createMap("x", "y", "z", ""))
                    .setInputIdByValue(true));
            builder.addValue("emptyvalue-2", "");

            builder.addPrepare(new RadioBoxDataPrepareRenderer("xvalue").setOptionData(createMap("y", "z")).setInputIdByValue(true));
            builder.addValue("xvalue", "x");

            builder.addPrepare(new RadioBoxDataPrepareRenderer("xvalue-2").setOptionData(createMap("x", "y", "z", "")).setInputIdByValue(
                    true));
            builder.addValue("xvalue-2", "x");

            builder.addPrepare(new RadioBoxDataPrepareRenderer("yvalue-2").setDuplicateSelector(".yvalue-wrapper")
                    .setOptionData(createMap("x", "y", "z", "")).setInputIdByValue(true));
            builder.addValue("yvalue-2", "y");

            return builder.toRenderer(false);
        }

        public Renderer staticOptionEditValue() {
            FieldRenderBuilder builder = FieldRenderBuilder.of(SelectBoxRenderer.class);

            builder.addValue("nullvalue", null);

            builder.addValue("nullvalue-2", null);

            builder.addValue("emptyvalue", "");

            builder.addValue("emptyvalue-2", "");

            builder.addValue("spacevalue", " ");

            builder.addValue("spacevalue-2", " ");

            builder.addValue("xvalue", "x");

            builder.addValue("xvalue-2", "x");

            builder.addValue("yvalue", "y");

            builder.addValue("yvalue-2", "y");

            return builder.toRenderer(true);
        }

        public Renderer staticOptionDisplayValue() {
            FieldRenderBuilder builder = FieldRenderBuilder.of(SelectBoxRenderer.class);

            builder.addValue("nullvalue", null);

            builder.addValue("nullvalue-2", null);

            builder.addValue("emptyvalue", "");

            builder.addValue("emptyvalue-2", "");

            builder.addValue("spacevalue", " ");

            builder.addValue("spacevalue-2", " ");

            builder.addValue("xvalue", "x");

            builder.addValue("xvalue-2", "x");

            builder.addValue("yvalue", "y");

            builder.addValue("yvalue-2", "y");

            return builder.toRenderer(false);
        }
    }

    @Test
    public void testNormalEdit() {
        new FormRenderCase("/RadioBox_normalEdit.html");
    }

    public void testNormalDisplay() {
        new FormRenderCase("/RadioBox_normalDisplay.html");
    }

    /*
    public void testStaticOptionEdit() {
        new FormRenderCase("/SelectBox_staticOptionEdit.html");
    }

    public void testStaticOptionDisplay() {
        new FormRenderCase("/SelectBox_staticOptionDisplay.html");
    }
    */
}
