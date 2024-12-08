package enterprises.stardust.flow.gradle

import enterprises.stardust.flow.models.ProjectMetadata
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradlex.reproduciblebuilds.ReproducibleBuildsPlugin

open class FlowEntrypointPlugin : Plugin<Project> {
    override fun apply(target: Project) = target.run {
        apply<ReproducibleBuildsPlugin>()

        val metadata = extensions.create("flowMetadata", ProjectMetadata::class.java)
        metadata.project = target
    }
}