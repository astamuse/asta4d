package com.astamuse.asta4d.web.test.form.field;

import org.testng.annotations.Test;

import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.util.collection.RowConvertor;
import com.astamuse.asta4d.web.form.field.OptionValueMap;
import com.astamuse.asta4d.web.form.field.OptionValuePair;
import com.astamuse.asta4d.web.form.field.impl.SelectPrepareRenderer;
import com.astamuse.asta4d.web.form.field.impl.SelectRenderer;
import com.astamuse.asta4d.web.test.WebTestBase;
import com.astamuse.asta4d.web.test.form.FormRenderCase;

public class SelectTest extends WebTestBase {

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
            FieldRenderBuilder builder = FieldRenderBuilder.of(SelectRenderer.class);

            builder.addPrepare(new SelectPrepareRenderer("nullvalue").setOptionData(createMap("x", "y", "z")));
            builder.addValue("nullvalue", null);

            builder.addPrepare(new SelectPrepareRenderer("nullvalue-2").setOptionData(createMap("x", "y", "z", "")));
            builder.addValue("nullvalue-2", null);

            builder.addPrepare(new SelectPrepareRenderer("emptyvalue").setOptionData(createMap("x", "y", "z")));
            builder.addValue("emptyvalue", "");

            builder.addPrepare(new SelectPrepareRenderer("emptyvalue-2").setOptionData(createMap("x", "y", "z", "")));
            builder.addValue("emptyvalue-2", "");

            builder.addPrepare(new SelectPrepareRenderer("spacevalue").setOptionData(createMap("x", "y", "z")));
            builder.addValue("spacevalue", " ");

            builder.addPrepare(new SelectPrepareRenderer("spacevalue-2").setOptionData(createMap("x", "y", "z", " ")));
            builder.addValue("spacevalue-2", " ");

            builder.addPrepare(new SelectPrepareRenderer("xvalue").setOptionData(createMap("y", "z")));
            builder.addValue("xvalue", "x");

            builder.addPrepare(new SelectPrepareRenderer("xvalue-2").setOptionData(createMap("x", "y", "z", "")));
            builder.addValue("xvalue-2", "x");

            builder.addPrepare(new SelectPrepareRenderer("yvalue").addOptionGroup("A", createMap("x", "z")).addOptionGroup("AA",
                    createMap("xx", "zz")));
            builder.addValue("yvalue", "y");

            builder.addPrepare(new SelectPrepareRenderer("yvalue-2").addOptionGroup("A", createMap("x", "y", "z")).addOptionGroup("AA",
                    createMap("xx", "yy", "zz")));
            builder.addValue("yvalue-2", "y");

            return builder.toRenderer(true);
        }

        public Renderer normalDisplayValue() {
            FieldRenderBuilder builder = FieldRenderBuilder.of(SelectRenderer.class);

            builder.addPrepare(new SelectPrepareRenderer("nullvalue").setOptionData(createMap("x", "y", "z")));
            builder.addValue("nullvalue", null);

            builder.addPrepare(new SelectPrepareRenderer("nullvalue-2").setOptionData(createMap("x", "y", "z", "")));
            builder.addValue("nullvalue-2", null);

            builder.addPrepare(new SelectPrepareRenderer("emptyvalue").setOptionData(createMap("x", "y", "z")));
            builder.addValue("emptyvalue", "");

            builder.addPrepare(new SelectPrepareRenderer("emptyvalue-2").setOptionData(createMap("x", "y", "z", "")));
            builder.addValue("emptyvalue-2", "");

            builder.addPrepare(new SelectPrepareRenderer("spacevalue").setOptionData(createMap("x", "y", "z")));
            builder.addValue("spacevalue", " ");

            builder.addPrepare(new SelectPrepareRenderer("spacevalue-2").setOptionData(createMap("x", "y", "z", " ")));
            builder.addValue("spacevalue-2", " ");

            builder.addPrepare(new SelectPrepareRenderer("xvalue").setOptionData(createMap("y", "z")));
            builder.addValue("xvalue", "x");

            builder.addPrepare(new SelectPrepareRenderer("xvalue-2").setOptionData(createMap("x", "y", "z", "")));
            builder.addValue("xvalue-2", "x");

            builder.addPrepare(new SelectPrepareRenderer("yvalue").addOptionGroup("A", createMap("x", "z")).addOptionGroup("AA",
                    createMap("xx", "zz")));
            builder.addValue("yvalue", "y");

            builder.addPrepare(new SelectPrepareRenderer("yvalue-2").addOptionGroup("A", createMap("x", "y", "z")).addOptionGroup("AA",
                    createMap("xx", "yy", "zz")));
            builder.addValue("yvalue-2", "y");

            builder.addPrepare(new SelectPrepareRenderer("zvalue").addOptionGroup("A", createMap("x", "y")).addOptionGroup("AA",
                    createMap("xx", "yy")));
            builder.addValue("zvalue", "z");

            builder.addPrepare(new SelectPrepareRenderer("zvalue-2").addOptionGroup("A", createMap("x", "y", "z")).addOptionGroup("AA",
                    createMap("xx", "yy", "zz")));
            builder.addValue("zvalue-2", "z");

            return builder.toRenderer(false);
        }

        public Renderer staticOptionEditValue() {
            FieldRenderBuilder builder = FieldRenderBuilder.of(SelectRenderer.class);

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
            FieldRenderBuilder builder = FieldRenderBuilder.of(SelectRenderer.class);

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
        new SelectPrepareRenderer("zvalue-2").addOptionGroup("A", createMap("x", "y", "z")).setOptionData(createMap("xx", "yy", "zz"));
    }

    @SuppressWarnings("deprecation")
    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "Option list group is not allowed.*")
    public void testConflictedGroupAndOption2() {
        new SelectPrepareRenderer("zvalue-2").setOptionData(createMap("xx", "yy", "zz")).addOptionGroup("A", createMap("x", "y", "z"));
    }

    public void testNormalEdit() {
        new FormRenderCase("/Select_normalEdit.html");
    }

    public void testNormalDisplay() {
        new FormRenderCase("/Select_normalDisplay.html");
    }

    public void testStaticOptionEdit() {
        new FormRenderCase("/Select_staticOptionEdit.html");
    }

    public void testStaticOptionDisplay() {
        new FormRenderCase("/Select_staticOptionDisplay.html");
    }
}
