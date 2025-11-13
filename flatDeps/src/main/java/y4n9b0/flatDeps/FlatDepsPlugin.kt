package y4n9b0.flatDeps

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.api.NamedDomainObjectSet
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.TaskProvider
import y4n9b0.flatDeps.AnsiColors.fg
import y4n9b0.flatDeps.AnsiColors.styles
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FlatDepsPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val allVariantTasks = mutableListOf<TaskProvider<*>>()

        // 1. 尝试 AGP 7+ 新 API
        val androidComponents = project.extensions.findByType(AndroidComponentsExtension::class.java)
        if (androidComponents != null) {
            androidComponents.onVariants { variant ->
                val variantName = variant.name.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase() else it.toString()
                }
                registerFlatDeps(project, variantName)?.let { allVariantTasks.add(it) }
            }
        } else {
            // 2. 否则回退到旧 API
            project.plugins.withId("com.android.application") {
                val application = project.extensions.findByType(AppExtension::class.java)!!
                application.applicationVariants.all { variant ->
                    val variantName = variant.name.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase() else it.toString()
                    }
                    registerFlatDeps(project, variantName)?.let { allVariantTasks.add(it) }
                }
            }

            project.plugins.withId("com.android.library") {
                val library = project.extensions.findByType(LibraryExtension::class.java)!!
                library.libraryVariants.all { variant ->
                    val variantName = variant.name.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase() else it.toString()
                    }
                    registerFlatDeps(project, variantName)?.let { allVariantTasks.add(it) }
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

    private fun registerFlatDeps(
        project: Project,
        variantName: String
    ): TaskProvider<*>? {
        val taskName = "flatDeps$variantName"
        val configurations = project.configurations.matching { config ->
            config.isCanBeResolved
                    && !config.isCanBeConsumed
                    // 只关心编译/运行时依赖
                    && (config.name.endsWith("${variantName}CompileClasspath", true) || config.name.endsWith("${variantName}RuntimeClasspath", true))
                    // 过滤掉测试、lint、apiElements 等
                    && !config.name.contains("Test", true)
                    && !config.name.contains("Lint", true)
                    && !config.name.contains("ApiElements", true)
                    && !config.name.contains("RuntimeElements", true)
                    && !config.name.contains("Metadata", true)
        }
        if (configurations.isEmpty()) {
            project.logger.error("${fg.red}No valid configuration found for ${styles.bold}:${project.name}:${variantName}${styles.reset}")
            return null
        }
        return project.tasks.register(taskName) { task ->
            task.group = "dependency"
            task.description = "Flat all dependencies for variant $variantName"
            task.doLast { writeDepsToFile(project, configurations, variantName, taskName) }
        }
    }

    private fun writeDepsToFile(
        project: Project,
        configurations: NamedDomainObjectSet<Configuration>,
        variantName: String,
        taskName: String,
    ) {
        val outDir = project.layout.buildDirectory.dir("outputs/dependencies").get().asFile
        if (!outDir.exists()) outDir.mkdirs()

        val outFile = File(outDir, "flatDeps$variantName.txt")
        if (outFile.exists()) outFile.delete()

        // outFile.appendText(buildHeader(project, variantName))
        configurations.flatMapTo(
            sortedSetOf(
                compareBy({ it.module.id.group }, { it.module.id.name }, { it.module.id.version })
            )
        ) { it.resolvedConfiguration.lenientConfiguration.allModuleDependencies }.forEach { dep ->
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
                                val prefix = if (index == soFiles.lastIndex) "└─" else "├─"
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

    private fun buildHeader(project: Project, variantName: String): String {
        val gitCommitId = fetchGitCommitId(project)
        val generatedTime = LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS")
        )
        return buildString {
            appendLine("Project       : ${project.rootProject.name}")
            appendLine("Module        : ${project.name}")
            appendLine("Variant       : $variantName")
            appendLine("Git Commit ID : ${gitCommitId ?: "N/A"}")
            appendLine("Generated at  : $generatedTime")
            appendLine()
        }
    }

    private fun fetchGitCommitId(project: Project): String? {
        return try {
            val gitDir = File(project.rootDir, ".git")
            if (gitDir.exists()) {
                // 获取 git commit id（如果有 .git 目录）
                val process = ProcessBuilder("git", "rev-parse", "--short", "HEAD")
                    .directory(project.rootDir)
                    .redirectErrorStream(true)
                    .start()
                process.inputStream.bufferedReader().readText().trim()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}