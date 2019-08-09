# Steps to update the Konversation artifacts
At first make sure that every thing still works, by running all the tests.
## Java Artifact
### Bintray
1. Make sure that in your global gradle configuration (located in `~/.gradle/gradle.properties`) are the keys `bintray.user` and `bintray.apikey`. The username is that one you use to login onto bintray.com, the *API Key* is located in [your profile](https://bintray.com/profile/edit) in the *API Key* section.
2. Update the version numbers of the artifacts in `/build.gradle` and update if required the dependency in the example project.
3. Execute `./gradlew runtime-jvm:bintrayUpload`
4. Verify that you see the artifact on [jcenter](https://jcenter.bintray.com/org/rewedigital/voice/konversation/).
### Maven Central
1. Execute the steps for Bintray, then open the [Artifact on Bintray](https://bintray.com/rewe-digital/Konversation/Konversation) and click on the `Maven Central` tab. Then click on the green button "sync". After a view minutes there should be the output "Successfully synced and closed repo."
2. After a view hours you should be able to see the artifact on [Maven Central](https://search.maven.org/search?q=g:org.rewedigital.voice%20AND%20a:konversation).
## JavaScript Artifact
### NPM
1. Make sure that you are logged in the npm cli. 
2. Execute `./gradlew runtime-js:publishNpmPackage`.
3. Verify the results on [`npm`](https://www.npmjs.com/package/@rewe-digital/konversation).
## Gradle Plugin
1. Make sure that
2. Execute `./gradlew gradle-plugin:publishPlugins`.
3. Verify that the update is visible on the [Gradle](https://plugins.gradle.org/plugin/org.rewedigital.konversation).
## CLI
There are two ways. At first the manual way:
1. Create the fat jar by executing `./gradlew cli:fatJar`
2. Create a new release at GitHub.
3. Upload the created konversation.jar at the release page named as `konversation-cli.jar`.
### Homebrew
1. Check out the tap currently at https://github.com/rekire/homebrew-packages
2. Update the sha256 hash and the (download) url in `konversation/Formular/konversation.rb`
3. Commit the changes and tag it
### Chocolaty
1. Copy the konversation.jar into `cli-integrations/chocolaty/tools`
2. Update the sha256 hash and the download url in `cli-integrations/chocolaty/tools/legal/VERIFICATION.txt`
3. Run in cmd or powershell `cli-integrations/chocolaty`: `choco pack && choco push konversation.nupkg -s https://chocolatey.org/`

Or the more automated way:
1. Create a token in [GitHub](https://github.com/settings/tokens) with repo permission.
2. Open a shell e.g. bash
3. Execute `./upload-cli.sh <version of the release> <true if preview> <github token>`. This might take a while since it does the manual steps and creates 2 commits in two repositories and two tags.
4. Open cmd or powershell (on Windows)
5. Execute in `cli-integrations/chocolaty`: `choco pack && choco push konversation.nupkg -s https://chocolatey.org/`