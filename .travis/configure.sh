#!/bin/bash
TAG=`git describe --tags`
MODULE=`echo ${TAG}|cut -d- -f 1`
VERSION=`echo ${TAG}|cut -d- -f 2-`

mkdir -p publish
echo "bintray.user=$bintry_user">>~/.gradle/gradle.properties
echo "bintray.key=$bintry_key">>~/.gradle/gradle.properties
echo "gradle.publish.key=$gradle_key">>~/.gradle/gradle.properties
echo "gradle.publish.secret=$gradle_secret">>~/.gradle/gradle.properties
echo "//registry.npmjs.org/:_authToken=$npm_token">~/.npmrc

echo "Configuring $MODULE for release $VERSION..."
# Hide the mail address from spam bots
git config --local user.email "`echo "Ym90QHJla2kucmU=" | base64 -d`"
git config --local user.name "Travis CI"
git remote set-url origin git@github.com:${TRAVIS_REPO_SLUG}.git
git config remote.origin.fetch "+refs/heads/*:refs/remotes/origin/*"

if [[ -f ".travis/configure_${MODULE}.sh" ]]; then
  .travis/configure_${MODULE}.sh
fi