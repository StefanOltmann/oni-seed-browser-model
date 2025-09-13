@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.vanniktech.mavenPublish)
    alias(libs.plugins.gitVersioning)
}

group = "de.stefan_oltmann"

gitVersioning.apply {

    rev {
        version = "\${commit.short}"
    }
}

kotlin {

    jvm()

    js {
        browser()
        nodejs()
    }

    wasmJs {
        browser()
        nodejs()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {

                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.serialization.protobuf)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}

val signingEnabled: Boolean = System.getenv("SIGNING_ENABLED")?.toBoolean() ?: false

mavenPublishing {

    publishToMavenCentral()

    if (signingEnabled)
        signAllPublications()

    coordinates(
        groupId = "de.stefan-oltmann",
        artifactId = "oni-seed-browser-model",
        version = "$version-SNAPSHOT"
    )

    pom {

        name = "ONI Seed Browser Model"

        description = "Object model for the ONI Seed Browser."

        url = "https://github.com/StefanOltmann/oni-seed-browser-model"

        licenses {
            license {
                name = "AGPL"
                url = "https://www.gnu.org/licenses/agpl-3.0.html"
            }
        }

        developers {
            developer {
                name = "Stefan Oltmann"
                url = "https://stefan-oltmann.de/"
                roles = listOf("maintainer", "developer")
                properties = mapOf("github" to "StefanOltmann")
            }
        }

        scm {
            url = "https://github.com/StefanOltmann/oni-seed-browser-model"
            connection = "scm:git:git://github.com/StefanOltmann/oni-seed-browser-model.git"
        }
    }
}
