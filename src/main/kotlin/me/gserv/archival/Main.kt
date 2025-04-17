/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival

import androidx.compose.ui.window.application
import io.github.oshai.kotlinlogging.KotlinLogging
import me.gserv.archival.config.AppConfig
import me.gserv.archival.data.Database
import me.gserv.archival.utils.currentOs
import me.gserv.archival.windows.DropTargetWindow
import me.gserv.archival.windows.MainWindow
import kotlin.io.path.Path
import kotlin.io.path.absolute
import kotlin.io.path.div
import kotlin.system.exitProcess

lateinit var mainWindow: MainWindow
lateinit var dropTarget: DropTargetWindow

private val logger = KotlinLogging.logger { }

fun copyNatives() {
	val cwd = Path(".").absolute()
	val resourcesDir = Path(System.getProperty("compose.application.resources.dir"))

	logger.info {
		"Java library path:\n" +
			System.getProperties().getOrDefault("java.library.path", "")?.toString()
				?.split(currentOs.envSep)?.joinToString("    \n") + "\n"
	}

	val libDir = resourcesDir / "lib"
	val appDir = cwd

	logger.info { "Compose application resources dir: \n    $resourcesDir" }
	logger.info { "Libraries dir: \n    $libDir" }
	logger.info { "Current working directory: \n    $cwd\n" }

	if (cwd == resourcesDir.parent) {
		logger.info { "NOTE: We appear to be running in an installed application context."}
	} else {
		logger.info { "NOTE: We appear to be running in a development environment."}
	}

	libDir.toFile().listFiles().forEach { file ->
		val target = (appDir / file.name).toFile().absoluteFile

		logger.info { "Copying: ${file.name} -> $target" }

		file.copyTo(target, overwrite = true).absoluteFile
	}
}

fun main() {
	Thread.setDefaultUncaughtExceptionHandler { _, e ->
		logger.error(e) { "Uncaught exception" }
		exitProcess(1)
	}

	copyNatives()

	application {
		if (System.getenv().contains("NO_LOAD")) {
			Database.connect("mem:test")
			Database.printCreateStatements()
		} else {
			AppConfig.load()

			mainWindow = MainWindow(this)
			dropTarget = DropTargetWindow(mainWindow)

			dropTarget.create()
			mainWindow.open()
		}
	}
}
