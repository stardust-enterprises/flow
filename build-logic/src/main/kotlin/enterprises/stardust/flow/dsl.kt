@file:Suppress("PackageDirectoryMismatch")

package org.gradle.kotlin.dsl

import enterprises.stardust.flow.models.ProjectMetadata
import enterprises.stardust.flow.consume
import org.gradle.api.Project

internal const val FLOW_EXTENSION_NAME = "flowMetadata"

fun Project.project(block: ProjectMetadata.() -> Unit): ProjectMetadata {
    val metadata = this.extensions.getByName<ProjectMetadata>(FLOW_EXTENSION_NAME)
    metadata.block()
    metadata.consume(this)
    return metadata
}