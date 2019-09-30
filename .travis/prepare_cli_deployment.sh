#!/bin/bash
TAG=`git describe --tags`
MODULE=`echo ${TAG}|cut -d- -f 1`
VERSION=`echo ${TAG}|cut -d- -f 2-`

echo Updating version numbers...
sed -i -E "s/^version '[^']+'$/version '$VERSION'/g" cli/build.gradle
git add build.gradle

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

echo "Patching Chocolatey..."
jarUrl="https://github.com/rewe-digital-incubator/Konversation/releases/download/$TAG/konversation-cli.jar"
sha256=`sha256sum publish/konversation-cli.jar | cut -d " " -f 1`
sed -e "s#jar: .*#jar: $jarUrl#g" -e "s/checksum: .*/checksum: $sha256/g" -i cli-integrations/chocolatey/legal/VERIFICATION.txt
sed -e "s#<version>.*</version>#<version>$VERSION</version>#g" -i cli-integrations/chocolatey/konversation.nuspec
git add cli-integrations/chocolatey/legal/VERIFICATION.txt cli-integrations/chocolatey/konversation.nuspec

# TODO checkout and patch brew repo.
# echo "Clone homebrew tap..."
# cd build
# rm -rf "homebrew-packages" | true
# git clone --depth=1 git@github.com:rekire/homebrew-packages.git
#
# echo "Patching Formula..."
# sed -e "s#url .*#url \"$jarUrl\"#g" -e "s/sha256 .*/sha256 \"$sha256\"/g" -i homebrew-packages/Formula/konversation.rb
#
# echo "Committing changes..."
# git add homebrew-packages/Formula/konversation.rb
# git commit -m "Update homebrew to version $VERSION"
# git tag -a "v$VERSION" -m "v$VERSION"
# git push
# git push origin --tags