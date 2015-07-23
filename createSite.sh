#!/usr/bin/env bash

function cp_site(){
f=$1

echo preparing $f

cp -ar ./$f/target/site ./target/site/$f
}

copyTo=$1

if [ X"$copyTo" = "X" ];
then
  echo copy target is not specified.
  exit 1
fi

mvn clean site javadoc:aggregate-jar

if [ $? -ne 0 ];
then
    exit 1
fi

cp_site asta4d-core
cp_site asta4d-web
cp_site asta4d-spring
cp_site asta4d-sample
cp_site asta4d-doc
cp_site asta4d-archetype
cp_site asta4d-archetype-prototype

version=`cat currentVersion`

copyTo=$copyTo/$version

mkdir $copyTo

echo copying files to $copyTo

echo copying whole site

cp -ar ./target/site $copyTo

echo copying java doc

cp -ar ./target/apidocs $copyTo

echo copying user guide

cp -ar ./asta4d-doc/target/docbkx ./asta4d-doc/target/userguide

mv ./asta4d-doc/target/userguide/pdf/index.pdf ./asta4d-doc/target/userguide/pdf/userguide.$version.pdf

cp -ar ./asta4d-doc/target/userguide $copyTo

cp userguide_index.html $copyTo/userguide/index.html

sed -i "s/@version/$version/g" $copyTo/userguide/index.html

echo copying page_index

cp page_index.html $copyTo/index.html

sed -i "s/@version/$version/g" $copyTo/index.html