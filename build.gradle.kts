import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm") version "2.1.10"

    id("dev.yumi.gradle.licenser") version "2.1.+"
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.10"
    id("org.jetbrains.compose") version "1.7.3"
}

group = "me.gserv.archival"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.foundation)
    implementation(compose.materialIconsExtended)

    implementation("org.slf4j", "slf4j-simple", "2.0.16")
    implementation("io.github.oshai", "kotlin-logging-jvm", "7.0.3")

    implementation("io.github.vinceglb", "filekit-compose", "0.8.8")

    implementation("com.github.slugify", "slugify", "3.0.7") {
        capabilities {
            requireCapability("com.github.slugify:slugify-transliterator")
        }
    }

    implementation("org.openjfx", "javafx-base", "11.0.2", classifier = "win")
    implementation("org.openjfx", "javafx-graphics", "11.0.2", classifier = "win")

    implementation("com.twelvemonkeys.imageio", "imageio-jpeg", "3.12.0")
    implementation("com.twelvemonkeys.imageio", "imageio-psd", "3.12.0")
    implementation("dev.brachtendorf", "JImageHash", "1.0.0")
    implementation("com.github.romankh3", "image-comparison", "4.4.0")

    implementation("com.h2database", "h2", "2.3.232")
    implementation("org.flywaydb", "flyway-core", "11.3.1")

    implementation("com.seanproctor", "data-table-material3", "0.11.2")

    implementation("org.jetbrains.exposed", "exposed-core", "0.59.0")
    implementation("org.jetbrains.exposed", "exposed-dao", "0.59.0")
    implementation("org.jetbrains.exposed", "exposed-jdbc", "0.59.0")
    implementation("org.jetbrains.exposed", "exposed-kotlin-datetime", "0.59.0")
    implementation("org.jetbrains.kotlinx", "kotlinx-datetime", "0.6.1")
}

kotlin {
    jvmToolchain(21)
}

license {
    // Add a license header rule, at least one must be present.
    rule(file("codeformat/HEADER"))
}

compose.desktop {
    application {
        mainClass = "me.gserv.archival.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Exe)

            packageName = "ScanManager"
            packageVersion = "1.0.0"
        }
    }
}
