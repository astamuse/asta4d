#!/usr/bin/env bash

set -e

# Base directory for this entire project
BASEDIR=$(cd $(dirname $0) && pwd)

mvn3 test -DskipTests=true -Deureika.debug=true -Pjetty-run -Deureika.home="$BASEDIR/eureika-web/workhome"

