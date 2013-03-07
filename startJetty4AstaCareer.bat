set topfolder=%~dp0
set MAVEN_OPTS=-javaagent:"%topfolder%\asta-career\libs\spring-instrument-3.1.2.RELEASE.jar" %MAVEN_OPTS%
mvn %1 test -DskipTests=true -Dastacareer.debug=true -Pasta-career-jetty-run -Djetty.port=8090 -Dastacareer.home="%topfolder%\asta-career\workhome"