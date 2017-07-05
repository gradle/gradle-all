A composite build that includes all the pieces of Gradle, including the Gradle guides, to allow development or exploration across all of these pieces.

Currently this build simply takes care of cloning the Gradle repository and a few of the Gradle guides repositories. The repositories are cloned into the `repos/` directory.

To use, just run `./gradlew` in the root directory or import into IDEA.

Currently there are no tasks to help with building or testing or to help with git operations across the pieces.

Note that it is currently not possible to run tests from IDEA, as both `./gradlew idea` and direct import are broken in various ways. 