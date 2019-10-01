#!/bin/bash
TAG=`git describe --tags`
MODULE=`echo ${TAG}|cut -d- -f 1`
VERSION=`echo ${TAG}|cut -d- -f 2-`

echo Setup deployment keys...
# prepare the key
chmod 600 travis_rsa
eval `ssh-agent -s`
ssh-add travis_rsa

echo Committing changes...
git fetch
git checkout master
git commit -m "[skip ci] Publish $MODULE version $VERSION"
git push
git tag -fa ${TAG} -m "Release $MODULE $VERSION"
git push origin master --tags -f

export TRAVIS_COMMIT=`git rev-parse HEAD`