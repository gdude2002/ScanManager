import org.gradle.declarative.dsl.schema.FqName.Empty.packageName
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import java.lang.System.console
import java.util.Calendar
import kotlin.text.Typography.copyright

plugins {
	kotlin("jvm") version "2.1.10"
	kotlin("plugin.serialization") version "2.1.10"

	id("dev.yumi.gradle.licenser") version "2.1.+"
	id("org.jetbrains.kotlin.plugin.compose") version "2.1.10"
	id("org.jetbrains.compose") version "1.7.3"

	id("org.openjfx.javafxplugin") version "0.1.0"
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

	implementation("org.slf4j", "slf4j-simple", "2.0.17")
	implementation("org.slf4j", "jul-to-slf4j", "2.0.17")

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

	implementation("org.apache.xmlgraphics", "batik-all", "1.18")
	implementation("org.librawfx:LibRawFX:1.9.1")
	implementation("com.twelvemonkeys.imageio", "imageio-batik", "3.12.0")
	implementation("com.twelvemonkeys.imageio", "imageio-bmp", "3.12.0")
	implementation("com.twelvemonkeys.imageio", "imageio-dds", "3.12.0")
	implementation("com.twelvemonkeys.imageio", "imageio-hdr", "3.12.0")
	implementation("com.twelvemonkeys.imageio", "imageio-icns", "3.12.0")
	implementation("com.twelvemonkeys.imageio", "imageio-iff", "3.12.0")
	implementation("com.twelvemonkeys.imageio", "imageio-jpeg", "3.12.0")
	implementation("com.twelvemonkeys.imageio", "imageio-pcx", "3.12.0")
	implementation("com.twelvemonkeys.imageio", "imageio-pict", "3.12.0")
	implementation("com.twelvemonkeys.imageio", "imageio-pnm", "3.12.0")
	implementation("com.twelvemonkeys.imageio", "imageio-psd", "3.12.0")
	implementation("com.twelvemonkeys.imageio", "imageio-sgi", "3.12.0")
	implementation("com.twelvemonkeys.imageio", "imageio-tga", "3.12.0")
	implementation("com.twelvemonkeys.imageio", "imageio-thumbsdb", "3.12.0")
	implementation("com.twelvemonkeys.imageio", "imageio-tiff", "3.12.0")
	implementation("com.twelvemonkeys.imageio", "imageio-webp", "3.12.0")
	implementation("com.twelvemonkeys.imageio", "imageio-xwd", "3.12.0")
	implementation("com.github.gotson.nightmonkeys:imageio-heif:1.0.0")
	implementation("com.github.gotson.nightmonkeys:imageio-jxl:1.0.0")

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

javafx {
	version = "24.0.1"
	modules.add("javafx.swing")
}

kotlin {
	jvmToolchain(21)
}

license {
	// Add a licence header rule, at least one must be present.
	rule(file("codeformat/HEADER"))
}

val year = Calendar.getInstance().get(Calendar.YEAR)

val APPDIR = "\$APPDIR"
val pathSep: String = File.pathSeparator
var dirSep: String = File.separator

if (dirSep == "\\") {
	dirSep = dirSep + dirSep
}

compose.desktop {
	application {
		mainClass = "me.gserv.archival.MainKt"

		jvmArgs.add("-Djava.library.path=$APPDIR$pathSep$APPDIR${dirSep}resources${dirSep}lib$pathSep$APPDIR${dirSep}..${dirSep}runtime")

		jvmArgs.add("--enable-native-access=ALL-UNNAMED")
		jvmArgs.add("--enable-native-access=org.librawfx")

		jvmArgs.add("--add-exports=java.desktop/sun.awt.image=ALL-UNNAMED")
		jvmArgs.add("--add-exports=javafx.graphics/com.sun.javafx.iio=ALL-UNNAMED")
		jvmArgs.add("--add-exports=javafx.graphics/com.sun.javafx.iio.common=ALL-UNNAMED")

		nativeDistributions {
			val buildType = System.getProperties().getOrDefault("buildType", null)?.toString()

			val formats = when (buildType) {
				"app" -> arrayOf(TargetFormat.AppImage)
				"exe" -> arrayOf(TargetFormat.Exe)
				"msi" -> arrayOf(TargetFormat.Msi)
				"dmg" -> arrayOf(TargetFormat.Dmg)
				"pkg" -> arrayOf(TargetFormat.Pkg)
				"deb" -> arrayOf(TargetFormat.Deb)
				"rpm" -> arrayOf(TargetFormat.Rpm)

				else -> arrayOf(
					// TargetFormat.Exe, TargetFormat.Msi,
					// TargetFormat.Dmg, TargetFormat.Pkg,
					TargetFormat.AppImage, // TargetFormat.Deb, TargetFormat.Rpm
				)
			}

			appResourcesRootDir.set(project.layout.projectDirectory.dir("src/main/natives/"))

			targetFormats(*formats)

			modules("java.sql", "java.desktop")

			packageName = "ScanManager"
			packageVersion = project.version.toString().split("-").first()

			copyright = "© $year Gareth Coles, EUPL v1.2."
			description = "Simple photo scan organiser and toolkit."
			licenseFile = rootProject.file("LICENSE")

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
		}
	}
}
