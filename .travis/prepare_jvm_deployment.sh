#!/bin/bash
VERSION=`git describe --tags|cut -d- -f 2-`

echo Updating version numbers...
sed -i -e "s/versions.runtime_jvm = '.*'/versions.runtime_jvm = '$VERSION'/g" build.gradle
git add build.gradle

sed -i '' publish/*