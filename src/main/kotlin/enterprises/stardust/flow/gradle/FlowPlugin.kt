@file:Suppress("UnstableApiUsage")

package enterprises.stardust.flow.gradle

import enterprises.stardust.flow.models.ProjectMetadata
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.api.initialization.resolve.RepositoriesMode
import org.gradle.api.plugins.ExtraPropertiesExtension
import org.gradle.kotlin.dsl.FLOW_EXTENSION_NAME
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.maven
import org.gradle.toolchains.foojay.FoojayToolchainsConventionPlugin
import org.gradlex.reproduciblebuilds.ReproducibleBuildsPlugin

fun ExtraPropertiesExtension.find(key: String): String? = if (has(key)) get(key).toString() else null

class FlowPlugin : DelegatingWrappedPlugin(
    projectPlugin = FlowProjectPlugin::class,
    settingsPlugin = FlowSettingsPlugin::class
) {
    open class FlowProjectPlugin : Plugin<Project> {
        override fun apply(target: Project) = target.run {
            apply<ReproducibleBuildsPlugin>()

            val metadata = extensions.create(FLOW_EXTENSION_NAME, ProjectMetadata::class.java)
            metadata.project = target
        }
    }

    open class FlowSettingsPlugin : Plugin<Settings> {
        override fun apply(target: Settings) = target.run {
            val noExclusive = extra.find("flow.root.repositories.exclusive")?.toBoolean() == false
            val mavenLocal = extra.find("flow.root.repositories.mavenLocal")?.toBoolean() == true
            val jitpack = extra.find("flow.root.repositories.jitpack")?.toBoolean() == true
            dependencyResolutionManagement {
                if (!noExclusive) {
                    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
                }
                repositories {
                    if (mavenLocal) {
                        mavenLocal()
                    }
                    mavenCentral()
                    gradlePluginPortal()
                    if (jitpack) {
                        maven("https://jitpack.io/")
                    }
                }
            }

            plugins.apply(FoojayToolchainsConventionPlugin::class.java)

            rootProject.name =
                extra["name"]?.toString() ?: "Missing project name"

            gradle.allprojects {
                pluginManager.apply(FlowProjectPlugin::class.java)
            }
        }
    }
}