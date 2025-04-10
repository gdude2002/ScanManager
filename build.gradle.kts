import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import java.util.Calendar

plugins {
	kotlin("jvm") version "2.1.10"
	kotlin("plugin.serialization") version "2.1.10"

	id("dev.yumi.gradle.licenser") version "2.1.+"
	id("org.jetbrains.kotlin.plugin.compose") version "2.1.10"
	id("org.jetbrains.compose") version "1.7.3"
}

val projectVersion: String by project

group = "me.gserv.archival"
version = projectVersion

repositories {
	mavenCentral()
	google()
}

dependencies {
	implementation(compose.desktop.currentOs)
	implementation(compose.foundation)
	implementation(compose.material3)
	implementation(compose.materialIconsExtended)

	implementation("org.slf4j", "slf4j-simple", "2.0.16")
	implementation("io.github.oshai", "kotlin-logging-jvm", "7.0.3")

	implementation("io.github.kdroidfilter:platformtools.darkmodedetector:0.2.7")
	implementation("io.github.vinceglb", "filekit-compose", "0.8.8")

	implementation("com.github.slugify", "slugify", "3.0.7") {
		capabilities {
			requireCapability("com.github.slugify:slugify-transliterator")
		}
	}

	implementation("org.openjfx", "javafx-base", "11.0.2", classifier = "win")
	implementation("org.openjfx", "javafx-graphics", "11.0.2", classifier = "win")

	implementation("com.github.skydoves:landscapist-animation:2.4.7")
	implementation("com.github.skydoves:landscapist-coil3:2.4.7")
	implementation("com.github.skydoves:landscapist-placeholder:2.4.7")

	implementation("com.twelvemonkeys.imageio", "imageio-jpeg", "3.12.0")
	implementation("com.twelvemonkeys.imageio", "imageio-psd", "3.12.0")
	implementation("dev.brachtendorf", "JImageHash", "1.0.0")
	implementation("com.github.romankh3", "image-comparison", "4.4.0")

	implementation("sh.calvin.reorderable:reorderable:2.4.3")

	implementation("com.h2database", "h2", "2.3.232")
	implementation("org.flywaydb", "flyway-core", "11.3.1")

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
	// Add a licence header rule, at least one must be present.
	rule(file("codeformat/HEADER"))
}

val year = Calendar.getInstance().get(Calendar.YEAR)

compose.desktop {
	application {
		mainClass = "me.gserv.archival.MainKt"
		jvmArgs.add("--enable-native-access=ALL-UNNAMED")

		nativeDistributions {
			val buildType = System.getProperties().getOrDefault("buildType", null)?.toString()

			val formats = when (buildType) {
				"exe" -> arrayOf(TargetFormat.Exe)
				"msi" -> arrayOf(TargetFormat.Msi)
				"dmg" -> arrayOf(TargetFormat.Dmg)
				"pkg" -> arrayOf(TargetFormat.Pkg)
				"deb" -> arrayOf(TargetFormat.Deb)
				"rpm" -> arrayOf(TargetFormat.Rpm)

				else -> arrayOf(
					TargetFormat.Exe, TargetFormat.Msi,
					TargetFormat.Dmg, TargetFormat.Pkg,
					TargetFormat.Deb, TargetFormat.Rpm
				)
			}

			targetFormats(*formats)

			linux {
				appCategory = "Productivity"
				debMaintainer = "gareth@gserv.me"
				menuGroup = "gserv.me"
				rpmLicenseType = "EUPL-1.2"
			}

			macOS {
				appCategory = "public.app-category.productivity"
				appStore = false
				bundleID = project.group.toString()
			}

			windows {
				console = false
				dirChooser = true
				perUserInstall = true

				menuGroup = "gserv.me"
				upgradeUuid = "c82597f2-47a3-415c-92a0-5dc3e12d75b1"
			}

			packageName = "ScanManager"
			packageVersion = project.version.toString().split("-").first()

			copyright = "© $year Gareth Coles, EUPL v1.2."
			description = "Simple photo scan organiser and toolkit."
			licenseFile = rootProject.file("LICENSE")
		}
	}
}
