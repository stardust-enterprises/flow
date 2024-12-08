@file:Suppress("PackageDirectoryMismatch")

package org.gradle.kotlin.dsl

import enterprises.stardust.flow.models.ScmMetadata
import org.gradle.api.Project

val Project.descriptionFromReadme: String?
    get() {
        val readme = file("README.md")
        if (!readme.exists()) {
            return parent?.descriptionFromReadme
        }
        return readme.readText().substringAfter("> ").substringBefore("\n")
    }

fun regularScmProvider(host: String, path: String): ScmMetadata {
    return ScmMetadata().apply {
        connection = "scm:git:git://$host/$path.git"
        developerConnection = "scm:git:ssh://$host/$path.git"
        url = "https://$host/$path"
    }
}

fun codebergScm(repo: String) = regularScmProvider("codeberg.org", repo)
fun githubScm(repo: String) = regularScmProvider("github.com", repo)
fun gitlabScm(repo: String) = regularScmProvider("gitlab.com", repo)