#!/usr/bin/env bash

version=1.1-b1

copyTo=../asta4d-github-page/1.1-b1

cp userguide_index.html $copyTo/userguide/index.html

cp page_index.html $copyTo/index.html

sed "s/@version/$version/g" $copyTo/userguide/index.html

sed "s/@version/$version/g" $copyTo/index.html