package enterprises.stardust.flow.gradle

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.api.initialization.resolve.RepositoriesMode
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.maven
import org.gradle.toolchains.foojay.FoojayToolchainsConventionPlugin

open class FlowRootPlugin : Plugin<Settings> {
    @Suppress("UnstableApiUsage")
    override fun apply(target: Settings) = target.run {
        val exclusive = extra["flow.root.repositories.exclusive"]?.toString()?.toBoolean() == true
        val mavenLocal = extra["flow.root.repositories.mavenLocal"]?.toString()?.toBoolean() == true
        val jitpack = extra["flow.root.repositories.jitpack"]?.toString()?.toBoolean() == true
        dependencyResolutionManagement {
            if (exclusive) {
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
            pluginManager.apply(FlowEntrypointPlugin::class.java)
        }
    }
}