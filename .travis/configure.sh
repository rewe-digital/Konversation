#!/bin/bash
TAG=`git describe --tags`
MODULE=`echo ${TAG}|cut -d- -f 1`
VERSION=`echo ${TAG}|cut -d- -f 2-`

echo "Configuring $MODULE for release $VERSION..."
mkdir -p publish
echo "bintray.user=$bintry_user">>~/.gradle/gradle.properties
echo "bintray.key=$bintry_key">>~/.gradle/gradle.properties
echo "gradle.publish.key=$gradle_key">>~/.gradle/gradle.properties
echo "gradle.publish.secret=$gradle_secret">>~/.gradle/gradle.properties
echo "//registry.npmjs.org/:_authToken=$npm_token">~/.npmrc