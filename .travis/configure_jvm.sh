#!/bin/bash
VERSION=`git describe --tags|cut -d- -f 2-`

echo Updating version numbers...
sed -i -E "s/JVM runtime version is [0-9][^]\"]+/JVM runtime version is $VERSION/g" docs/index.md
git add docs/index.md

sed -i -e "s/versions.runtime_jvm = '.*'/versions.runtime_jvm = '$VERSION'/g" build.gradle
git add build.gradle

sed -i '' publish/*