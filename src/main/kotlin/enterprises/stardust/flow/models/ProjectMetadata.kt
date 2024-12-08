package enterprises.stardust.flow.models

import enterprises.stardust.flow.*
import enterprises.stardust.flow.models.configure.GradlePluginConfiguration
import enterprises.stardust.flow.models.configure.JavaConfiguration
import enterprises.stardust.flow.models.configure.KotlinConfiguration
import enterprises.stardust.flow.models.dependency.DependencyScope
import enterprises.stardust.flow.models.dependency.RepositoryScope
import org.gradle.api.Project
import org.gradle.kotlin.dsl.descriptionFromReadme
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun <T : Activatable> configurator(stuff: T, also: (T) -> Unit = {}): ReadOnlyProperty<Model<*>, T> =
    object : ReadOnlyProperty<Model<*>, T> {
        override fun getValue(thisRef: Model<*>, property: KProperty<*>): T =
            stuff.also {
                it.activated = true
                also(it)
            }
    }

open class ProjectMetadata(
    var group: String? = inherit,
    var version: String? = inherit,
    var description: String? = unset,
    var vendor: String? = unset,
) : Model<Project> {
    internal lateinit var project: Project

    var dependencies = DependencyScope()
        private set
    var repositories = RepositoryScope()
        private set
    var scm = ScmMetadata()
    var licenses = LicensesMetadata()
    var developers = DevelopersMetadata()

    private val _java = JavaConfiguration()
    val java by configurator(_java)
    private val _kotlin = KotlinConfiguration()
    val kotlin by configurator(_kotlin) { java }
    internal val _gradle = GradlePluginConfiguration()
    val gradlePlugin by configurator(_gradle) { kotlin }

    private val components = listOf(
        _java,
        _kotlin,
        _gradle,
        repositories,
        dependencies,
    )

    override fun consume0(target: Project) {
        target.group = group.orInherit("group", target.parent?.let { "${it.group}.${it.name}" } ?: target.properties["group"])!!
        target.version = version.orInherit("version", target.parent?.version ?: target.properties["version"])!!
        target.description = description.orInherit("description", target.descriptionFromReadme)

        components.forEach { it.consume(target) }
    }
}
