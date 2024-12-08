package enterprises.stardust.flow.models.dependency

import enterprises.stardust.flow.Model
import org.gradle.api.Project

private const val defaultConfiguration = "implementation"

open class DependencyScope : Model<Project> {
    private val dependencies = mutableListOf<Pair<String, String>>()

    operator fun invoke(block: DependencyScope.() -> Unit) = block()
    operator fun invoke(vararg deps: String) = deps.forEach { dependencies.add(defaultConfiguration to it) }

    fun gradle(id: String, version: String? = null): String =
        "$id:${id}.gradle.plugin${version?.let { ":$it" }}"

    fun kotlin(id: String): String = "org.jetbrains.kotlin:kotlin-$id"

    operator fun String.invoke(dep: String) {
        dependencies.add(this to dep)
    }

    operator fun String.unaryPlus() {
        dependencies.add(defaultConfiguration to this)
    }

    operator fun String.unaryMinus() {
        dependencies.add("compileOnly" to this)
    }

    operator fun String.not() {
        dependencies.add("testImplementation" to this)
    }

    override fun consume0(target: Project) {
        dependencies.forEach { (config, dep) -> target.dependencies.add(config, dep) }
    }
}