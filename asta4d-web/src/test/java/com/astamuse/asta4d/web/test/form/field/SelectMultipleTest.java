package com.astamuse.asta4d.web.test.form.field;

import org.testng.annotations.Test;

import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.util.collection.RowConvertor;
import com.astamuse.asta4d.web.form.field.OptionValueMap;
import com.astamuse.asta4d.web.form.field.OptionValuePair;
import com.astamuse.asta4d.web.form.field.impl.SelectMultipleRenderer;
import com.astamuse.asta4d.web.form.field.impl.SelectPrepareRenderer;
import com.astamuse.asta4d.web.test.WebTestBase;
import com.astamuse.asta4d.web.test.form.FormRenderCase;

public class SelectMultipleTest extends WebTestBase {

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
            FieldRenderBuilder builder = FieldRenderBuilder.of(SelectMultipleRenderer.class);

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

        public Renderer normalEditMultiValue() {
            FieldRenderBuilder builder = FieldRenderBuilder.of(SelectMultipleRenderer.class);

            builder.addPrepare(new SelectPrepareRenderer("nullvalue").setOptionData(createMap("x", "y", "z")));
            builder.addValue("nullvalue", new Object[] { "a", null });

            builder.addPrepare(new SelectPrepareRenderer("nullvalue-2").setOptionData(createMap("x", "y", "z", "")));
            builder.addValue("nullvalue-2", new Object[] { "x", null });

            builder.addPrepare(new SelectPrepareRenderer("emptyvalue").setOptionData(createMap("x", "y", "z")));
            builder.addValue("emptyvalue", new Object[] { "x", "" });

            builder.addPrepare(new SelectPrepareRenderer("emptyvalue-2").setOptionData(createMap("x", "y", "z", "")));
            builder.addValue("emptyvalue-2", new Object[] { "a", "" });

            builder.addPrepare(new SelectPrepareRenderer("spacevalue").setOptionData(createMap("x", "y", "z")));
            builder.addValue("spacevalue", new Object[] { "a", " " });

            builder.addPrepare(new SelectPrepareRenderer("spacevalue-2").setOptionData(createMap("x", "y", "z", " ")));
            builder.addValue("spacevalue-2", new Object[] { "a", " " });

            builder.addPrepare(new SelectPrepareRenderer("xvalue").setOptionData(createMap("y", "z")));
            builder.addValue("xvalue", new Object[] { "x", "a" });

            builder.addPrepare(new SelectPrepareRenderer("xvalue-2").setOptionData(createMap("x", "y", "z", "")));
            builder.addValue("xvalue-2", new Object[] { "x", "y", "a" });

            builder.addPrepare(new SelectPrepareRenderer("yvalue").addOptionGroup("A", createMap("x", "z")).addOptionGroup("AA",
                    createMap("xx", "zz")));
            builder.addValue("yvalue", new Object[] { "y", "zz" });

            builder.addPrepare(new SelectPrepareRenderer("yvalue-2").addOptionGroup("A", createMap("x", "y", "z")).addOptionGroup("AA",
                    createMap("xx", "yy", "zz")));
            builder.addValue("yvalue-2", new Object[] { "y", "yy" });

            return builder.toRenderer(true);
        }

        public Renderer normalDisplayValue() {
            FieldRenderBuilder builder = FieldRenderBuilder.of(SelectMultipleRenderer.class);

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

        public Renderer normalDisplayMultiValue() {
            FieldRenderBuilder builder = FieldRenderBuilder.of(SelectMultipleRenderer.class);

            builder.addPrepare(new SelectPrepareRenderer("nullvalue").setOptionData(createMap("x", "y", "z")));
            builder.addValue("nullvalue", new Object[] { "a", null });

            builder.addPrepare(new SelectPrepareRenderer("nullvalue-2").setOptionData(createMap("x", "y", "z", "")));
            builder.addValue("nullvalue-2", new Object[] { "x", null });

            builder.addPrepare(new SelectPrepareRenderer("emptyvalue").setOptionData(createMap("x", "y", "z")));
            builder.addValue("emptyvalue", new Object[] { "a", "" });

            builder.addPrepare(new SelectPrepareRenderer("emptyvalue-2").setOptionData(createMap("x", "y", "z", "")));
            builder.addValue("emptyvalue-2", new Object[] { "y", "" });

            builder.addPrepare(new SelectPrepareRenderer("spacevalue").setOptionData(createMap("x", "y", "z")));
            builder.addValue("spacevalue", new Object[] { "a", " " });

            builder.addPrepare(new SelectPrepareRenderer("spacevalue-2").setOptionData(createMap("x", "y", "z", " ")));
            builder.addValue("spacevalue-2", new Object[] { "z", " " });

            builder.addPrepare(new SelectPrepareRenderer("xvalue").setOptionData(createMap("y", "z")));
            builder.addValue("xvalue", new Object[] { "x", "" });

            builder.addPrepare(new SelectPrepareRenderer("xvalue-2").setOptionData(createMap("x", "y", "z", "")));
            builder.addValue("xvalue-2", new Object[] { "x", "z" });

            builder.addPrepare(new SelectPrepareRenderer("yvalue").addOptionGroup("A", createMap("x", "z")).addOptionGroup("AA",
                    createMap("xx", "zz")));
            builder.addValue("yvalue", new Object[] { "y", "a" });

            builder.addPrepare(new SelectPrepareRenderer("yvalue-2").addOptionGroup("A", createMap("x", "y", "z")).addOptionGroup("AA",
                    createMap("xx", "yy", "zz")));
            builder.addValue("yvalue-2", new Object[] { "y", "zz" });

            builder.addPrepare(new SelectPrepareRenderer("zvalue").addOptionGroup("A", createMap("x", "y")).addOptionGroup("AA",
                    createMap("xx", "yy")));
            builder.addValue("zvalue", new Object[] { "a", "z" });

            builder.addPrepare(new SelectPrepareRenderer("zvalue-2").addOptionGroup("A", createMap("x", "y", "z")).addOptionGroup("AA",
                    createMap("xx", "yy", "zz")));
            builder.addValue("zvalue-2", new Object[] { "z", "zz", "a" });

            return builder.toRenderer(false);
        }

        public Renderer staticOptionEditValue() {
            FieldRenderBuilder builder = FieldRenderBuilder.of(SelectMultipleRenderer.class);

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

        public Renderer staticOptionEditMultiValue() {
            FieldRenderBuilder builder = FieldRenderBuilder.of(SelectMultipleRenderer.class);

            builder.addValue("nullvalue", new Object[] { "a", null });

            builder.addValue("nullvalue-2", new Object[] { "z", null });

            builder.addValue("emptyvalue", new Object[] { "a", "" });

            builder.addValue("emptyvalue-2", new Object[] { "a", "" });

            builder.addValue("spacevalue", new Object[] { "a", " " });

            builder.addValue("spacevalue-2", new Object[] { "z", " " });

            builder.addValue("xvalue", new Object[] { "x", "a" });

            builder.addValue("xvalue-2", new Object[] { "x", "a" });

            builder.addValue("yvalue", new Object[] { "y", "a" });

            builder.addValue("yvalue-2", new Object[] { "y", "z" });

            return builder.toRenderer(true);
        }

        public Renderer staticOptionDisplayValue() {
            FieldRenderBuilder builder = FieldRenderBuilder.of(SelectMultipleRenderer.class);

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

        public Renderer staticOptionDisplayMultiValue() {
            FieldRenderBuilder builder = FieldRenderBuilder.of(SelectMultipleRenderer.class);
            builder.addValue("nullvalue", new Object[] { "a", null });

            builder.addValue("nullvalue-2", new Object[] { "z", null });

            builder.addValue("emptyvalue", new Object[] { "a", "" });

            builder.addValue("emptyvalue-2", new Object[] { "a", "" });

            builder.addValue("spacevalue", new Object[] { "a", " " });

            builder.addValue("spacevalue-2", new Object[] { "z", " " });

            builder.addValue("xvalue", new Object[] { "x", "a" });

            builder.addValue("xvalue-2", new Object[] { "x", "a" });

            builder.addValue("yvalue", new Object[] { "y", "a" });

            builder.addValue("yvalue-2", new Object[] { "y", "z" });

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
        new FormRenderCase("/SelectMultiple_normalEdit.html");
    }

    public void testNormalEditMultiValue() {
        new FormRenderCase("/SelectMultiple_normalEditMultiValue.html");
    }

    public void testNormalDisplay() {
        new FormRenderCase("/SelectMultiple_normalDisplay.html");
    }

    public void testNormalDisplayMultiValue() {
        new FormRenderCase("/SelectMultiple_normalDisplayMultiValue.html");
    }

    public void testStaticOptionEdit() {
        new FormRenderCase("/SelectMultiple_staticOptionEdit.html");
    }

    public void testStaticOptionEditMultiValue() {
        new FormRenderCase("/SelectMultiple_staticOptionEditMultiValue.html");
    }

    public void testStaticOptionDisplay() {
        new FormRenderCase("/SelectMultiple_staticOptionDisplay.html");
    }

    public void testStaticOptionDisplayMultiValue() {
        new FormRenderCase("/SelectMultiple_staticOptionDisplayMultiValue.html");
    }
}
