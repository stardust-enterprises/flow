@file:Suppress("PackageDirectoryMismatch")

package org.gradle.kotlin.dsl

import enterprises.stardust.flow.consume
import enterprises.stardust.flow.models.ProjectMetadata
import org.gradle.api.Project

internal const val FLOW_EXTENSION_NAME = "__flowMetadata"

fun Project.project(block: ProjectMetadata.() -> Unit): ProjectMetadata {
    val metadata = this.extensions.getByName<ProjectMetadata>(FLOW_EXTENSION_NAME)
    metadata.block()
    //TODO: figure out how to comsume on "buildscript" evaluation, and not when running in `subprojects` or `allprojects` scope
    // (not afterEvaluate becaues the Kotlin plugin cannot be applied with state `executed`)
    metadata.consume(this)
    return metadata
}