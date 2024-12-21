package enterprises.stardust.flow.models.configure

import enterprises.stardust.flow.Activatable
import enterprises.stardust.flow.Model
import enterprises.stardust.flow.consume
import enterprises.stardust.flow.models.ProjectMetadata
import io.freefair.gradle.plugins.lombok.LombokPlugin
import io.github.gradlenexus.publishplugin.NexusPublishExtension
import io.github.gradlenexus.publishplugin.NexusPublishPlugin
import org.gradle.api.InvalidUserCodeException
import org.gradle.api.Project
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.plugins.JvmTestSuitePlugin
import org.gradle.api.plugins.jvm.JvmTestSuite
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.FLOW_EXTENSION_NAME
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.repositories
import org.gradle.plugins.signing.SigningExtension
import org.gradle.plugins.signing.SigningPlugin
import org.gradle.testing.base.TestingExtension

class JavaConfiguration(
    var languageVersion: JavaLanguageVersion = JavaLanguageVersion.of(8),
    var sourcesJar: Boolean = true,
    var javadocJar: Boolean = true,
    var lombok: Boolean = false,
    internal var nexusPublishing: Boolean = false,
    internal var testFramework: TestFramework? = null
) : Model<Project>, Activatable {
    override var activated: Boolean = false

    fun withNexusPublishing(value: Boolean = true) {
        nexusPublishing = value
    }

    fun withLombok(value: Boolean = true) {
        lombok = value
    }

    fun withJUnit() {
        testFramework = TestFramework.JUNIT
    }

    fun withKotlinTest() {
        testFramework = TestFramework.KTEST
    }

    operator fun invoke(version: Int, block: JavaConfiguration.() -> Unit = {}) {
        languageVersion = JavaLanguageVersion.of(version)
        block()
    }

    operator fun invoke(block: JavaConfiguration.() -> Unit) {
        block()
    }

    @Suppress("UnstableApiUsage")
    override fun consume0(target: Project) = target.run {
        target.logger.lifecycle("<*> JavaConfiguration.consume0")

        val metadata = extensions.getByName<ProjectMetadata>(FLOW_EXTENSION_NAME)

        pluginManager.apply(JavaLibraryPlugin::class.java)
        extensions.configure(JavaPluginExtension::class.java) {
            if (sourcesJar) withSourcesJar()
            if (javadocJar) withJavadocJar()

            toolchain.languageVersion.set(languageVersion)
        }

        try {
            target.repositories {
                mavenCentral()
            }
            metadata.repositories.defaults()
        } catch (e: InvalidUserCodeException) {
            if (e.message?.contains("prefer settings repositories") == false) {
                e.printStackTrace()
            }
        }

        if (lombok) {
            pluginManager.apply(LombokPlugin::class.java)
        }

        if (testFramework != null) {
            pluginManager.apply(JvmTestSuitePlugin::class.java)
            extensions.configure(TestingExtension::class.java) {
                suites.apply {
                    getByName<JvmTestSuite>("test") {
                        if (testFramework == TestFramework.JUNIT) {
                            useJUnitJupiter()
                        } else {
                            pluginManager.apply("org.jetbrains.kotlin.jvm")
                            useKotlinTest()
                        }
                    }
                }
            }
        }

//        pluginManager.apply(GitPlugin::class.java)
//        tasks.named("jar", Jar::class.java) {
//            println(extensions.findByName("indraGit"))
//        }

        pluginManager.apply(MavenPublishPlugin::class.java)
        pluginManager.apply(SigningPlugin::class.java)

        if (nexusPublishing) {
            if (metadata._gradle.activated) {
                throw IllegalStateException("Nexus publishing is not supported when using the Gradle plugin")
            }
            // Ensure we have proper metadata
            metadata.scm.consume(this)
            metadata.licenses.consume(this)
            metadata.developers.consume(this)

            pluginManager.apply(NexusPublishPlugin::class.java)

            tasks.apply {
                afterEvaluate {
                    val publishToSonatype = tasks.getByName("publishToSonatype")
                    val closeAndReleaseSonatypeStagingRepository =
                        tasks.getByName("closeAndReleaseSonatypeStagingRepository")

                    closeAndReleaseSonatypeStagingRepository
                        .mustRunAfter(publishToSonatype)

                    // Wrapper task since calling both one after the other in IntelliJ
                    // seems to cause some problems.
                    register("releaseToSonatype") {
                        group = "publishing"

                        dependsOn(
                            publishToSonatype,
                            closeAndReleaseSonatypeStagingRepository
                        )
                    }

                    // Prevent build failure after configuration cache miss
                    tasks.filter { it.group == "publishing" }
                        .filter { it.name.contains("Sonatype") }
                        .filter { it.notCompatibleWithConfigurationCache("Nexus plugin"); return@filter false }
                }
            }

            extensions.configure(NexusPublishExtension::class.java) {
                repositories.sonatype {
                    nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
                    snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))

                    // Skip this step if environment variables NEXUS_USERNAME or NEXUS_PASSWORD aren't set.
                    username.set(properties["NEXUS_USERNAME"] as? String ?: return@sonatype)
                    password.set(properties["NEXUS_PASSWORD"] as? String ?: return@sonatype)
                }
            }
        }

        extensions.configure(PublishingExtension::class.java) {
            publications {
                val isGradlePlugin = metadata._gradle.activated
                val block: MavenPublication.() -> Unit = {
                    val mavenPublication = this
                    if (!isGradlePlugin) {
                        from(components.getByName("java"))
                    }

                    pom {
                        name.set(project.name)
                        description.set(project.description)
                        metadata.scm.url?.let { url.set(it) }

                        licenses {
                            metadata.licenses.licenses.forEach { license ->
                                license {
                                    name.set(license.name)
                                    url.set(license.url)
                                    distribution.set(license.distribution)
                                }
                            }
                        }

                        developers {
                            metadata.developers.developers.forEach { developer ->
                                developer {
                                    id.set(developer.id)
                                    name.set(developer.name ?: developer.id)
                                    developer.email?.let { email.set(it) }
                                }
                            }
                        }

                        metadata.scm.takeIf { it.isValid() }?.let { scmData ->
                            scm {
                                connection.set(scmData.connection)
                                developerConnection.set(scmData.developerConnection)
                                url.set(scmData.url)
                            }
                        }
                    }

                    extensions.configure(SigningExtension::class.java) {
                        isRequired = properties["signing.keyId"] != null
                        sign(mavenPublication)
                    }
                }
                if (!isGradlePlugin) {
                    create("defaultMaven", MavenPublication::class.java) {
                        block(this)
                    }
                } else {
                    val gradlePluginMarkerPublications = metadata._gradle.plugins.map { it.first }
                        .map { it.replace(".", "_") }
                        .map { "${it}PluginMarkerMaven" }
                        .toTypedArray().takeIf { it.size > 1 } ?: arrayOf("defaultPluginMarkerMaven")

                    // FIXME: figure out how to configure a container object lazily
                    all {
                        if (name in arrayOf("pluginMaven", *gradlePluginMarkerPublications)) {
                            block(this as MavenPublication)
                        }
                    }
                }
            }
        }
    }
}

enum class TestFramework {
    JUNIT,
    KTEST,
}