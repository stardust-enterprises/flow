package enterprises.stardust.flow.models

import enterprises.stardust.flow.Model
import enterprises.stardust.flow.unset
import org.gradle.api.Project

data class ScmMetadata(
    var connection: String? = unset,
    var developerConnection: String? = unset,
    var url: String? = unset,
): Model<Project> {
    override fun consume0(target: Project) {
    }

    fun isValid() = this.connection != null && this.developerConnection != null && this.url != null
}