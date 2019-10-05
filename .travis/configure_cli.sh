#!/bin/bash
TAG=`git describe --tags`
MODULE=`echo ${TAG}|cut -d- -f 1`
VERSION=`echo ${TAG}|cut -d- -f 2-`

echo Updating version numbers...
sed -i -E "s/^version '[^']+'$/version '$VERSION'/g" cli/build.gradle
git add cli/build.gradle

sed -i -E "s/version is [0-9][^]\"]+/version is $VERSION/g" cli/readme.md
sed -i -E "s/cli-[0-9][^ ]+-blue/`echo ${TAG}|sed -e "s/-/--/g"`-blue/g" cli/readme.md
git add cli/readme.md

sed -i -e "s/const val version = \".*\"/const val version = \"$VERSION\"/g" cli/src/main/kotlin/org/rewedigital/konversation/Cli.kt
git add cli/src/main/kotlin/org/rewedigital/konversation/Cli.kt

sed -i -e "s/versions.cli = '.*'/versions.cli = '$VERSION'/g" build.gradle
git add build.gradle

echo Prepairing artifacts...
# Add link to the not yet created artifact
ln -s ../cli/build/libs/konversation.jar publish/konversation-cli.jar