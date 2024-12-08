package enterprises.stardust.flow.models.configure

import enterprises.stardust.flow.Activatable
import enterprises.stardust.flow.Model
import enterprises.stardust.flow.models.ProjectMetadata
import org.gradle.api.Project
import org.gradle.kotlin.dsl.FLOW_EXTENSION_NAME
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.getByName
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper

class KotlinConfiguration(
    var compilerArgs: MutableList<String> = mutableListOf(),
    var languageVersion: KotlinVersion = KotlinVersion.KOTLIN_2_1,
    var optIns: MutableList<String> = mutableListOf(),
) : Model<Project>, Activatable {
    override var activated: Boolean = false

    fun compilerArgs(vararg compilerArgs: String) {
        this.compilerArgs.addAll(compilerArgs)
    }

    fun optIns(vararg optIns: String) {
        this.optIns.addAll(optIns)
    }

    override fun consume0(target: Project) {
        target.logger.lifecycle("<*> KotlinConfiguration.consume0")
        val metadata = target.extensions.getByName<ProjectMetadata>(FLOW_EXTENSION_NAME)

        target.run {
            apply<KotlinPluginWrapper>()
            extensions.configure(KotlinJvmProjectExtension::class.java) {
                jvmToolchain {
                    languageVersion.set(metadata.java.languageVersion)
                }
                compilerOptions {
                    languageVersion.set(this@KotlinConfiguration.languageVersion)
                    freeCompilerArgs.set(compilerArgs)
                    optIn.set(optIns)
                }
            }
        }
    }
}