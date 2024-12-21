package enterprises.stardust.flow.gradle

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
import kotlin.reflect.KClass

/**
 * @author xtrm
 * @since 0.2.0
 */
abstract class DelegatingWrappedPlugin(
    val projectPlugin: KClass<*>? = null,
    val settingsPlugin: KClass<*>? = null,
    val gradlePlugin: KClass<*>? = null,
) : Plugin<Any> {
    override fun apply(target: Any) {
        val (pluginManager, handlerClass) = when (target) {
            is Gradle -> target.pluginManager to gradlePlugin
            is Project -> target.pluginManager to projectPlugin
            is Settings -> target.pluginManager to settingsPlugin
            else -> throw GradleException("Unsupported target type: ${target::class}")
        }
        pluginManager.apply(
            handlerClass?.java
                ?: throw IllegalArgumentException("No plugin target specified")
        )
    }
}