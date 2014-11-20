package com.astamuse.asta4d.test.unit;

import static com.astamuse.asta4d.render.SpecialRenderer.Clear;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.jsoup.nodes.Element;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.astamuse.asta4d.render.ChildReplacer;
import com.astamuse.asta4d.render.GoThroughRenderer;
import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.render.test.RendererTestException;
import com.astamuse.asta4d.render.test.RendererTester;
import com.astamuse.asta4d.render.test.TestableElementWrapper;
import com.astamuse.asta4d.test.render.infra.BaseTest;
import com.astamuse.asta4d.util.ElementUtil;
import com.astamuse.asta4d.util.collection.ListConvertUtil;
import com.astamuse.asta4d.util.collection.RowConvertor;

public class RenderTesterTest extends BaseTest {

    @Test
    public void testGetSingle() {

        // prepare test target
        Renderer render = new GoThroughRenderer();
        render.add("#someIdForInt", 12345);
        render.add("#someIdForLong", 12345L);
        render.add("#someIdForBool", true);
        render.add("#someIdForStr", "a str");
        render.add("#someIdForNull", (Object) null);
        render.add("#someIdForClear", Clear);

        Element newChild = ElementUtil.parseAsSingle("<div></div>");
        render.add("#someIdForElementSetter", new ChildReplacer(newChild));

        render.add("#someIdForElement", ElementUtil.parseAsSingle("<div>eee</div>"));

        render.add("#someIdForRenderer", Renderer.create("#value", "value"));

        // perform test
        RendererTester tester = RendererTester.forRenderer(render);

        Assert.assertFalse(tester.noOp());

        Assert.assertEquals(tester.get("#someIdForInt"), 12345);
        Assert.assertEquals(tester.get("#someIdForLong"), 12345L);
        Assert.assertEquals(tester.get("#someIdForBool"), true);
        Assert.assertEquals(tester.get("#someIdForStr"), "a str");
        Assert.assertEquals(tester.get("#someIdForNull"), null);
        Assert.assertEquals(tester.get("#someIdForClear"), Clear);

        Assert.assertFalse(tester.noOp("#someIdForClear"));
        Assert.assertTrue(tester.noOp("#notexistop"));

        Assert.assertEquals(tester.get("#someIdForElementSetter"), new ChildReplacer(ElementUtil.parseAsSingle("<div></div>")));

        Assert.assertEquals(tester.get("#someIdForElement"), TestableElementWrapper.parse("<div>eee</div>"));

        RendererTester recursiveTester = RendererTester.forRenderer((Renderer) tester.get("#someIdForRenderer"));
        Assert.assertEquals(recursiveTester.get("#value"), "value");

        // noop test
        tester = RendererTester.forRenderer(Renderer.create());
        Assert.assertTrue(tester.noOp());

    }

    @Test(expectedExceptions = RendererTestException.class, expectedExceptionsMessageRegExp = "There is no value to be rendered for selector(.*)")
    public void testGetSingleNotFound() {
        Renderer render = new GoThroughRenderer();
        render.add("#someId", 12345);

        RendererTester tester = RendererTester.forRenderer(render);
        Assert.assertEquals(tester.get("#someIdNotExist"), 12345);
    }

    @Test(expectedExceptions = RendererTestException.class, expectedExceptionsMessageRegExp = "(.*)as a list(.*)")
    public void testGetSingleFoundMore() {
        Renderer render = new GoThroughRenderer();
        render.add("#someId", Arrays.asList(123, 345));

        RendererTester tester = RendererTester.forRenderer(render);
        Assert.assertEquals(tester.get("#someId"), 123);
    }

    @Test(expectedExceptions = RendererTestException.class, expectedExceptionsMessageRegExp = "(.*)as a empty list(.*)")
    public void testGetSingleFoundMore2() {
        Renderer render = new GoThroughRenderer();
        render.add("#someId", Collections.emptyList());

        RendererTester tester = RendererTester.forRenderer(render);
        Assert.assertEquals(tester.get("#someId"), 123);
    }

    @Test(expectedExceptions = RendererTestException.class, expectedExceptionsMessageRegExp = "(.*)multiple times(.*)")
    public void testGetSingleFoundMore3() {
        Renderer render = new GoThroughRenderer();
        render.add("#someId", 123);

        render.add("#someId", 345);

        RendererTester tester = RendererTester.forRenderer(render);
        Assert.assertEquals(tester.get("#someId"), 123);
    }

    @Test
    public void testGetList() {
        // prepare test target
        Renderer render = new GoThroughRenderer();
        render.add("#someIdForInt", Arrays.asList(123, 456, 789));
        render.add("#someIdForLong", Arrays.asList(123L, 456L, 789L));
        render.add("#someIdForBool", Arrays.asList(true, true, false));
        render.add("#someIdForStr", Arrays.asList("str1", "str2", "str3"));

        Element newChild1 = ElementUtil.parseAsSingle("<div>1</div>");
        Element newChild2 = ElementUtil.parseAsSingle("<div>2</div>");

        render.add("#someIdForElementSetter", Arrays.asList(new ChildReplacer(newChild1), new ChildReplacer(newChild2)));

        render.add("#someIdForElement", Arrays.asList(newChild1, newChild2));

        // perform test
        RendererTester tester = RendererTester.forRenderer(render);
        Assert.assertEquals(tester.getAsList("#someIdForInt"), Arrays.asList(123, 456, 789));
        Assert.assertEquals(tester.getAsList("#someIdForLong"), Arrays.asList(123L, 456L, 789L));
        Assert.assertEquals(tester.getAsList("#someIdForBool"), Arrays.asList(true, true, false));
        Assert.assertEquals(tester.getAsList("#someIdForStr"), Arrays.asList("str1", "str2", "str3"));

        Assert.assertEquals(
                tester.getAsList("#someIdForElementSetter"),
                Arrays.asList(new ChildReplacer(ElementUtil.parseAsSingle("<div>1</div>")),
                        new ChildReplacer(ElementUtil.parseAsSingle("<div>2</div>"))));

        Assert.assertEquals(tester.getAsList("#someIdForElement"),
                Arrays.asList(TestableElementWrapper.parse("<div>1</div>"), TestableElementWrapper.parse("<div>2</div>")));

    }

    @Test
    public void testGetListAsRenderer() {
        Renderer render = new GoThroughRenderer();
        render.add("#someIdForRenderer", Arrays.asList(123, 456, 789), new RowConvertor<Integer, Renderer>() {
            @Override
            public Renderer convert(int rowIndex, Integer obj) {
                return Renderer.create("#id", "id-" + obj).add("#otherId", "otherId-" + obj);
            }
        });

        RendererTester tester = RendererTester.forRenderer(render);
        List<Renderer> renderList = tester.getAsList("#someIdForRenderer", Renderer.class);

        Assert.assertEquals(renderList.size(), 3);

        List<RendererTester> testerList = RendererTester.forRendererList(renderList);
        List<String> confirmIdList = Arrays.asList("id-123", "id-456", "id-789");

        for (int i = 0; i < testerList.size(); i++) {
            RendererTester recursiveTester = testerList.get(i);
            Assert.assertEquals(recursiveTester.get("#id"), confirmIdList.get(i));
        }

        // we can also write tests in a more functional way
        List<String> confirmOtherIdList = Arrays.asList("otherId-123", "otherId-456", "otherId-789");
        Assert.assertEquals(confirmOtherIdList, ListConvertUtil.transform(testerList, new RowConvertor<RendererTester, String>() {
            @Override
            public String convert(int rowIndex, RendererTester tester) {
                return (String) tester.get("#otherId");
            }
        }));

    }

    @Test
    public void testGetAttr() {
        Renderer render = new GoThroughRenderer();
        render.add("#id", "+class", "yyy");
        render.add("#id", "-class", "zzz");

        render.add("#id", "+class", "xxx");

        render.add("#id", "value", "hg");
        render.add("#id", "href", (Object) null);

        render.add("#X", "value", new Date(123456L));

        RendererTester tester = RendererTester.forRenderer(render);
        Assert.assertEquals(tester.getAttrAsList("#id", "+class"), Arrays.asList("yyy", "xxx"));
        Assert.assertEquals(tester.getAttr("#id", "-class"), "zzz");
        Assert.assertEquals(tester.getAttr("#id", "value"), "hg");
        Assert.assertEquals(tester.getAttr("#id", "href"), null);
        Assert.assertEquals(tester.getAttr("#X", "value"), new Date(123456L));

        Assert.assertFalse(tester.noOp("#id", "+class"));
        Assert.assertFalse(tester.noOp("#id", "-class"));
        Assert.assertFalse(tester.noOp("#id", "value"));

        Assert.assertTrue(tester.noOp("#id", "+cccc"));
        Assert.assertTrue(tester.noOp("#id", "-cccc"));
        Assert.assertTrue(tester.noOp("#id", "cccc"));

    }

    @Test(expectedExceptions = RendererTestException.class, expectedExceptionsMessageRegExp = "There is no value to be rendered for attr(.*)")
    public void testGetAttrNotFound() {
        Renderer render = new GoThroughRenderer();
        render.add("#id", "+class", "yyy");

        RendererTester tester = RendererTester.forRenderer(render);

        Assert.assertEquals(tester.getAttr("#id", "href"), null);

    }

    @Test(expectedExceptions = RendererTestException.class, expectedExceptionsMessageRegExp = "(.*)more than one values(.*)")
    public void testGetAttrFindMore() {
        Renderer render = new GoThroughRenderer();
        render.add("#id", "value", "yyy");
        render.add("#id", "value", "zzz");

        RendererTester tester = RendererTester.forRenderer(render);

        Assert.assertEquals(tester.getAttr("#id", "value"), "zzz");

    }

    @Test(expectedExceptions = RendererTestException.class, expectedExceptionsMessageRegExp = "This method is only for retrieving rendered value of \"\\+class\" and \"\\-class\" attr action")
    public void testGetAttrFindMore2() {
        Renderer render = new GoThroughRenderer();
        render.add("#id", "value", "yyy");
        render.add("#id", "value", "zzz");

        RendererTester tester = RendererTester.forRenderer(render);

        Assert.assertEquals(tester.getAttrAsList("#id", "value"), Arrays.asList("yyy", "zzz"));

    }

}
