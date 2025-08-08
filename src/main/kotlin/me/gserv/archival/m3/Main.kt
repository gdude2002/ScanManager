/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.m3

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.github.kdroidfilter.platformtools.darkmodedetector.isSystemInDarkMode
import io.github.oshai.kotlinlogging.KotlinLogging
import me.gserv.archival.data.Database
import me.gserv.archival.loadNatives
import me.gserv.archival.m3.config.AppSettings
import me.gserv.archival.m3.config.Theme
import me.gserv.archival.m3.resources.fonts.Fonts
import me.gserv.archival.setup
import org.slf4j.bridge.SLF4JBridgeHandler
import java.io.StringWriter
import java.util.*
import java.util.logging.LogManager
import kotlin.system.exitProcess

private val logger = KotlinLogging.logger { }

fun main() {
	setup()

	SLF4JBridgeHandler.install()

	val props = Properties()
	val stream = StringWriter()

	props.setProperty(".level", "FINEST")
	props.store(stream, "")

	LogManager.getLogManager().readConfiguration(stream.toString().byteInputStream())

	Thread.setDefaultUncaughtExceptionHandler { _, e ->
		logger.error(e) { "Uncaught exception" }
		exitProcess(1)
	}

	loadNatives()

	AppSettings.load()

	application {
		if (System.getenv().contains("NO_LOAD")) {
			Database.connect("mem:test")
			Database.printCreateStatements()
		} else {
			val systemDarkMode = isSystemInDarkMode()

			AppSettings.setup()

			val theme = when (AppSettings.theme) {
				Theme.AUTOMATIC -> if (systemDarkMode) {
					darkColorScheme()
				} else {
					lightColorScheme()
				}

				Theme.DARK -> darkColorScheme()
				Theme.LIGHT -> lightColorScheme()
			}

			val windowState = rememberWindowState(
				size = DpSize(1000.dp, 800.dp)
			)

			Window(
				onCloseRequest = {
					Database.close()
					this@application.exitApplication()
				},

				state = windowState,
				title = "Scan Manager"
			) {
				MaterialTheme(
					colorScheme = theme,
					typography = Fonts.get(AppSettings.textFont)?.typography() ?: Fonts.Inter.typography(),
				) {
					mainWindow(windowState)
				}
			}
		}
	}
}
