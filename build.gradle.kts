project {
    scm = githubScm("stardust-enterprises/flow")
    licenses += isc
    developers += xtrm

    dependencies {
        +gradle("org.gradle.toolchains.foojay-resolver-convention", "0.9.0")

        +gradle("org.gradlex.reproducible-builds", "1.0")
        +gradle("org.jetbrains.kotlin.jvm", "2.1.0")
        +kotlin("sam-with-receiver")
        +kotlin("assignment")
        +gradle("io.freefair.lombok", "8.11")
        +gradle("io.github.gradle-nexus.publish-plugin", "2.0.0")
        +gradle("com.gradle.plugin-publish", "1.3.0")
    }

    gradlePlugin {
        plugins += ("enterprises.stardust.flow" to "enterprises.stardust.flow.gradle.FlowPlugin")
        displayName = "Flow"
        description = "Boilerplate-be-gone, the Gradle plugin."
        tags += arrayOf("java", "scaffold", "boilerplate")
    }
}