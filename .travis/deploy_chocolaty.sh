#!/bin/bash
version=`git describe --tags|cut -d- -f 2-`

cp publish/konversation.jar tools/konversation.jar
cd cli-integrations/chocolatey
docker run --rm -v $PWD:$PWD -w $PWD linuturk/mono-choco pack
docker run --rm -v $PWD:$PWD -w $PWD linuturk/mono-choco push --key ${chocolatey_key} konversation.${version}.nupkg --source https://push.chocolatey.org/