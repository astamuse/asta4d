package com.astamuse.asta4d.web.test.form.field;

import org.testng.annotations.Test;

import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.util.collection.RowConvertor;
import com.astamuse.asta4d.web.form.field.OptionValueMap;
import com.astamuse.asta4d.web.form.field.OptionValuePair;
import com.astamuse.asta4d.web.form.field.impl.SelectBoxDataPrepareRenderer;
import com.astamuse.asta4d.web.form.field.impl.SelectBoxRenderer;
import com.astamuse.asta4d.web.test.WebTestBase;
import com.astamuse.asta4d.web.test.form.FormRenderCase;

public class SelectBoxTest extends WebTestBase {

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
            FieldRenderBuilder builder = FieldRenderBuilder.of(SelectBoxRenderer.class);

            builder.addPrepare(new SelectBoxDataPrepareRenderer("nullvalue").setOptionData(createMap("x", "y", "z")));
            builder.addValue("nullvalue", null);

            builder.addPrepare(new SelectBoxDataPrepareRenderer("nullvalue-2").setOptionData(createMap("x", "y", "z", "")));
            builder.addValue("nullvalue-2", null);

            builder.addPrepare(new SelectBoxDataPrepareRenderer("emptyvalue").setOptionData(createMap("x", "y", "z")));
            builder.addValue("emptyvalue", "");

            builder.addPrepare(new SelectBoxDataPrepareRenderer("emptyvalue-2").setOptionData(createMap("x", "y", "z", "")));
            builder.addValue("emptyvalue-2", "");

            builder.addPrepare(new SelectBoxDataPrepareRenderer("spacevalue").setOptionData(createMap("x", "y", "z")));
            builder.addValue("spacevalue", " ");

            builder.addPrepare(new SelectBoxDataPrepareRenderer("spacevalue-2").setOptionData(createMap("x", "y", "z", " ")));
            builder.addValue("spacevalue-2", " ");

            builder.addPrepare(new SelectBoxDataPrepareRenderer("xvalue").setOptionData(createMap("y", "z")));
            builder.addValue("xvalue", "x");

            builder.addPrepare(new SelectBoxDataPrepareRenderer("xvalue-2").setOptionData(createMap("x", "y", "z", "")));
            builder.addValue("xvalue-2", "x");

            builder.addPrepare(new SelectBoxDataPrepareRenderer("yvalue").addOptionGroup("A", createMap("x", "z")).addOptionGroup("AA",
                    createMap("xx", "zz")));
            builder.addValue("yvalue", "y");

            builder.addPrepare(new SelectBoxDataPrepareRenderer("yvalue-2").addOptionGroup("A", createMap("x", "y", "z")).addOptionGroup(
                    "AA", createMap("xx", "yy", "zz")));
            builder.addValue("yvalue-2", "y");

            return builder.toRenderer(true);
        }

        public Renderer normalDisplayValue() {
            FieldRenderBuilder builder = FieldRenderBuilder.of(SelectBoxRenderer.class);

            builder.addPrepare(new SelectBoxDataPrepareRenderer("nullvalue").setOptionData(createMap("x", "y", "z")));
            builder.addValue("nullvalue", null);

            builder.addPrepare(new SelectBoxDataPrepareRenderer("nullvalue-2").setOptionData(createMap("x", "y", "z", "")));
            builder.addValue("nullvalue-2", null);

            builder.addPrepare(new SelectBoxDataPrepareRenderer("emptyvalue").setOptionData(createMap("x", "y", "z")));
            builder.addValue("emptyvalue", "");

            builder.addPrepare(new SelectBoxDataPrepareRenderer("emptyvalue-2").setOptionData(createMap("x", "y", "z", "")));
            builder.addValue("emptyvalue-2", "");

            builder.addPrepare(new SelectBoxDataPrepareRenderer("spacevalue").setOptionData(createMap("x", "y", "z")));
            builder.addValue("spacevalue", " ");

            builder.addPrepare(new SelectBoxDataPrepareRenderer("spacevalue-2").setOptionData(createMap("x", "y", "z", " ")));
            builder.addValue("spacevalue-2", " ");

            builder.addPrepare(new SelectBoxDataPrepareRenderer("xvalue").setOptionData(createMap("y", "z")));
            builder.addValue("xvalue", "x");

            builder.addPrepare(new SelectBoxDataPrepareRenderer("xvalue-2").setOptionData(createMap("x", "y", "z", "")));
            builder.addValue("xvalue-2", "x");

            builder.addPrepare(new SelectBoxDataPrepareRenderer("yvalue").addOptionGroup("A", createMap("x", "z")).addOptionGroup("AA",
                    createMap("xx", "zz")));
            builder.addValue("yvalue", "y");

            builder.addPrepare(new SelectBoxDataPrepareRenderer("yvalue-2").addOptionGroup("A", createMap("x", "y", "z")).addOptionGroup(
                    "AA", createMap("xx", "yy", "zz")));
            builder.addValue("yvalue-2", "y");

            builder.addPrepare(new SelectBoxDataPrepareRenderer("zvalue").addOptionGroup("A", createMap("x", "y")).addOptionGroup("AA",
                    createMap("xx", "yy")));
            builder.addValue("zvalue", "z");

            builder.addPrepare(new SelectBoxDataPrepareRenderer("zvalue-2").addOptionGroup("A", createMap("x", "y", "z")).addOptionGroup(
                    "AA", createMap("xx", "yy", "zz")));
            builder.addValue("zvalue-2", "z");

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

    @SuppressWarnings("deprecation")
    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "Option list without group is not allowed.*")
    public void testConflictedGroupAndOption() {
        new SelectBoxDataPrepareRenderer("zvalue-2").addOptionGroup("A", createMap("x", "y", "z")).setOptionData(
                createMap("xx", "yy", "zz"));
    }

    @SuppressWarnings("deprecation")
    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "Option list group is not allowed.*")
    public void testConflictedGroupAndOption2() {
        new SelectBoxDataPrepareRenderer("zvalue-2").setOptionData(createMap("xx", "yy", "zz")).addOptionGroup("A",
                createMap("x", "y", "z"));
    }

    public void testNormalEdit() {
        new FormRenderCase("/SelectBox_normalEdit.html");
    }

    public void testNormalDisplay() {
        new FormRenderCase("/SelectBox_normalDisplay.html");
    }

    public void testStaticOptionEdit() {
        new FormRenderCase("/SelectBox_staticOptionEdit.html");
    }

    public void testStaticOptionDisplay() {
        new FormRenderCase("/SelectBox_staticOptionDisplay.html");
    }
}
