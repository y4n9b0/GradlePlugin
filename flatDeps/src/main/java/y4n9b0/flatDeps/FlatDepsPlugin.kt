package y4n9b0.flatDeps

import org.gradle.api.Plugin
import org.gradle.api.Project

class FlatDepsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        println("\n====> hello!!")
    }

    companion object {}
}