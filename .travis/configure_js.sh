#!/bin/bash
VERSION=`git describe --tags|cut -d- -f 2-`

echo Updating version numbers...
sed -i -e "s/\"version\": \".*\"/\"version\": \"$VERSION\"/g" runtime-js/src/main/javascript/package.json
git add runtime-js/src/main/javascript/package.json

sed -i -E "s/JS runtime version is [0-9][^]\"]+/JS runtime version is $VERSION/g" docs/index.md
git add docs/index.md

sed -i -e "s/versions.runtime_js = '.*'/versions.runtime_js = '$VERSION'/g" build.gradle
git add build.gradle