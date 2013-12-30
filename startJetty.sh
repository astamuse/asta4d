#!/usr/bin/env bash

set -e

# Base directory for this entire project
BASEDIR=$(cd $(dirname $0) && pwd)
mvn test -DskipTests=true -Dasta4d.sample.source_location=$BASEDIR/asta4d-sample/src/main/java -Pjetty-run

