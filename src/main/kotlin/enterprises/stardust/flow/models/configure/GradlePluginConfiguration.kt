package enterprises.stardust.flow.models.configure

import enterprises.stardust.flow.Activatable
import enterprises.stardust.flow.Model
import enterprises.stardust.flow.consume
import enterprises.stardust.flow.models.ProjectMetadata
import enterprises.stardust.flow.unset
import org.gradle.api.HasImplicitReceiver
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.SupportsKotlinAssignmentOverloading
import org.gradle.kotlin.dsl.FLOW_EXTENSION_NAME
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.gradleKotlinDsl
import org.gradle.plugin.devel.GradlePluginDevelopmentExtension
import org.gradle.plugin.devel.PluginDeclaration
import org.gradle.plugin.devel.plugins.JavaGradlePluginPlugin
import org.jetbrains.kotlin.assignment.plugin.gradle.AssignmentExtension
import org.jetbrains.kotlin.assignment.plugin.gradle.AssignmentSubplugin
import org.jetbrains.kotlin.samWithReceiver.gradle.SamWithReceiverExtension
import org.jetbrains.kotlin.samWithReceiver.gradle.SamWithReceiverGradleSubplugin

class GradlePluginConfiguration(
    var plugins: MutableList<Pair<String, String>> = mutableListOf(),
    var displayName: String? = unset,
    var description: String? = unset,
    var website: String? = unset,
    var tags: MutableList<String> = mutableListOf(),
) : Model<Project>, Activatable {
    override var activated: Boolean = false

    @Suppress("UnstableApiUsage")
    private var pluginsBlock: NamedDomainObjectContainer<PluginDeclaration>.() -> Unit = {
        if (plugins.isEmpty()) {
            throw IllegalStateException("No plugins were configured")
        }

        if (plugins.size == 1) {
            create("default") {
                val (id, implementationClass) = plugins[0]
                this.id = id
                this.implementationClass = implementationClass
                displayName = this@GradlePluginConfiguration.displayName
                description = this@GradlePluginConfiguration.description
                this@GradlePluginConfiguration.tags.forEach { tags.add(it) }
            }
        } else {
            for ((id, implementationClass) in plugins) {
                create(id.replace(".", "_")) {
                    this.id = id
                    this.implementationClass = implementationClass
                    displayName = this@GradlePluginConfiguration.displayName
                    description = this@GradlePluginConfiguration.description
                    this@GradlePluginConfiguration.tags.forEach { tags.add(it) }
                }
            }
        }
    }

    fun plugins(block: NamedDomainObjectContainer<PluginDeclaration>.() -> Unit) {
        this.pluginsBlock = block
    }

    operator fun invoke(block: GradlePluginConfiguration.() -> Unit) {
        block()
    }

    override fun consume0(target: Project) = target.run {
        logger.lifecycle("<*> GradlePluginConfiguration.consume0")

        dependencies {
            "compileOnly"(gradleKotlinDsl())
            "compileOnly"(gradleApi())
        }

        val metadata = extensions.getByName<ProjectMetadata>(FLOW_EXTENSION_NAME)
        metadata.scm.consume(this)

        pluginManager.apply(JavaGradlePluginPlugin::class.java)
        extensions.configure(GradlePluginDevelopmentExtension::class.java) {
            vcsUrl.set(metadata.scm.url)
            this@GradlePluginConfiguration.website?.let { website.set(it) }
            plugins {
                pluginsBlock()
            }
        }

        // From the `kotlin-dsl` plugin
        plugins.apply(SamWithReceiverGradleSubplugin::class.java)
        extensions.configure(SamWithReceiverExtension::class.java) {
            annotation(HasImplicitReceiver::class.qualifiedName!!)
        }

        plugins.apply(AssignmentSubplugin::class.java)
        extensions.configure(AssignmentExtension::class.java) {
            annotation(SupportsKotlinAssignmentOverloading::class.qualifiedName!!)
        }
    }
}