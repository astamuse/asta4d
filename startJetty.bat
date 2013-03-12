set topfolder=%~dp0
mvn %1 test -DskipTests=true -Dasta4d.sample.source_location=%topfolder%\asta4d-sample\src\main\java -Pjetty-run