#!/usr/bin/env bash

set -e

# Base directory for this entire project
BASEDIR=$(cd $(dirname $0) && pwd)
export MAVEN_OPTS=-javaagent:"$BASEDIR/eureika-web/libs/spring-instrument-3.1.2.RELEASE.jar" 

mvn3 test -DskipTests=true -Deureika.debug=true -Peureika-jetty-run -Deureika.home="$BASEDIR/eureika-web/workhome"

