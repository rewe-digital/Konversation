#!/bin/bash
set -e
repo="rewe-digital/Konversation"
artifact="konversation-cli.jar"
artifactMineType="application/jar"
source="cli/build/libs/konversation.jar"
version=$1
token=$3

if [[ $# -ne 3 ]]; then
  echo "Missing args!"
  echo "$0 <version> <boolean is prerelease> <github token>"
  exit
fi

if [[ ! -f ${source} ]]; then
  echo "Creating jar..."
  ./gradlew :cli:fJ
fi

echo "Create release $version..."

json=`curl -X POST "https://api.github.com/repos/$repo/releases" -H "Authorization: token $token" -H "Content-Type: application/json" \
           -d "{\"tag_name\": \"v$version\",\"target_commitish\": \"master\",\"name\": \"v$version\",\"body\": \"Release of $version\",\"draft\": false,\"prerelease\": $2}" -s`
url=`node -e "console.log($json.upload_url.replace('{?name,label}',''))"`

echo "Uploading assert to $url..."
response=`curl "$url?name=$artifact" -X "POST" -H "Authorization: token $token" -H "Content-Type: $artifactMineType" -d "@$source" -s`
jarUrl=`node -e "console.log($response.browser_download_url)"`
echo "Published at $jarUrl"

echo "Calculating hash..."
sha256=`sha256sum ${source} | cut -d " " -f 1`

echo "Clone homebrew tap..."
cd build
rm -rf "homebrew-packages" | true
git clone --depth=1 git@github.com:rekire/homebrew-packages.git
cd homebrew-packages/Formula


echo "Patching Formula..."
sed -e "s#url .*#url \"$jarUrl\"#g" -e "s/sha256 .*/sha256 \"$sha256\"/g" -i konversation.rb

echo "Committing changes..."
git add konversation.rb
git commit -m "Update homebrew to version $version"
git tag -a "v$version" -m "v$version"
git push
git push origin --tags

echo "Patching Chocolatey..."
cd ../../../cli-integrations/chocolatey
sed -e "s#jar: .*#jar: $jarUrl#g" -e "s/checksum: .*/checksum: $sha256/g" -i legal/VERIFICATION.txt
sed -e "s#<version>.*</version>#<version>$version</version>#g" -i konversation.nuspec
git add legal/VERIFICATION.txt konversation.nuspec
# update artifact
rm "tools/konversation.jar" | true
cp "../../$source" "tools/konversation.jar"
git commit -m "Update Chocolatey to version $version"
git tag -a "cli-$version" -m "cli-$version"
git push origin --tags

echo "Please also update chocolatey with:"
echo "cd cli-integrations/chocolatey"
echo "choco pack"
echo "choco push konversation.$version.nupkg -s https://push.chocolatey.org/"