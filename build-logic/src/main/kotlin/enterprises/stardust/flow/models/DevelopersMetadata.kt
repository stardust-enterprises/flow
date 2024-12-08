package enterprises.stardust.flow.models

import enterprises.stardust.flow.Model
import org.gradle.api.Project

data class DeveloperData(
    val id: String,
    val name: String? = null,
    val email: String? = null,
)

class DevelopersMetadata : Model<Project> {
    val developers = mutableListOf<DeveloperData>()

    operator fun invoke(vararg developers: String) = developers.forEach { it.unaryPlus() }
    operator fun invoke(block: DevelopersMetadata.() -> Unit) = block()

    fun String.unaryPlus() {
        developers.add(DeveloperData(this, this, null))
    }

    operator fun plusAssign(developer: String) = developer.unaryPlus()

    operator fun plusAssign(developer: DeveloperData): Unit {
        this.developers.add(developer)
    }

    override fun consume0(target: Project) {
    }
}