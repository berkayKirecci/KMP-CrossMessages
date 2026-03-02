import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.vanniktech.mavenPublish)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    signing
}

kotlin {
    androidTarget {
        publishLibraryVariants("release")
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
        }
    }
}


dependencies {
    debugImplementation(compose.uiTooling)
}

android {
    namespace = "io.github.berkaykirecci.crossmessages"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

tasks.register("publishToMaven") {
    group = "publishing"
    description = "Publishes all publications to Maven repository."
    dependsOn("publishToMavenCentral")
}

group = "io.github.berkaykirecci"
version = "1.0.1"

signing {
    useGpgCmd()
    sign(publishing.publications)
}

publishing {
    repositories {
        maven {
            name = "GithubPackages"
            url = uri("https://maven.pkg.github.com/berkayKirecci/KMP-CrossMessages")
            credentials(PasswordCredentials::class)
        }
    }
}

mavenPublishing {
    publishToMavenCentral()

    signAllPublications()

    coordinates(group.toString(), "crossmessages", version.toString())

    pom {
        name = "KMP-CrossMessages"
        description = "Multiplatform Library"
        inceptionYear = "2025"
        url.set("https://github.com/berkayKirecci/KMP-CrossMessages")
        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                distribution = "https://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }
        developers {
            developer {
                id = "berkayKirecci"
                name = "Berkay Kireçci"
                url = "https://github.com/berkayKirecci/"
            }
        }
        scm {
            url = "https://github.com/berkayKirecci/KMP-CrossMessages"
            connection = "scm:git:git://github.com/berkayKirecci/KMP-CrossMessages.git"
            developerConnection = "scm:git:ssh://github.com/berkayKirecci/KMP-CrossMessages.git"
        }
    }
}
