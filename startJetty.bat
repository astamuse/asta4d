set topfolder=%~dp0
set MAVEN_OPTS=-javaagent:"%topfolder%\eureika-web\libs\spring-instrument-3.1.2.RELEASE.jar" %MAVEN_OPTS%
mvn %1 test -DskipTests=true -Deureika.debug=true -Pjetty-run 