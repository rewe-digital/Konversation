#!/bin/bash
TAG=`git describe --tags`
MODULE=`echo ${TAG}|cut -d- -f 1`
VERSION=`echo ${TAG}|cut -d- -f 2-`

echo "Patching Chocolatey..."
jarUrl="https://github.com/rewe-digital/Konversation/releases/download/$TAG/konversation-cli.jar"
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