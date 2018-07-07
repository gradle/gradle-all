import org.eclipse.jgit.api.CloneCommand

buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath("org.eclipse.jgit:org.eclipse.jgit:4.8.0.201706111038-r")
    }
}

data class Repo(val name: String, val url: String, val included: Boolean = true) {
}

val repos = listOf(
    Repo("gradle", "git@github.com:gradle/gradle.git"),
    // Not included: need to use develop branch
    Repo("kotlin-dsl", "git@github.com:gradle/kotlin-dsl.git", false),
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

repos.forEach { r ->
    val repoDir = file("repos/${r.name}")
    if (!repoDir.exists()) {
        println("Checking out ${r.url}")
        repoDir.mkdirs()
        val command = CloneCommand()
        command.setDirectory(repoDir)
        command.setURI(r.url)
        command.call()
    }
    if (r.included) {
        includeBuild(repoDir)
    }
}
