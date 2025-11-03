package y4n9b0.flatDeps

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.api.LibraryVariant
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import y4n9b0.flatDeps.AnsiColors.fg
import y4n9b0.flatDeps.AnsiColors.styles
import java.io.File

class FlatDepsPlugin : Plugin<Project> {
    override fun apply(project: Project) {

        val allVariantTasks = mutableListOf<TaskProvider<*>>()

        // 1. 尝试 AGP 7+ 新 API
        val androidComponents = project.extensions.findByType(AndroidComponentsExtension::class.java)
        if (androidComponents != null) {
            androidComponents.onVariants { variant ->
                registerFlatDepsForNewApi(project, variant)?.let { allVariantTasks.add(it) }
            }
        } else {
            // 2. 否则回退到旧 API
            project.plugins.withId("com.android.application") {
                val application = project.extensions.findByType(AppExtension::class.java)!!
                application.applicationVariants.all { variant ->
                    registerFlatDepsForOldApi(project, variant)?.let { allVariantTasks.add(it) }
                }
            }

            project.plugins.withId("com.android.library") {
                val library = project.extensions.findByType(LibraryExtension::class.java)!!
                library.libraryVariants.all { variant ->
                    registerFlatDepsForOldApi(project, variant)?.let { allVariantTasks.add(it) }
                }
            }
        }

        // 3. 汇总任务
        project.tasks.register("flatDeps") { task ->
            task.group = "dependency"
            task.description = "Flat all dependencies for all variants"
            allVariantTasks.forEach { t -> task.dependsOn(t) }
        }
    }

    // AGP 7+ 新 API
    private fun registerFlatDepsForNewApi(
        project: Project,
        variant: com.android.build.api.variant.Variant
    ): TaskProvider<*>? {
        val variantName = variant.name.capitalize()
        val taskName = "flatDeps$variantName"

        val configuration = project.configurations.findByName("${variant.name}CompileClasspath")
            ?: project.configurations.findByName("${variant.name}Compile")
        if (configuration == null) {
            project.logger.error("${fg.red}No configuration found for ${styles.bold}:${project.name}:${variant.name}${styles.reset}")
            return null
        }

        return project.tasks.register(taskName) { task ->
            task.group = "dependency"
            task.description = "Flat all dependencies for variant $variantName"
            task.doLast { writeDepsToFile(project, configuration, taskName) }
        }
    }

    // AGP 4.x-6.x 旧 API
    private fun registerFlatDepsForOldApi(project: Project, variant: Any): TaskProvider<*>? {
        val variantName = when (variant) {
            is ApplicationVariant -> variant.name
            is LibraryVariant -> variant.name
            else -> return null
        }.capitalize()

        val taskName = "flatDeps$variantName"
        val configuration = project.configurations.findByName("${variantName.decapitalize()}CompileClasspath")
            ?: project.configurations.findByName("${variantName.decapitalize()}Compile")
        if (configuration == null) {
            project.logger.error("${fg.red}No configuration found for ${styles.bold}:${project.name}:${variant.name}${styles.reset}")
            return null
        }

        return project.tasks.register(taskName) { task ->
            task.group = "dependency"
            task.description = "Flat all dependencies for variant $variantName"
            task.doLast { writeDepsToFile(project, configuration, taskName) }
        }
    }

    private fun writeDepsToFile(
        project: Project,
        configuration: org.gradle.api.artifacts.Configuration,
        taskName: String
    ) {
        val logDir = project.layout.buildDirectory.dir("outputs/logs").get().asFile
        if (!logDir.exists()) logDir.mkdirs()

        val outFile = File(logDir, "$taskName.txt")
        if (outFile.exists()) outFile.delete()

        val deps = configuration.resolvedConfiguration.lenientConfiguration.allModuleDependencies
            .sortedWith(compareBy({ it.module.id.group }, { it.module.id.name }, { it.module.id.version }))

        deps.forEach { dep ->
            val moduleId = dep.module.id
            outFile.appendText("${moduleId.group}:${moduleId.name}:${moduleId.version}\n")

            // 遍历 artifacts 寻找 .so
            dep.moduleArtifacts.forEach { artifact ->
                val file = artifact.file
                if (file.extension in listOf("aar", "jar")) {
                    try {
                        java.util.zip.ZipFile(file).use { zip ->
                            val soFiles = zip.entries().asSequence()
                                .filter { /*it.name.startsWith("jni/") &&*/ it.name.endsWith(".so") }
                                .map { it.name/*.substringAfterLast("/")*/ }
                                .toSet()
                                .sorted()
                            soFiles.forEachIndexed { index, so ->
                                val prefix = when (index) {
                                    soFiles.lastIndex -> "└─"
                                    else -> "├─"
                                }
                                outFile.appendText("\t$prefix $so\n")
                            }
                        }
                    } catch (e: Exception) {
                        project.logger.warn("Failed to read artifact ${file.name}: ${e.message}")
                    }
                }
            }
        }
    }
}