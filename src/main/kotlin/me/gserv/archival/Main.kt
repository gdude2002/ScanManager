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
import me.gserv.archival.windows.DropTargetWindow
import me.gserv.archival.windows.MainWindow
import kotlin.system.exitProcess

lateinit var mainWindow: MainWindow
lateinit var dropTarget: DropTargetWindow

fun main() {
	val logger = KotlinLogging.logger { }

	Thread.setDefaultUncaughtExceptionHandler { _, e ->
		logger.error(e) { "Uncaught exception" }
		exitProcess(1)
	}

	application {
		logger.info {
			"Compose application resources dir: " +
				System.getProperties().getOrDefault("compose.application.resources.dir", "")?.toString()
		}

		logger.info {
			"Java library path: " +
				System.getProperties().getOrDefault("java.library.path", "")?.toString()
		}

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
