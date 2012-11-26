set topfolder=%~dp0
mvn %1 test -DskipTests=true -Pjetty-run