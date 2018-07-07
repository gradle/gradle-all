import org.eclipse.jgit.api.CloneCommand
import org.eclipse.jgit.api.PullCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.MergeCommand
import org.eclipse.jgit.internal.storage.file.FileRepository

buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath("org.eclipse.jgit:org.eclipse.jgit:5.0.1.201806211838-r")
    }
}

data class Repo(val name: String, val url: String, val included: Boolean = true) {
}

val repos = listOf(
        Repo("gradle", "git@github.com:gradle/gradle.git"),
        // Not included: need to use develop branch
        Repo("kotlin-dsl", "git@github.com:gradle/kotlin-dsl.git", false),
        Repo("gradle-native", "git@github.com:gradle/gradle-native.git"),
        Repo("exemplar", "git@github.com:gradle/exemplar.git"),
        Repo("dependency-management-samples", "git@github.com:gradle/dependency-management-samples.git"),
        // Not included: needs fix for plugin resolution
        Repo("native-samples", "git@github.com:gradle/native-samples.git", false),

        // Guides

        Repo("migrating-build-logic-from-groovy-to-kotlin", "git@github.com:gradle-guides/migrating-build-logic-from-groovy-to-kotlin.git"),
        Repo("creating-build-scans", "git@github.com:gradle-guides/creating-build-scans.git"),
        Repo("building-java-web-applications", "git@github.com:gradle-guides/building-java-web-applications.git"),
        Repo("using-an-existing-gradle-build", "git@github.com:gradle-guides/using-an-existing-gradle-build.git"),
        Repo("creating-new-gradle-builds", "git@github.com:gradle-guides/creating-new-gradle-builds.git"),
        Repo("performance", "git@github.com:gradle-guides/performance.git"),
        Repo("using-build-cache", "git@github.com:gradle-guides/using-build-cache.git"),
        // Not included: wrong root project name
        Repo("creating-multi-project-builds", "git@github.com:gradle-guides/creating-multi-project-builds.git", false),
        Repo("building-java-libraries", "git@github.com:gradle-guides/building-java-libraries.git"),
        Repo("building-java-applications", "git@github.com:gradle-guides/building-java-applications.git"),
        Repo("consuming-jvm-libraries", "git@github.com:gradle-guides/consuming-jvm-libraries.git"),
        Repo("building-scala-libraries", "git@github.com:gradle-guides/building-scala-libraries.git"),
        Repo("building-kotlin-jvm-libraries", "git@github.com:gradle-guides/building-kotlin-jvm-libraries.git"),
        Repo("building-java-9-modules", "git@github.com:gradle-guides/building-java-9-modules.git"),
        Repo("building-groovy-libraries", "git@github.com:gradle-guides/building-groovy-libraries.git"),
        Repo("building-cpp-libraries", "git@github.com:gradle-guides/building-cpp-libraries.git"),
        Repo("building-cpp-executables", "git@github.com:gradle-guides/building-cpp-executables.git"),
        Repo("building-c-executables", "git@github.com:gradle-guides/building-c-executables.git"),
        Repo("building-android-apps", "git@github.com:gradle-guides/building-android-apps.git"),
        Repo("writing-gradle-plugins", "git@github.com:gradle-guides/writing-gradle-plugins.git"),
        Repo("writing-gradle-tasks", "git@github.com:gradle-guides/writing-gradle-tasks.git"),
        Repo("testing-gradle-plugins", "git@github.com:gradle-guides/testing-gradle-plugins.git"),
        Repo("publishing-plugins-to-gradle-plugin-portal", "git@github.com:gradle-guides/publishing-plugins-to-gradle-plugin-portal.git"),
        Repo("implementing-gradle-plugins", "git@github.com:gradle-guides/implementing-gradle-plugins.git"),
        Repo("designing-gradle-plugins", "git@github.com:gradle-guides/designing-gradle-plugins.git"),
        Repo("using-the-worker-api", "git@github.com:gradle-guides/using-the-worker-api.git"),
        Repo("gradle-site-plugin", "git@github.com:gradle-guides/gradle-site-plugin.git"),
        Repo("gradle-guides-plugin", "git@github.com:gradle-guides/gradle-guides-plugin.git"),
        Repo("guides-test-fixtures", "git@github.com:gradle-guides/guides-test-fixtures.git"),
        Repo("tutorial-template", "git@github.com:gradle-guides/tutorial-template.git"),
        Repo("topical-template", "git@github.com:gradle-guides/topical-template.git"),
        // Not included: mismatched build scan plugin version
        Repo("gs-template", "git@github.com:gradle-guides/gs-template.git", false),
        Repo("style-guide", "git@github.com:gradle-guides/style-guide.git"),
        Repo("greeting-plugin-example", "git@github.com:gradle-guides/greeting-plugin-example.git"),
        Repo("writing-getting-started-guides", "git@github.com:gradle-guides/writing-getting-started-guides.git"),
        Repo("running-webpack-with-gradle", "git@github.com:gradle-guides/running-webpack-with-gradle.git"),
        Repo("migrating-from-maven", "git@github.com:gradle-guides/migrating-from-maven.git"),
        Repo("executing-gradle-builds-on-teamcity", "git@github.com:gradle-guides/executing-gradle-builds-on-teamcity.git"),
        Repo("executing-gradle-builds-on-travisci", "git@github.com:gradle-guides/executing-gradle-builds-on-travisci.git"),
        Repo("executing-gradle-builds-on-jenkins", "git@github.com:gradle-guides/executing-gradle-builds-on-jenkins.git")
)

var checkOutTask: Task? = null
var updateTask: Task? = null

gradle.rootProject {
    checkOutTask = tasks.create("checkoutAll")
    updateTask = tasks.create("updateAll")
}

repos.forEach { r ->
    val repoDir = file("repos/${r.name}")
    if (!repoDir.exists()) {
        gradle.rootProject {
            val t = tasks.create("check-out-${r.name}") {
                doLast {
                    println("Checking out ${r.url}")
                    repoDir.mkdirs()
                    val cloneCommand = CloneCommand()
                    cloneCommand.setDirectory(repoDir)
                    cloneCommand.setURI(r.url)
                    cloneCommand.call()
                }
            }
            checkOutTask!!.dependsOn(t)
        }
    } else {
        gradle.rootProject {
            val t = tasks.create("update-${r.name}") {
                doLast {
                    println("Updating ${r.url}")
                    val git = Git.open(repoDir)
                    val pullCommand = git.pull()
                    pullCommand.setRebase(true)
                    pullCommand.setFastForward(MergeCommand.FastForwardMode.FF_ONLY)
                    pullCommand.call()
                }
            }
            updateTask!!.dependsOn(t)
        }
    }
    if (r.included && repoDir.exists()) {
        includeBuild(repoDir)
    }
}
