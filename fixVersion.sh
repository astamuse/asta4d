#!/bin/bash

fixVer=$1

if [ "fixVer"X = ""X ]
then
  cmd=`BASENAME $0`
  echo $cmd \<release/dev\> \<new version\>
  exit 2
else
  BASEDIR=$(cd $(dirname $0) && pwd)
  cd $BASEDIR
  echo $fixVer > currentVersion
  mvn versions:set -DnewVersion=$fixVer
  cd asta4d-archetype-prototype
  mvn versions:set -DnewVersion=$fixVer
fi