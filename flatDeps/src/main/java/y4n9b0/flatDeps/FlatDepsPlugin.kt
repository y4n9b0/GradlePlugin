package y4n9b0.flatDeps

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.Variant
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import java.io.File

class FlatDepsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // 1. 检查 Android 模块
        val androidComponents = project.extensions.findByType(AndroidComponentsExtension::class.java)
            ?: return // 非 Android 模块直接跳过

        val allVariantTasks = mutableListOf<TaskProvider<*>>()

        // 2. 对每个变体注册 flatDeps 任务
        androidComponents.onVariants { variant ->
            registerFlatDepsForVariant(project, variant)?.let {
                allVariantTasks.add(it)
            }
        }

        // 3. 注册汇总任务，依赖所有变体的 flatDeps
        project.tasks.register("flatDeps") { task ->
            task.group = "dependency"
            task.description = "Flat all dependencies for all variants"
            allVariantTasks.forEach { t -> task.dependsOn(t) }
        }
    }

    private fun registerFlatDepsForVariant(project: Project, variant: Variant): TaskProvider<*>? {
        val variantName = variant.name.capitalize()
        val taskName = "flatDeps$variantName"

        val configurationName = "${variant.name}CompileClasspath"
        val configuration = project.configurations.findByName(configurationName)
            ?: project.configurations.findByName("${variant.name}Compile")
            ?: return null // 找不到配置则跳过

        return project.tasks.register(taskName) { task ->
            task.group = "dependency"
            task.description = "Flat all dependencies for variant $variantName"

            task.doLast {
                val logDir = project.layout.buildDirectory.dir("outputs/logs").get().asFile
                if (!logDir.exists()) logDir.mkdirs()

                val outFile = File(logDir, "$taskName.txt")
                if (outFile.exists()) outFile.delete()

                configuration.resolvedConfiguration.lenientConfiguration.allModuleDependencies
                    .sortedWith(compareBy({ it.module.id.group }, { it.module.id.name }, { it.module.id.version }))
                    .forEach { dep ->
                        val mid = dep.module.id
                        outFile.appendText("${mid.group}:${mid.name}:${mid.version}\n")
                    }
            }
        }
    }
}