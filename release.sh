#!/bin/bash
if [[ $# -eq 0 ]]; then
  echo "Expecting argument update or prepair"
  exit -1
fi

TAG=`git describe --tags`
MODULE=`echo $TAG|cut -d- -f 1`
VERSION=`echo $TAG|cut -d- -f 2-`

echo $1ing release $VERSION
if [[ $1 == "update" ]]; then
  mkdir -p publish
  ./release-$MODULE.sh update
fi

if [[ $1 == "prepair" ]]; then
  echo Setup deployment keys...
  # prepare the key
  chmod 600 travis_rsa
  eval `ssh-agent -s`
  ssh-add travis_rsa

  echo Committing changes...
  # Hide the mail address from spam bots
  git config --local user.email "`echo "Ym90QHJla2kucmU=" | base64 -d`"
  git config --local user.name "Travis CI"
  git remote set-url origin git@github.com:rewe-digital-incubator/Konversation.git
  git config remote.origin.fetch "+refs/heads/*:refs/remotes/origin/*"
  git fetch
  echo travis_rsa>>.gitignore
  echo publish>>.gitignore
  git checkout master
  ./release-$MODULE.sh publish
  git commit -m "[skip ci] Publish $MODULE version $VERSION"
  git push
  git tag -fa $TAG -m "Release $MODULE $VERSION"
  git push origin master --tags -f

  ls -l publish
  ls -l cli/build/libs
  sed -i '' publish/*
fi
