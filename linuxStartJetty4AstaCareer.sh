#!/usr/bin/env bash

set -e

# Base directory for this entire project
BASEDIR=$(cd $(dirname $0) && pwd)
export MAVEN_OPTS=-javaagent:"$BASEDIR/asta-career/libs/spring-instrument-3.1.2.RELEASE.jar" 

mvn3 test -DskipTests=true -Dastacareer.debug=true -Pasta-career-jetty-run -Djetty.port=8090 -Dastacareer.home="$BASEDIR/asta-career/workhome"

