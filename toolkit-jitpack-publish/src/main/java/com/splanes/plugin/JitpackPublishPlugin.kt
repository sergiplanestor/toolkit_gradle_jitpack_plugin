package com.splanes.plugin

import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.creating
import org.gradle.kotlin.dsl.getValue

class JitpackPublishPlugin : org.gradle.api.Plugin<org.gradle.api.Project> {

    override fun apply(project: org.gradle.api.Project) {

        project.pluginManager.apply("maven-publish")

        val artifactConfig = project.extensions.create<JitpackPublishPluginExtension>(PLUGIN_EXTENSION)
        artifactConfig.apply {
            artifactGroup.convention(ARTIFACT_GROUP)
            artifactId.convention(project.name)
            artifactVersion.convention(ARTIFACT_VERSION)
        }

        val src by project.tasks.creating(org.gradle.api.tasks.bundling.Jar::class) {
            group = org.gradle.api.plugins.JavaBasePlugin.DOCUMENTATION_GROUP
            description = JAR_DESCRIPTION
            archiveClassifier.set(JAR_ARCHIVE_CLASSIFIER)
            from(project.sources())
        }

        project.afterEvaluate {
            publishing {
                publications {
                    listOf(RELEASE, DEBUG).forEach { flavor ->
                        create<org.gradle.api.publish.maven.MavenPublication>(flavor) {
                            config(project.components, src, artifactConfig, isDebug = flavor == DEBUG)
                        }
                    }
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> org.gradle.api.Project.extension(name: String): T =
        extensions.getByName(name) as T

    private fun org.gradle.api.Project.sources(): Set<java.io.File> =
        extension<com.android.build.gradle.BaseExtension>(ANDROID)
            .sourceSets
            .getByName(MAIN)
            .java
            .srcDirs

    private fun org.gradle.api.Project.publishing(configure: org.gradle.api.Action<org.gradle.api.publish.PublishingExtension>): Unit =
        extensions.configure(PUBLISHING, configure)

    private fun org.gradle.api.publish.maven.MavenPublication.config(
        components: org.gradle.api.component.SoftwareComponentContainer,
        source: Any,
        artifactConfig: JitpackPublishPluginExtension,
        isDebug: Boolean = false
    ) {
        from(components.getByName(if (isDebug) DEBUG else RELEASE))
        artifact(source)
        groupId = artifactConfig.artifactGroup.get()
        artifactId = artifactConfig.artifactId.get() + if (isDebug) "-$DEBUG" else ""
        version = artifactConfig.artifactVersion.get()
    }

    companion object {
        private const val ARTIFACT_GROUP = "release"
        private const val ARTIFACT_VERSION = "release"
        private const val RELEASE = "release"
        private const val DEBUG = "debug"
        private const val ANDROID = "android"
        private const val MAIN = "main"
        private const val PUBLISHING = "publishing"
        private const val PLUGIN_EXTENSION = "JitpackPublishPluginExtension"
        private const val JAR_DESCRIPTION = "Assembles sources JAR"
        private const val JAR_ARCHIVE_CLASSIFIER = "sources"
    }
}