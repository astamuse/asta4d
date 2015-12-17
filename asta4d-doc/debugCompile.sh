#!/bin/bash
touch src/docbkx/index.xml
mvn -DprjDocRoot=. docbkx:generate-html@html-singlepage