package enterprises.stardust.flow.models.dependency

import enterprises.stardust.flow.Model
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import java.net.URI

class RepositoryScope : Model<Project> {
    private val repositories = mutableListOf<RepositoryHandler.() -> Unit>()

    operator fun invoke(block: RepositoryScope.() -> Unit) = block()
    operator fun invoke(vararg repos: String) =
        repos.forEach { repositories.add { maven { url = URI.create(it) } } }

    operator fun String.unaryPlus() {
        repositories.add {
            val data: Pair<String?, String> = if (this@unaryPlus.contains('@')) {
                val data = this@unaryPlus.split('@')
                data[0] to data[1]
            } else {
                null to this@unaryPlus
            }
            maven {
                data.first?.let { name = it }
                url = URI.create(data.second)
            }
        }
    }

    fun defaults() {
        repositories.add { mavenCentral() }
        repositories.add { gradlePluginPortal() }
    }

    override fun consume0(target: Project) {
        repositories.forEach { repoBlock ->
            target.repositories.repoBlock()
        }
    }
}