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

        // Guides

        Repo("migrating-build-logic-from-groovy-to-kotlin", "git@github.com:gradle-guides/migrating-build-logic-from-groovy-to-kotlin.git"),
        Repo("using-an-existing-gradle-build", "git@github.com:gradle-guides/using-an-existing-gradle-build.git"),
        Repo("creating-new-gradle-builds", "git@github.com:gradle-guides/creating-new-gradle-builds.git"),
        // Not included: wrong root project name
        Repo("creating-multi-project-builds", "git@github.com:gradle-guides/creating-multi-project-builds.git", false),
        Repo("building-java-libraries", "git@github.com:gradle-guides/building-java-libraries.git"),
        Repo("building-scala-libraries", "git@github.com:gradle-guides/building-scala-libraries.git"),
        Repo("writing-gradle-plugins", "git@github.com:gradle-guides/writing-gradle-plugins.git"),
        Repo("writing-gradle-tasks", "git@github.com:gradle-guides/writing-gradle-tasks.git"),
        Repo("publishing-plugins-to-gradle-plugin-portal", "git@github.com:gradle-guides/publishing-plugins-to-gradle-plugin-portal.git"),
        Repo("implementing-gradle-plugins", "git@github.com:gradle-guides/implementing-gradle-plugins.git"),
        Repo("designing-gradle-plugins", "git@github.com:gradle-guides/designing-gradle-plugins.git"),
        Repo("gradle-site-plugin", "git@github.com:gradle-guides/gradle-site-plugin.git"),
        Repo("gradle-guides-plugin", "git@github.com:gradle-guides/gradle-guides-plugin.git")
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
