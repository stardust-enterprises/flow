plugins {
    id("enterprises.stardust.flow")
}

project {
    vendor = "Stardust Enterprises"

    scm = githubScm("stardust-enterprises/flow")
    licenses += isc
    developers += xtrm

    dependencies {
        +gradlePlugin("org.gradle.toolchains.foojay-resolver-convention", "0.9.0")

        +kotlin("sam-with-receiver")
        +kotlin("assignment")
        +gradlePlugin("io.freefair.lombok", "8.11")
        +gradlePlugin("org.gradlex.reproducible-builds", "1.0")
        +gradlePlugin("io.github.gradle-nexus.publish-plugin", "2.0.0")
        +gradlePlugin("org.jetbrains.kotlin.jvm", "2.1.0")
    }

    gradlePlugin {
        plugins += ("enterprises.stardust.flow" to "enterprises.stardust.flow.gradle.FlowEntrypointPlugin")
        plugins += ("enterprises.stardust.flow.root" to "enterprises.stardust.flow.gradle.FlowRootPlugin")
        displayName = "Flow"
        description = "Boilerplate-be-gone, the Gradle plugin."
        website = "https://github.com/stardust-enterprises/flow"
        tags += arrayOf("java", "scaffold", "boilerplate")
    }
}