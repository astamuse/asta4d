## What is Asta4D

It is a web application framework which is friendly to design and flexible to development.
It is inspired by lift which is a famous scala web application framework. Asta4D is therefore 
developed as an alternative of [lift](http://liftweb.net/) for Java. If you like scala, we strongly recommend lift 
for you and if you‘d like to enjoy the benefit of lift while still staying with Java, we believe 
our Asta4D is your best fit.


## Why Asta4D
In the past decade, plenty of Java based web application frameworks are generated. Especially the MVC 
architecture and JSP tag libs (or other traditional template technologies) that has greatly released our 
productivity. But unfortunately, we are still suffering from the following situations:

1. The designers or front-end engineers are keeping complaining the mixed-in dynamic code, as they disturb their efforts of redesigning the page style or structure. And in the mean time, the back-end developers are also complaining that the front-end guys break the working page frequently,  because redesign or the new design is hard to merge due to the huge cost of source refactoring. 
1. The developers are complaining about the poor functionalities of template language which they are using and tired from the various magic skills for complex rendering logic.
1. The developers are discontented with the counterproductivity of MVC architecture and desire a more efficient approach like traditional PHP/ASP development.

Thus, we created Asta4D.

## A taste of Asta4D
-   Separated template and rendering logic

    There is no dynamic code in template file. An Asta4D template file is always a pure HTML file which can be 
    easily maintained by front-end developers, it is very design friendly and we can reduce the workload for 
    source refactoring by over 90%.


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


    There is only some minimal mix-in extra declarations that tell template engine which Java class will rendering these 
    html contents, which Java class is usually called as a snippet class.

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
-   Testable Rendering logic

    Since all the rendering logics are being held by a Renderer instance which can be simply retrieved by invoking the target method of snippet class, unit test can be simply performed.

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

-   High security of being immune from cross-site(XSS/CSRF)
    
    Asta4D is, by nature, immune from cross-site problems. You do not need to take care of cross-site any more. All the rendered value would be escaped by default and your clients have no chance to put malicious contents to your server.
    
-   View first and URL matching

    There is no a controller which dispatches requests. All the requests will be dispatched by a sort of predefined URL matching rules and will be handled by request handlers. 

    ```java
    rules.add("/app/", "/templates/index.html");

    rules.add("/app/handler")
         .handler(LoginHandler.class)
         .handler(EchoHandler.class)
         .forward(LoginFailure.class, "/templates/error.html")
         .forward("/templates/success.html");
    ```

-   Request handlers for ajax and Restful request
    
    json request:

    ```java
    rules.add("/app/ajax/getUserList").handler(GetUserListHandler.class).json();
    ```

    ```java
    public class GetUserListHandler {

        @RequestHandler
        public List<String> queryUserList() {
            return Arrays.asList("otani", "ryu", "mizuhara");
        }
    }
    ```

    Restful request:

    ```java
    rules.add(PUT, "/app/ajax/addUser").handler(AddUserHandler.class).rest();
    ```

    ```java
    public class AddUserHandler {

        @RequestHandler
        public HeaderInfo doAdd(String newUserName) {
            // some logic that should add a new user by the given name
            // ...
            return new HeaderInfo(200);
        }
    ```

-   Isolate side effect with request handler and multi-threaded page rendering

    There are two types of action in a system, one is with “side effect”, another one is without “side effect”. “actions with side effect” are ones that will change the system status once they are performed. For instance, for the same URL, a login request (if succeeded) will cause a client’s privilege to be changed and the client could probably get a different page view from what the client get before login, because of which we say a login request is an action with side effect. Another obvious example is a database update operation. Once an update is committed, all the related clients will get a different output from the result before the update, which is also classified as “an action with side effect”. How about a query? We consider a query as an operation without side effect, it means that a client will always get the same result regardless of how many times the query is executed.

    We believe the actions with side effect should be managed seriously and we do that by putting all the actions with side effect to request handlers so that the view layer is purified and this makes the source more clear and maintainable. This is also means with Asta4D we can easily perform multi thread rendering on a single page because they are now all side-effect free. 

    parallel snippet rendering: All the snippet marked as afd:parallel” or “parallel” will be executed parallel.
    
    ```html
    <div afd:render="ParallelTest$TestRender:snippetInDiv" afd:parallel>
        <div id="test">xx</div>
    </div>

    <afd:snippet render="ParallelTest$TestRender:snippetReplaceDiv" parallel>
        <div id="test">xx</div>
    </afd:snippet>
    ```    

    parallel list rendering: A parallel data convertor can be used for parallel rendering a list.
    
    ```java
    Renderer renderer = Renderer.create("div#test", list, 
        new ParallelDataConvertor<String, String>() {
                @Override
                public String convert(String obj) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return obj + "-sleep";
                }
            });
    ```

## Quick start

[Online Sample](http://asta4d-sample.xzer.cloudbees.net/)

[JavaDoc](http://astamuse.github.io/asta4d/javadoc/)

There is a maven archetype for asta4d. If you want to start with the archetype, you have to [install Maven 3](http://maven.apache.org/download.cgi) at first. After installed Maven 3, create  the sample project by the following command:

    ```
    mvn archetype:generate                       \
        -DarchetypeGroupId=com.astamuse          \
        -DarchetypeArtifactId=asta4d-archetype   \
        -DarchetypeVersion=0.12.30               \
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

Additionally, there is a [Japanese document](http://astamuse.github.com/asta4d/userguide/index_jp.html) which includes more detailed user guide which is for our employees.

## Best practices

-   Use class name selectors instead of others in most cases

    Frontend designers would change html structures frequently, using class selector can avoid modifying the backend sources every time the html changed.

    We made a convention that all the classes with prefix "X-" are faked and used by backend logics to mark the anchor points of data.

-   Use request handler to normalize requests

    Some pages holds multiple url patterns, you can normalize all the patterns by a request handler. 

    We also use request hanlders to prepare the "target data" for the target page. A significant point is that preparing "target data" does not mean MVC architecture, we just query a simple entity or build a pojo to **represent the normalized condition** of the target page. 

## Todo list

Immediate tasks: 

-   cachable snippet

    (a rendered snippet result should can be cached)

-   Rendering helper for validation
    
    (not implementing validaiton which should use third-party implementations such as [JSR 303](http://beanvalidation.org/), just help rendering validation result easier)


## Questions and answers

-   **Q**: Who developed Asta4D and what its current status is?
    
    **A**: Asta4D is powered by [astamuse company Ltd.](http://www.astamuse.co.jp/) locating at Tokyo Japan. We are concentrating on global innovation support 
    and develop Asta4D for our own services. Currently, Asta4D is used by our new service development and is still in alpha release status.

-   **Q**: Why are there Spring dependencies in Asta4D?
    
    **A**: Our initial purpose is to drive up our new service development, so we have to consider a balance of progression and schedule 
    between Asta4D’s development and our service’s development. So we decided to start Asta4D’s work basing on Spring MVC 
    therefore Spring MVC can do the things that we have no time to do. Currently, we have removed dependencies from Spring, but since we
    started our work from Spring MVC and therefore Asta4D can work perfectly with Spring, so our sample project is still using Spring as a sample of
    integration of Spring bean management. If you don't need Spring, simply change the dependency of "asta4d-spring" to "asta4d-web".

    
## Release Notes
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