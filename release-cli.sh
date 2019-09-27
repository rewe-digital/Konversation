#!/bin/bash
if [[ $# -eq 0 ]]; then
  echo "Expecting argument update or publish"
  exit -1
fi

TAG=`git describe --tags`
MODULE=`echo $TAG|cut -d- -f 1`
VERSION=`echo $TAG|cut -d- -f 2-`

if [[ $1 == "update" ]]; then
  echo Updating version numbers...
  sed -i -E "s/^version '[^']+'$/version '$VERSION'/g" cli/build.gradle
  git add build.gradle

  sed -i -E "s/version is [0-9][^]\"]+/version is $VERSION/g" readme.md
  sed -i -E "s/cli-[0-9][^ ]+-blue/`echo $TAG|sed -e "s/-/--/g"`-blue/g" readme.md
  git add readme.md

  echo Prepairing artifacts...
  # Add link to the not yet created artifact
  ln -s ../cli/build/libs/test-$VERSION.jar publish/konversation-cli.jar
fi

if [[ $1 == "publish" ]]; then
  echo "Patching Chocolatey..."
  jarUrl="https://github.com/rewe-digital-incubator/Konversation/releases/download/$TAG/konversation-cli.jar"
  sha256=`sha256sum publish/konversation-cli.jar | cut -d " " -f 1`
  cd cli-integrations/chocolatey
  sed -e "s#jar: .*#jar: $jarUrl#g" -e "s/checksum: .*/checksum: $sha256/g" -i legal/VERIFICATION.txt
  sed -e "s#<version>.*</version>#<version>$VERSION</version>#g" -i konversation.nuspec
  git add legal/VERIFICATION.txt konversation.nuspec
  # update artifact
  # rm "tools/konversation.jar" | true
  # cp "../publish/konversation-cli.jar" "tools/konversation.jar"
  # git commit -m "Update Chocolatey to version $VERSION"
  # git tag -a "$TAG" -m "$TAG"
  # git push origin --tags

  echo "Please also update chocolatey on a Windows maschine with:"
  echo "git clone git@github.com:rewe-digital-incubator/Konversation.git"
  echo "cd cli-integrations/chocolatey"
  echo "wget $jarUrl -O tools/konversation.jar"
  echo "choco pack"
  echo "choco push konversation.$VERSION.nupkg -s https://push.chocolatey.org/"
  cd ../..
fi