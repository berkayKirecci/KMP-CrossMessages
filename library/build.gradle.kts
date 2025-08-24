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
    namespace = "com.berkaykirecci.snackbar"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

tasks.register("publishToGithub") {
    group = "publishing"
    description = "Publishes all publications to Github Packages repository."
    dependsOn("publishAllPublicationsToGithubPackagesRepository")
}

val userName = extra["GithubPackagesUsername"] as? String
val developerName = extra["GithubPackagesName"] as? String

publishing {
    repositories {
        maven {
            name = "GithubPackages"
            url = uri("https://maven.pkg.github.com/$userName/KMP-Snackbar")
            credentials(PasswordCredentials::class)
        }
    }
}

group = "com.berkaykirecci"
version = "0.0.63"

signing {
    //useGpgCmd()
    //sign(publishing.publications)
}

mavenPublishing {
    publishToMavenCentral()

    //signAllPublications()

    coordinates(group.toString(), "snackbar", version.toString())

    pom {
        name = "KMP-Snackbar"
        description = "Multiplatform Library"
        inceptionYear = "2025"
        url.set("https://github.com/$userName/KMP-Snackbar")
        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                distribution = "https://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }
        developers {
            developer {
                id = "$userName"
                name = "$developerName"
                url = "https://github.com/$userName/"
            }
        }
        scm {
            url = "https://github.com/$userName/KMP-Snackbar"
            connection = "scm:git:git://github.com/$userName/KMP-Snackbar.git"
            developerConnection = "scm:git:ssh://github.com/$userName/KMP-Snackbar.git"
        }
    }
}
