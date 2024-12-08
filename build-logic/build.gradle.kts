plugins {
    `java-library`
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

fun gradlePlugin(id: String, version: String? = null): String =
    "$id:${id}.gradle.plugin${version?.let { ":$it" }}"

dependencies {
    implementation(gradleApi())
    implementation(gradleKotlinDsl())
    implementation(kotlin("gradle-plugin"))
    implementation(kotlin("gradle-plugin-api"))
    implementation(kotlin("sam-with-receiver"))
    implementation(kotlin("assignment"))
    implementation(gradlePlugin("io.freefair.lombok", "8.11"))
    implementation(gradlePlugin("org.gradlex.reproducible-builds", "1.0"))
    implementation(gradlePlugin("io.github.gradle-nexus.publish-plugin", "2.0.0"))
    implementation(gradlePlugin("org.jetbrains.kotlin.jvm", "2.1.0"))
    implementation(gradlePlugin("org.gradle.toolchains.foojay-resolver-convention", "0.9.0"))
//    implementation(gradlePlugin("net.kyori.indra.git", "3.1.3"))
}