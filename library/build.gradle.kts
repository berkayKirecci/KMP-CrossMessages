import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKmpLibrary)
    alias(libs.plugins.vanniktech.mavenPublish)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    android {
        namespace = "io.github.berkaykirecci.crossmessages"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        // Off by default here; without it Compose resources never reach the AAR.
        androidResources {
            enable = true
        }

        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    iosArm64()
    iosSimulatorArm64()
    jvm()

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs { browser() }
    js { browser() }

    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain.dependencies {
            // `api`: these types appear in the public API. Keep the `compose.*` accessors despite
            // the deprecation — plain coordinates drop the version and platform suffix from the POM.
            api(compose.runtime)
            api(compose.foundation)
            api(compose.material3)
            api(compose.ui)
            api(compose.components.resources)
            api(libs.kotlinx.coroutines.core)
            api(libs.kotlinx.collections.immutable)
        }

        val fallbackMain = create("fallbackMain") { dependsOn(commonMain.get()) }
        jvmMain { dependsOn(fallbackMain) }
        jsMain { dependsOn(fallbackMain) }
        wasmJsMain { dependsOn(fallbackMain) }
    }
}

compose.resources {
    packageOfResClass = "io.github.berkaykirecci.crossmessages.resources"
    publicResClass = false
}

// Stability reports: ./gradlew :library:assemble -Pcrossmessages.composeReports
composeCompiler {
    if (providers.gradleProperty("crossmessages.composeReports").isPresent) {
        reportsDestination = layout.buildDirectory.dir("compose-reports")
        metricsDestination = layout.buildDirectory.dir("compose-reports")
    }
}

group = "io.github.berkaykirecci"

// Bump before tagging a release; the publish workflow does not read the git tag.
version = "2.0.0"

mavenPublishing {
    publishToMavenCentral()

    // Signs only when CI supplies a key, so publishToMavenLocal works without a GPG keyring.
    if (providers.gradleProperty("signingInMemoryKey").isPresent) {
        signAllPublications()
    }

    coordinates(group.toString(), "crossmessages", version.toString())

    pom {
        name = "KMP-CrossMessages"
        description = "Snackbars, toasts and alert dialogs for Compose Multiplatform, " +
                "with native presentation on Android and iOS."
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
