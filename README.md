## What is Asta4D

Asta4D is a web application framework which is friendly to designer and flexible to developer. Asta4D affords high productivity than traditional MVC architecture by View First architecture. It also allows front-end engineers and back-end engineers work independently without interference by separating rendering logic from template files.

Asta4D is inspired by [lift](http://liftweb.net/)  which is a famous scala web application framework and it is developed by astamuse company Ltd. locating at Tokyo Japan. We are concentrating on global innovation support and developing Asta4D for our own services. Currently, Asta4D is driving our new service development.

## Why Asta4D
In the past decade, plenty of Java based web application frameworks are generated. Especially the MVC 
architecture and JSP tag libs (or other traditional template technologies) that has greatly released our 
productivity. But unfortunately, we are still suffering from the following situations:

1. The designers or front-end engineers are keeping complaining the mixed-in dynamic code, as they disturb their efforts of redesigning the page style or structure. And in the mean time, the back-end developers are also complaining that the front-end guys break the working page frequently,  because redesign or the new design is hard to merge due to the huge cost of source refactoring. 
1. The developers are complaining about the poor functionalities of template language which they are using and tired from the various magic skills for complex rendering logic.
1. The developers are discontented with the counterproductivity of MVC architecture and desire a more efficient approach like traditional PHP/ASP development.

Thus, we created Asta4D. Currently, Asta4D is driving our service site:[astamuse.com](http://astamuse.com)

## How Asta4D helps us

Asta4D is our solution to combat those issues. Thanks to lift, from where we learn a lot. We designed Asta4D complying with the following points:

1. Separate template and rendering logic
    
    Asta4D affords front-end engineers a friendly environment by separating rendering logic from template files which are pure html files. At the mean time, back-end engineers can use the powerful Java language to implement the rendering logic without being suffering from the "poor and sometimes magic" template languages.

    There is no dynamic code in template file. An Asta4D template file is always a pure HTML file which can be easily maintained by front-end developers, it is very design friendly and we can reduce the workload for source refactoring by over 90%.


    ```html
    <section>
        <article>
            <div afd:render="SimpleSnippet">dummy text</div>
            <afd:snippet render="SimpleSnippet:setProfile">
                <p id="name">name:<span>dummy name</span></p>
                <p id="age">age:<span>0</span></p>
            </afd:snippet>
        </article>
    </section>
    ```

    In the snippet class, we use traditional CSS selector to reference rendering target, amazing and powerful.
    
    
    ```java
    public class SimpleSnippet {
    
        public Renderer render(String name) {
            if (StringUtils.isEmpty(name)) {
                name = "Asta4D";
            }
            Element element = ElementUtil.parseAsSingle("<span>Hello " + name + "!</span>");
            return Renderer.create("*", element);
        }
    
        public Renderer setProfile() {
            Renderer render = new GoThroughRenderer();
            render.add("p#name span", "asta4d");
            render.add("p#age span", "20");
            return render;
        }
    }
    ```

1. Testable Rendering logic

    All of the rendering logic of Asta4D is testable and you can simply test them by write simple junit cases, which can replace over than half of selenium tests.

    ```java
        // prepare test target
        Renderer render = new GoThroughRenderer();
        render.add("#someIdForInt", 12345);
    
        // perform test
        RendererTester tester = RendererTester.forRenderer(render);
        Assert.assertEquals(tester.get("#someIdForInt"), 12345);
    
    ```
    
    Rendering for list data can be performed as well
    
    ```java
        // prepare test target
        Renderer render = new GoThroughRenderer();
        render.add("#someIdForInt", Arrays.asList(123, 456, 789));
    
        // perform test
        RendererTester tester = RendererTester.forRenderer(render);
        Assert.assertEquals(tester.getAsList("#someIdForInt"), Arrays.asList(123, 456, 789));
    
    ```
    
    [Further samples for test](https://github.com/astamuse/asta4d/blob/develop/asta4d-core/src/test/java/com/astamuse/asta4d/test/unit/RenderTesterTest.java)

1. High security of being immune from cross-site(XSS/CSRF)
    
    Asta4D is, by nature, immune from cross-site(XSS/CSRF) problems. You do not need to take care of cross-site any more. All the rendered value would be escaped by default and your clients have no chance to put malicious contents to your server.

1. View first without controller
    
    Asta4D also affords higher productivity than traditional MVC architecture by View First mechanism. And it is also easier to change than MVC architecture.

    There is no a controller which dispatches requests. All the requests will be dispatched by a sort of predefined URL matching rules and will be handled by request handlers. 
    
    ```java
    rules.add("/app/", "/templates/index.html");
    
    rules.add("/app/handler")
         .handler(LoginHandler.class)
         .handler(EchoHandler.class)
         .forward(LoginFailure.class, "/templates/error.html")
         .forward("/templates/success.html");
    ```

1. Isolate side effect with request handler
    
    Asta4D imports the conception of "side-effect" from functional programming languages and separating the "side-effect" by request handlers, which afford more flexibility on page rendering because the view layer is side-effect free now. Therefore Asta4D allows parallel page rendering in multiple threads as a built-in feature.

## What does "Asta4D" means

The name of Asta4D is from our company's name: astamuse. We explain the "4D" as following ways:

1. For Designer
    
    Asta4D consider the design friendliness as the most important factor of itself. We hope web designers can fulfil their maximum potential of creativity without squandering their time on the back-end technologies which they could never be adept at.

1. For developer
    
    We hope Asta4D can help developers to achieve their work more easily. Developers would never be afflicted with complex rendering logic because they can use powerful Java language to do whatever they want since the rendering has been split from template files. View first also releases developers from the cumbersome MVC architecture, now they have more time to have a cup of coffee.

1. 4 Dimension
    
    We believe that Asta4D can act as a wormhole that connects the front-end and the back-end. We can move quicker by Asta4D just like we are going through the 4 dimensional space.


## Quick start

[Online Sample](http://asta4d-sample.xzer.cloudbees.net/)

[JavaDoc](http://astamuse.github.io/asta4d/javadoc/)

There is a maven archetype for asta4d. If you want to start with the archetype, you have to [install Maven 3](http://maven.apache.org/download.cgi) at first. After installed Maven 3, create  the sample project by the following command:

    ```
    mvn archetype:generate                       \
        -DarchetypeGroupId=com.astamuse          \
        -DarchetypeArtifactId=asta4d-archetype   \
        -DarchetypeVersion=0.14.2.10               \
        -DgroupId=<your.groupid>                 \
        -DartifactId=<your-artifactId>
    ```

or simply follow the wizard by filtered list:

    ```
    mvn archetype:generate -Dfilter=com.astamuse:asta4d-archetype
    ```

After the archetype is created, enter the folder which has a "pom.xml" file, run the following command:

    ```
    mvn jetty:run
    ```
 
Then you can access the sample project by http://localhost:8080, there are source samples shown, it is a good start from reading the samples.
After you confirm the sample project is OK, you can add your own url mapping rules to /src/main/java/.../.../UrlRules.java,
and also you can add your own html template files to /src/main/webapp.

Additionally, there is an on working [English user guide](http://astamuse.github.com/asta4d/userguide/index.html) which is updated at irregular intervals. There is also an obsolete [Japanese document](http://astamuse.github.com/asta4d/userguide/index_jp.html) and there is something changed from it had been written.

## Best practices

-   Use class name selectors instead of others in most cases

    Frontend designers would change html structures frequently, using class selector can avoid modifying the backend sources every time the html changed.

    We made a convention that all the classes with prefix "X-" are faked and used by backend logics to mark the anchor points of data.

-   Use request handler to normalize requests

    Some pages holds multiple url patterns, you can normalize all the patterns by a request handler. 

    We also use request hanlders to prepare the "target data" for the target page. A significant point is that preparing "target data" does not mean MVC architecture, we just query a simple entity or build a pojo to **represent the normalized condition** of the target page. 

## Todo

Immediate tasks: 

-   cachable snippet

    A rendered snippet result should can be cached.

-   Rendering helper for validation
    
    Not implementing validaiton which should use third-party implementations such as [JSR 303/349](http://beanvalidation.org/), just help rendering validation result easier.

Want to do: 

-   convertable context data annotation

    Since Java does not support inheriting from annotation, we nned a mechanism to convert any annotation to the default @ContextData annotation for better producibility.

-   default value of context data
    
    The current @ContextData does not support declaring default value, we need support it and additionally Unified EL([JSR341](https://jcp.org/en/jsr/detail?id=341)) is desired.

    
## Release Notes
-   0.14.2.10
    
    FIX
    - deprecated reverse injection(it is not necessary and should be removed in future)
    - NullPointerException on removed nested snippet declared by "afd:render"

-   0.14.1.31
    
    ADD
    - allow default msg content for msg rendering
    - allow extra attribution and var declaration on remaped rules
    - allow initialize asta4d in spring mvc as template solution only
    
    FIX
    - predefined clear nodes are not removed correctly
    - wrong spelled method name
    - refactor rendering test mechanism for better test support(with minor bug fix)

-   0.12.30
    
    ADD
    - more sample references to sample projects
    - add fixVersion.sh to make release simpler
    
    FIX
    - minor bugs on handling SpecialRenderer#Clear
    - bugs in archetype
    - format of asta4d-doc(make it prettier)

-   0.12.13
    
    ADD
    - RendererTest can be used for unit test of Renderer now
    - Treat null rendering value as removing target node
    - More debug-friendly log messages
    - Some tests
    
    FIX
    - BinaryDataUtil does not handle file path of "classpath:" correctly
    - Does not handle default request handler instance correctly
    - Refactor for context map for scopes, the Session scope will not create new session any more

-   0.8.6
    
    ADD
    - Some debug friendly message
    - Redirect now can specify code 301 or 302
    - Allow initialize asta4d Configuration from external properties file
    
    FIX
    - Refactor request chain process, now we can perfectly handle request chain
    - A bug that post process of request interceptor will be executed multiple times
    - BinaryDataUtil does not handle file path correctly
    - Context does not be initialized before dispatch

-   0.7.24
    
    ADD
    - ico MIME type
    - A empty content provider which can be used to stop the request handler chain
    
    FIX
    - GenericPathHandler should get access url from Context
    - A bug when selector not found on rendering

-   0.7.22
    
    ADD
    - A request handler can be set as generic as request interceptor.
    - Access URL can be rewritten.
    - URL Rule can be rewritten.
    - A @ContextDataSet can be used for collecting context variables in a single class, eg. form parameters.(This is a base for form validation mechanism in furture)
    
    Remove
    - Depencies of Spring MVC is no longer necessary. Asta4dServlet can be used for handling http requests and StaticResourceHandler can be used for static resource files.



## LICENSE

Apache License, Version 2.0

    Copyright 2012 astamuse company,Ltd.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.