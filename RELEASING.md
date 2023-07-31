# Releasing

## Prepare

1. Create a new branch for the upcoming release
2. Update [plugin/build.gradle.kts](./plugin/build.gradle.kts) so that `project.version` is set to the new version.
3. Update [CHANGELOG.md](./CHANGELOG.md)
    1. Move all content under `## [Unreleased]` to a new section that follows this pattern: `## [VERSION] YYYY-MM-DD`
    2. If appropriate, add a high-level summary of changes at the beginning of the new section
4. Commit the changes, push them and open a PR targeting `main`

## Release

1. After peer-review, merge the release preparation PR
2. On your local machine, run `git switch main && git pull` to ensure you're on the `main` branch with the latest changes
3. Create a (lightweight) Git tag for the release and push it: (i.e. for version `3.0.0`: `git tag v3.0.0 && git push origin v3.0.0`)
4. A GitHub action takes care of the actual release process after the tag has been pushed. [You can follow the release process on GitHub](https://github.com/heroku/heroku-gradle/actions/workflows/release.yml).
5. Create a GitHub release from the tag created earlier. Use the markdown for the release from [CHANGELOG.md](./CHANGELOG.md) as the release description.
