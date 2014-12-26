## Release Notes

-   1.0-b4 (2014.12.26)

    ENHANCE
    - shutdown all threads by Asta4D at servlet's destroying
    
-   1.0-b3 (2014.11.20)

    ADD
    - more debug friendly information for malformed html when rendering form
    - data value convertor for date/time string (joda-time as well)
    - form field renderer for html5 date/time input element
    - test methods for no operation on RenderTester
	
    FIX
    - bug in sample project
    - NullPointerException occurs when the default forward/redirect is missing
    - SiteCategoryAwaredTemplateResolver should cache found template file by different search categories
    - handle unknown http method better(rather than throw exception)
    
    ENHANCE
    - reduce memory usage of form flow trace storing mechanism
    - source refactoring to allow more flexible form process logic
    - allow pass logger to debug rendering
    - handle unknown http method better(rather than throw exception)

-   1.0-b2 (2014.10.29)

    ADD
    - site category support
	
    FIX
    - many many many fix...
    
    ENHANCE
    - refactoring of i18n mechanism to afford easier extendibility
    - form flow mechanism become stable    
    - well written sample for form flow
    - upgraded to the newest jsoup version 1.8.1

-   1.0-b1 (2014.10.06)

    ADD
    - form flow support
	
    FIX
    - many many many fix...

-   0.14.606

    ADD
    - support a callback rendering interface called Renderable
    - support static embeding which will embed the target file to the holding file when the holding file is initialized
    - allow customize json and rest result transformer
    
    FIX
    - template files are locked due to the input stream is not closed

-   0.14.4.30
    
    FIX
    - missing annotation convertor for web convenience annotations

-   0.14.4.28
    
    ADD
	- allow customize ResourceBundle loading and add encoding support for message file
	- more flexible usage of @ContextData
		- annotation conversion mechanism(name of @SessionData, @QueryParam can be speficied now)
		- The policy of how to handle type unmatch situation on context data conversion can be specified now: throw exception(default action), assign default value, record trace information in context.
		- customized element to array conversion can be supported in context data conversion
	- more flexible usage of @ContextDataSet
		- Allow search data by name in context at first for ContextDataSet annotated class data
		- allow singleton instance of ContextDataSet in single context life cycle
		- allow create ContextDataSet by specified factory class
	- afd:comment tag support
	- allow configuration initializer customizable
	- allow configure the parameter name of forwarded flash scope data on url
	- allow rendering Component directly by Render#add method


	FIX
    - potential concurrent hashmap access in ParallelRowConvertor
    - allow any asta4d's tag in head
    - make sample project runnable without spring
    - forwarded flash scope id on url should be encrypted to avoid guessing attack
    - timeout check is necessary even the target data map of flash data exists
    - make SpringWebPageView workable
	
	REMOVE
    - deprecated transform methods in ListConvertUtil
    - dependency from activemq (since we dont need it)
    - redundant source
    
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
