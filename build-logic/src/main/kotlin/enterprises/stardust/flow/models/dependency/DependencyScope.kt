package enterprises.stardust.flow.models.dependency

import enterprises.stardust.flow.Model
import org.gradle.api.Project

private const val configurationName = "implementation"

open class DependencyScope : Model<Project> {
    private val dependencies = mutableListOf<String>()

    operator fun invoke(block: DependencyScope.() -> Unit) = block()
    operator fun invoke(vararg deps: String) = deps.forEach { dependencies.add(it) }

    fun gradlePlugin(id: String, version: String? = null): String =
        "$id:${id}.gradle.plugin${version?.let { ":$it" }}"

    fun kotlin(id: String): String = "org.jetbrains.kotlin:kotlin-$id"

    operator fun String.unaryPlus() {
        dependencies.add(this)
    }

    override fun consume0(target: Project) {
        dependencies.forEach { t -> target.dependencies.add(configurationName, t) }
    }
}