plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("com.android.tools.build:gradle:7.1.0")
    implementation(kotlin("gradle-plugin", version = "1.6.10"))
}