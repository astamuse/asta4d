set topfolder=%~dp0
set MAVEN_OPTS=-javaagent:"%topfolder%\asta-career\libs\spring-instrument-3.1.2.RELEASE.jar" %MAVEN_OPTS%
mvn %1 test -DskipTests=true -Deureika.debug=true -Pasta-career-jetty-run -D jetty.port=8090 -Deureika.home="%topfolder%\asta-career\workhome"