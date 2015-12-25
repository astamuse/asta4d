/*
 * Copyright 2014 astamuse company,Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.astamuse.asta4d.test.unit;

import static com.astamuse.asta4d.render.SpecialRenderer.Clear;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.jsoup.nodes.Element;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.astamuse.asta4d.render.ChildReplacer;
import com.astamuse.asta4d.render.Renderable;
import com.astamuse.asta4d.render.Renderer;
import com.astamuse.asta4d.render.test.RendererTestException;
import com.astamuse.asta4d.render.test.RendererTester;
import com.astamuse.asta4d.render.test.TestableElementWrapper;
import com.astamuse.asta4d.test.render.infra.BaseTest;
import com.astamuse.asta4d.util.ElementUtil;
import com.astamuse.asta4d.util.collection.RowConvertor;

public class RenderTesterTest extends BaseTest {

    @Test
    public void testGetSingle() {

        // prepare test target
        Renderer render = Renderer.create();
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

        render.add("#someIdForRendable", () -> {
            return Renderer.create("#id", "xx");
        });

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

        recursiveTester = RendererTester.forRenderer(((Renderable) tester.get("#someIdForRendable")).render());
        Assert.assertEquals(recursiveTester.get("#id"), "xx");

        // noop test
        tester = RendererTester.forRenderer(Renderer.create());
        Assert.assertTrue(tester.noOp());

    }

    @Test(expectedExceptions = RendererTestException.class, expectedExceptionsMessageRegExp = "There is no value to be rendered for selector(.*)")
    public void testGetSingleNotFound() {
        Renderer render = Renderer.create();
        render.add("#someId", 12345);

        RendererTester tester = RendererTester.forRenderer(render);
        Assert.assertEquals(tester.get("#someIdNotExist"), 12345);
    }

    @Test(expectedExceptions = RendererTestException.class, expectedExceptionsMessageRegExp = "(.*)as a list(.*)")
    public void testGetSingleFoundMore() {
        Renderer render = Renderer.create();
        render.add("#someId", Arrays.asList(123, 345));

        RendererTester tester = RendererTester.forRenderer(render);
        Assert.assertEquals(tester.get("#someId"), 123);
    }

    @Test(expectedExceptions = RendererTestException.class, expectedExceptionsMessageRegExp = "(.*)as a empty list(.*)")
    public void testGetSingleFoundMore2() {
        Renderer render = Renderer.create();
        render.add("#someId", Collections.emptyList());

        RendererTester tester = RendererTester.forRenderer(render);
        Assert.assertEquals(tester.get("#someId"), 123);
    }

    @Test(expectedExceptions = RendererTestException.class, expectedExceptionsMessageRegExp = "(.*)multiple times(.*)")
    public void testGetSingleFoundMore3() {
        Renderer render = Renderer.create();
        render.add("#someId", 123);

        render.add("#someId", 345);

        RendererTester tester = RendererTester.forRenderer(render);
        Assert.assertEquals(tester.get("#someId"), 123);
    }

    @Test
    public void testGetList() {
        // prepare test target
        Renderer render = Renderer.create();
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

        Assert.assertEquals(tester.getAsList("#someIdForElementSetter"),
                Arrays.asList(new ChildReplacer(ElementUtil.parseAsSingle("<div>1</div>")),
                        new ChildReplacer(ElementUtil.parseAsSingle("<div>2</div>"))));

        Assert.assertEquals(tester.getAsList("#someIdForElement"),
                Arrays.asList(TestableElementWrapper.parse("<div>1</div>"), TestableElementWrapper.parse("<div>2</div>")));

    }

    @Test
    public void testGetListAsRenderer() {
        Renderer render = Renderer.create();
        render.add("#someIdForRenderer", Arrays.asList(123, 456, 789), new RowConvertor<Integer, Renderer>() {
            @Override
            public Renderer convert(int rowIndex, Integer obj) {
                return Renderer.create("#id", "id-" + obj).add("#otherId", "otherId-" + obj);
            }
        });
        render.add("#someIdForStream", Arrays.asList(123, 456, 789).stream().map((i) -> {
            return Renderer.create("#id", "id-" + i).add("#otherId", "otherId-" + i);
        }));

        RendererTester tester = RendererTester.forRenderer(render);
        for (String selector : Arrays.asList("#someIdForRenderer", "#someIdForStream")) {
            // to test by traditional way
            {
                List<RendererTester> testerList = tester.getAsRendererTesterList(selector);
                List<String> confirmIdList = Arrays.asList("id-123", "id-456", "id-789");

                Assert.assertEquals(testerList.size(), 3);
                for (int i = 0; i < testerList.size(); i++) {
                    RendererTester recursiveTester = testerList.get(i);
                    Assert.assertEquals(recursiveTester.get("#id"), confirmIdList.get(i));
                }
            }

            // to test by more functional way
            {
                List<String> confirmOtherIdList = Arrays.asList("otherId-123", "otherId-456", "otherId-789");
                List<RendererTester> testerList = tester.getAsRendererTesterList(selector);
                Assert.assertEquals(testerList.stream().map((t) -> {
                    return t.get("#otherId");
                }).collect(Collectors.toList()), confirmOtherIdList);
            }

        }

    }

    @Test
    public void testGetAttr() {
        Renderer render = Renderer.create();
        render.add("#id", "+class", "yyy");
        render.add("#id", "-class", "zzz");

        render.add("#id", "+class", "xxx");
        render.add("#id", "value", new Date(123456L));
        render.add("#id", "href", (Object) null);

        render.add("#idstr", "value", "hg");
        render.add("#idint", "value", 3);
        render.add("#idlong", "value", 3L);
        render.add("#idbool", "value", true);

        RendererTester tester = RendererTester.forRenderer(render);
        Assert.assertEquals(tester.getAttrAsList("#id", "+class"), Arrays.asList("yyy", "xxx"));
        Assert.assertEquals(tester.getAttr("#id", "-class"), "zzz");
        Assert.assertEquals(tester.getAttr("#id", "value"), new Date(123456L));
        Assert.assertEquals(tester.getAttr("#id", "href"), null);

        Assert.assertEquals(tester.getAttr("#idstr", "value"), "hg");
        Assert.assertEquals(tester.getAttr("#idint", "value"), "3");
        Assert.assertEquals(tester.getAttr("#idlong", "value"), "3");
        Assert.assertEquals(tester.getAttr("#idbool", "value"), "true");

        Assert.assertFalse(tester.noOp("#id", "+class"));
        Assert.assertFalse(tester.noOp("#id", "-class"));
        Assert.assertFalse(tester.noOp("#id", "value"));
        Assert.assertFalse(tester.noOp("#id", "href"));

        Assert.assertFalse(tester.noOp("#idstr", "value"));
        Assert.assertFalse(tester.noOp("#idint", "value"));
        Assert.assertFalse(tester.noOp("#idlong", "value"));
        Assert.assertFalse(tester.noOp("#idbool", "value"));

        Assert.assertTrue(tester.noOp("#id", "+cccc"));
        Assert.assertTrue(tester.noOp("#id", "-cccc"));
        Assert.assertTrue(tester.noOp("#id", "cccc"));

    }

    @Test(expectedExceptions = RendererTestException.class, expectedExceptionsMessageRegExp = "There is no value to be rendered for attr(.*)")
    public void testGetAttrNotFound() {
        Renderer render = Renderer.create();
        render.add("#id", "+class", "yyy");

        RendererTester tester = RendererTester.forRenderer(render);

        Assert.assertEquals(tester.getAttr("#id", "href"), null);

    }

    @Test(expectedExceptions = RendererTestException.class, expectedExceptionsMessageRegExp = "(.*)more than one values(.*)")
    public void testGetAttrFindMore() {
        Renderer render = Renderer.create();
        render.add("#id", "value", "yyy");
        render.add("#id", "value", "zzz");

        RendererTester tester = RendererTester.forRenderer(render);

        Assert.assertEquals(tester.getAttr("#id", "value"), "zzz");

    }

    @Test(expectedExceptions = RendererTestException.class, expectedExceptionsMessageRegExp = "This method is only for retrieving rendered value of \"\\+class\" and \"\\-class\" attr action")
    public void testGetAttrFindMore2() {
        Renderer render = Renderer.create();
        render.add("#id", "value", "yyy");
        render.add("#id", "value", "zzz");

        RendererTester tester = RendererTester.forRenderer(render);

        Assert.assertEquals(tester.getAttrAsList("#id", "value"), Arrays.asList("yyy", "zzz"));

    }

}
