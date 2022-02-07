package com.splanes.plugin

import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.creating
import org.gradle.kotlin.dsl.getValue
import java.io.File

class JitpackPublishPlugin : org.gradle.api.Plugin<org.gradle.api.Project> {

    override fun apply(project: org.gradle.api.Project) {

        project.pluginManager.apply("maven-publish")

        val src by project.tasks.creating(org.gradle.api.tasks.bundling.Jar::class) {
            group = org.gradle.api.plugins.JavaBasePlugin.DOCUMENTATION_GROUP
            description = "Assembles sources JAR"
            archiveClassifier.set("sources")
            from(project.sources())
        }

        project.afterEvaluate {
            publishing {
                publications {
                    create<org.gradle.api.publish.maven.MavenPublication>("release") {
                        applyPublishConfig(project, src, isDebug = false)
                    }
                    create<org.gradle.api.publish.maven.MavenPublication>("debug") {
                        applyPublishConfig(project, src, isDebug = true)
                    }
                }
            }
        }
    }

    private val org.gradle.api.Project.ext: org.gradle.api.plugins.ExtensionContainer get() = (this as org.gradle.api.plugins.ExtensionAware).extensions

    private fun org.gradle.api.Project.sources(): Set<File> {
        val baseExtension = ext.getByName("android") as com.android.build.gradle.BaseExtension
        return baseExtension.sourceSets.getByName("main").java.srcDirs
    }

    private fun org.gradle.api.Project.publishing(configure: org.gradle.api.Action<org.gradle.api.publish.PublishingExtension>): Unit =
        ext.configure("publishing", configure)

    private fun org.gradle.api.publish.maven.MavenPublication.applyPublishConfig(
        project: org.gradle.api.Project,
        src: Any,
        isDebug: Boolean
    ) {
        from(project.components(isDebug))
        artifact(src)

        groupId = "com.github.sergiplanestor"
        artifactId = project.artifactId(isDebug)
        version = project.properties["${project.name}_version"]?.toString() ?: "1.0.0"
    }

    private fun org.gradle.api.Project.artifactId(isDebug: Boolean): String =
        project.name + if (isDebug) "-debug" else ""

    private fun org.gradle.api.Project.components(isDebug: Boolean): org.gradle.api.component.SoftwareComponent =
        components.getByName(if (isDebug) "debug" else "release")

}