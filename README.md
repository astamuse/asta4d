## News

- 2015-09-07, the 1.1-b2 with minor bug fixes is released.
- 2015-07-23, the 1.1-b1 with better Java 8 support is released.
- 2015-02-14, the first official release "1.0-Valentines" is released at the Valentine's day!

## Quick start

[documents](http://astamuse.github.io/asta4d/1.1-b2/)(being updated at irregular intervals)

[Online Sample](http://asta4dsample-xzer.rhcloud.com/)

There is a maven archetype for asta4d. If you want to start with the archetype, you have to [install Maven 3](http://maven.apache.org/download.cgi) at first. After installed Maven 3, create  the sample project by the following command:

```batch
mvn archetype:generate                       \
    -DarchetypeGroupId=com.astamuse          \
    -DarchetypeArtifactId=asta4d-archetype   \
    -DarchetypeVersion=1.1-b2        \
    -DgroupId=<your.groupid>                 \
    -DartifactId=<your-artifactId>
```

or simply follow the wizard by filtered list:

```batch
mvn archetype:generate -DarchetypeGroupId=com.astamuse -DarchetypeArtifactId=asta4d-archetype -DarchetypeVersion=1.1-b2
```

*The 1.1-x with better Java 8 support is recommended for new projects, if you cannot use Java 8, use 1.0-Valentines instead.*

After the archetype is created, enter the folder which has a "pom.xml" file, run the following command:

```batch
mvn jetty:run
```
 
Then you can access the sample project by http://localhost:8080, there are source samples shown, it is a good start from reading the samples.
After you confirm the sample project is OK, you can add your own url mapping rules to /src/main/java/.../.../UrlRules.java,
and also you can add your own html template files to /src/main/webapp.

Reading the [Best Practice](http://astamuse.github.io/asta4d/1.1-b2/userguide/index.html#chapter-best-practice) before writing your own code is recommended.

## What is Asta4D

Asta4D is a view first web application framework which is friendly to designers and flexible to developers. Asta4D affords high productivity than traditional MVC architecture by "View First" architecture. It also allows front-end engineers and back-end engineers work independently without interference by separating rendering logic from template files.

Asta4D is inspired by [lift](http://liftweb.net/)  which is a famous scala web application framework and it is developed by astamuse company Ltd. locating at Tokyo Japan. We are concentrating on global innovation support and developing Asta4D for our own services. Currently, Asta4D is driving our new service development.

## Why Asta4D
In the past decade, plenty of Java based web application frameworks are generated. Especially the MVC 
architecture and JSP tag libs (or other traditional template technologies) that has greatly released our 
productivity. But unfortunately, we are still suffering from the following situations:

1. The designers or front-end engineers are keeping complaining the mixed-in dynamic code, as they disturb their efforts of redesigning the page style or structure. And in the mean time, the back-end developers are also complaining that the front-end guys break the working page frequently,  because redesign or the new design is hard to merge due to the huge cost of source refactoring. 
1. The developers are complaining about the poor functionalities of template language which they are using and tired from the various magic skills for complex rendering logic.
1. The developers are discontented with the counterproductivity of MVC architecture and desire a more efficient approach.

Thus, we created Asta4D. Currently, Asta4D is driving our service site:[astamuse.com](http://astamuse.com)

## What does "Asta4D" mean

The name of Asta4D is from our company's name: astamuse. We explain the "4D" as following ways:

1. For designers
    
    Asta4D consider the design friendliness as the most important factor of itself. We hope web designers can fulfil their maximum potential of creativity without squandering their time on the back-end technologies which they could never be adept at.

1. For developers
    
    We hope Asta4D can help developers to achieve their work more easily. Developers would never be afflicted with complex rendering logic because they can use powerful Java language to do whatever they want since the rendering has been split from template files. View first also releases developers from the cumbersome MVC architecture, now they have more time to have a cup of coffee.

1. 4 dimension
    
    We believe that Asta4D can act as a wormhole that connects the front-end and the back-end. We can move quicker by Asta4D just like we are going through the 4 dimensional space.

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
            return Renderer.create("div", name);
        }
    
        public Renderer setProfile() {
            Renderer render = Renderer.create();
            render.add("p#name span", "asta4d");
            render.add("p#age span", 20);
            return render;
        }
    }
    ```

1. Testable Rendering logic

    All of the rendering logic of Asta4D is testable and you can simply test them by write simple junit cases, which can replace over than half of selenium tests.

    ```java
        // prepare test target
        Renderer render = Renderer.create();
        render.add("#someIdForInt", 12345);
    
        // perform test
        RendererTester tester = RendererTester.forRenderer(render);
        Assert.assertEquals(tester.get("#someIdForInt"), 12345);
    
    ```
    
    Rendering for list data can be performed as well
    
    ```java
        // prepare test target
        Renderer render = Renderer.create();
        render.add("#someIdForInt", Arrays.asList(123, 456, 789));
    
        // perform test
        RendererTester tester = RendererTester.forRenderer(render);
        Assert.assertEquals(tester.getAsList("#someIdForInt"), Arrays.asList(123, 456, 789));
    
    ```
    
    [Further samples for test](https://github.com/astamuse/asta4d/blob/develop/asta4d-core/src/test/java/com/astamuse/asta4d/test/unit/RenderTesterTest.java)

1. High security of being immune from cross-site(XSS/CSRF)
    
    Asta4D is, by nature, immune from cross-site(XSS/CSRF) problems. You do not need to take care of cross-site any more. All the rendered value would be escaped by default and your clients have no chance to put malicious contents to your server.

1. View first
    
    Asta4D also affords higher productivity than traditional MVC architecture by View First mechanism. And it is also easier to change than MVC architecture.

    A controller is not necessary for request dispatch. All the requests cound be dispatched by a sort of predefined URL matching rules and could be forwarded to template files directly, which is called as view first.
    
    ```java
    rules.add("/app/", "/templates/index.html");
    ```

1. Isolate side effect with request handler
    
    Asta4D imports the conception of "side-effect" from functional programming languages and separating the "side-effect" by request handlers, which afford more flexibility on page rendering because the view layer is side-effect free now. Therefore Asta4D allows parallel page rendering in multiple threads as a built-in feature.

	[See details about side effect](http://astamuse.github.io/asta4d/userguide/#chapter-side-effect)

1. Advanced MVC
	
	Asta4D also affords a evolved MVC architecture which is more clarified for the duty of each application layer than the traditional MVC architecture.

	By traditional MVC architecture, we often have to expand the transaction from the controller layer across to the view layer to combat the lazy load issue, which ugly structure is essentially caused by the tangled controller which holds various unrelated duties.
  
	It is also strange that we have to modify our controller's implementation at every time we change the page appearance at view layer. Such situation could not satisfy us since the layers are not uncoupled really.
  
	We would say that the traditional controller is indeed a tangled magic container for most logics, a controller will unfortunately be coupled to most layers in the system even our initial purpose of MVC is to uncouple our logics. By contrast, Asta4D allows developers to really uncouple all the tangled logics easily. Basically we could split the traditional controller's duty to following parts:
	
	-	request handler
      
		Which takes the responsibilities of all the operations with side-effect.

	-	result matching in url rule

		Which dispatches the request to different views according to the result from request handler

	-	snippet class

		Which has the responsibility to render data to the page and also holds the obligation of preparing all the necessary data for page rendering.

	By above architecture, we could perfectly uncouple our logics by clarifying the obligation of each layer.

1. Built-in form flow mechanism

    Asta4D treat all the form processes as flow and afford a well defined architecture for various form processes. Asta4D gives developers the possibility of concentrating on their real business logics rather than technic issues. Basically, developers only need to implement an init method and an update method for a form process.

    ```java
    public class SingleInputFormHandler extends OneStepFormHandler<PersonForm> {
    
        @Override
        protected PersonForm createInitForm() throws Exception {
            ...
        }
    
        @Override
        protected void updateForm(PersonForm form) {
            ...
        }
    
    }
    ```
## Roadmap

1.0.x (maintainance branch)

-	bug fix

1.1 (current developing branch)

plain to officially release before the end of 2015, snapshot will keep release(b1 has been released at 2015.7.23).

-	java 8 support(lambda, etc.)
-	minor funcationalities enhancement
-	user guide update

2.0

-	no plain yet



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