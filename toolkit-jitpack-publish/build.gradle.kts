plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    `maven-publish`
    id("com.gradle.plugin-publish") version "0.16.0"
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation("com.android.tools.build:gradle:7.1.0")
}

pluginBundle {
    website = "https://github.com/sergiplanestor/toolkit_gradle_jitpack_plugin"
    vcsUrl = "https://github.com/sergiplanestor/toolkit_gradle_jitpack_plugin.git"
    tags = listOf("testing", "integrationTesting", "compatibility")
}

gradlePlugin {
    val jitpackPublish by plugins.creating {
        id = "com.splanes.plugin.toolkit-jitpack-publish"
        group = "com.splanes"
        version = "1.0.0"
        displayName = "Custom plugin which publishes the module sources to jitpack"
        description = "Custom plugin which publishes the module sources to jitpack. More info: <a href=\"https://github.com/sergiplanestor/toolkit_gradle_jitpack_plugin\">Github</a>"
        implementationClass = "com.splanes.plugin.JitpackPublishPlugin"
    }
}