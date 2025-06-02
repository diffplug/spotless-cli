# Developer Notes for Releasing

## Creating a release

To create a new release, follow these steps (everything automated):

1. Navigate to https://github.com/diffplug/spotless-cli/actions/workflows/publish.yml
2. Click on "Run workflow", use main branch, by default don't provide a version number

## Re-creating a release

If for some reason, we need to re-create a release, follow these steps (manual labor involved):

### In spotless-cli repo:

https://github.com/diffplug/spotless-cli

1. Navigate to "Releases" (right column)
2. Make sure you are on the right release version (the one you need to recreate)
3. Click on the trashcan in the upper right corner of the release
4. Confirm
5. Switch to "Tags" in the tab-bar at the top
6. Click on the `...` at the very right of the version to recreate -> Delete tag
7. Confirm

### In homebrew-tap repo:

https://github.com/diffplug/homebrew-tap

1. Navigate to "Releases" (right column)
2. Switch to "Tags" in the tab-bar at the top
3. Click on the `...` at the very right of the version to recreate -> Delete tag
4. Confirm

### In chocolatey-bucket repo:

https://github.com/diffplug/chocolatey-bucket

1. Navigate to "Releases" (right column)
2. Switch to "Tags" in the tab-bar at the top
3. Click on the `...` at the very right of the version to recreate -> Delete tag
4. Confirm

### In local clone of spotless-cli repo:

1. Make sure you are on main branch, pull the HEAD
2. Open `CHANGELOG.md` and merge the version to recreate with the Unreleased version
   This typically means deleting the release header and empty lines, but could mean to cut&paste sections together
3. Run `gradlew changelogCheck -i` to make sure the changelog is valid
4. If you have the deleted tag also locally, delete it with `git tag -d <tagname>` e.g. `git tag -d v1.0.0`
5. commit the changes with `git commit -m "changelog: recreate v<your-version>"` e.g. `git commit -m "changelog: recreate v1.0.0"`
6. push the changes with `git push origin main`

## Manually publishing to chocolatey bucket

Open the "publish a release" action run in spotless-cli repo

https://github.com/diffplug/spotless-cli/actions

1. Scroll down to "Artifacts"
2. Download the `jreleaser-release-windows` artifact
3. Unzip the artifact
4. Copy everything from the `<zip-root>/package/spotless-cli` into the repo `chocolatey-bucket`.
5. Remove the `spotless-cli/spotless-cli-*.nupkg` file
6. Add everything with `git add .`
7. Commit everything with `git commit -m "spotless-cli v<your-version>"` e.g. `git commit -m "spotless-cli v0.1.0"`
8. Push everything with `git push origin main`
9. Create a tag with `git tag v<your-version>` e.g. `git tag v0.1.0`
10. Push the tag with `git push origin v<your-version>` e.g. `git push origin v0.1.0`
