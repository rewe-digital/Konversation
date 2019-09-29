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
# Hide the mail address from spam bots
git config --local user.email "`echo "Ym90QHJla2kucmU=" | base64 -d`"
git config --local user.name "Travis CI"
git remote set-url origin git@github.com:${TRAVIS_REPO_SLUG}.git
git config remote.origin.fetch "+refs/heads/*:refs/remotes/origin/*"
git fetch
echo travis_rsa>>.gitignore
echo publish>>.gitignore
git checkout master

if [[ -f "prepare_${MODULE}_deployment.sh" ]]; then
  ./prepare_${MODULE}_deployment.sh
fi

git commit -m "[skip ci] Publish $MODULE version $VERSION"
git push
git tag -fa ${TAG} -m "Release $MODULE $VERSION"
git push origin master --tags -f

export TRAVIS_COMMIT=`git rev-parse HEAD`

sed -i '' publish/*