/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.m3.config

import androidx.compose.runtime.*
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import me.gserv.archival.baseDir
import me.gserv.archival.data.Database
import me.gserv.archival.m3.resources.fonts.Fonts
import java.util.*
import kotlin.io.path.*

object AppSettings {
	private val logger = KotlinLogging.logger { }
	private val json = Json {
		encodeDefaults = true
		ignoreUnknownKeys = true
	}
	private val mutex = Mutex()

	private val oldConfigFile = Path(System.getProperty("user.home"), "ScanManager", "config.properties")
	private val configFile = Path(System.getProperty("user.home"), "ScanManager", "config.json")

	var current: Config by mutableStateOf(Config())

	var loaded = false

	var dataFolder: String
		get() = current.dataFolder
		set(value) {
			current.dataFolder = value

			Database.connect(value)

			save()
		}

	var alwaysExpandSidebar: SidebarMode by mutableStateOf(SidebarMode.TOGGLE)
	var headerFont: String by mutableStateOf(Fonts.Poppins.name)
	var theme: Theme by mutableStateOf(Theme.AUTOMATIC)
	var textFont: String by mutableStateOf(Fonts.Inter.name)

	@Composable
	fun setup() {
		LaunchedEffect(alwaysExpandSidebar) {
			if (current.alwaysExpandSidebar != alwaysExpandSidebar) {
				current.alwaysExpandSidebar = alwaysExpandSidebar

				if (loaded) {
					mutex.withLock {
						save()
					}
				}
			}
		}

		LaunchedEffect(headerFont) {
			if (current.headerFont != headerFont) {
				current.headerFont = headerFont

				if (loaded) {
					mutex.withLock {
						save()
					}
				}
			}
		}

		LaunchedEffect(theme) {
			if (current.theme != theme) {
				current.theme = theme

				if (loaded) {
					mutex.withLock {
						save()
					}
				}
			}
		}

		LaunchedEffect(textFont) {
			if (current.textFont != textFont) {
				current.textFont = textFont

				if (loaded) {
					mutex.withLock {
						save()
					}
				}
			}
		}
	}

	fun load(force: Boolean = true) {
		logger.debug { "Loading configuration..." }

		if (loaded && !force) {
			logger.debug { "Skipped: Not reloading existing configuration" }

			return
		}

		loaded = false

		if (oldConfigFile.exists()) {
			logger.debug { "Migrating old configuration file..." }

			migrate()
		}

		if (configFile.exists()) {
			logger.debug { "Loading file: ${configFile.absolutePathString()}" }

			current = json.decodeFromString(configFile.readText())
		} else {
			logger.debug { "Saving default configuration to file: ${configFile.absolutePathString()}" }

			current = Config()

			save()
		}

		logger.debug { "Ensuring data folder exists..." }

//		Filesystem.ensureBinders()

		logger.debug { "Connecting to database..." }

//		Database.connect(dataFolder)

		alwaysExpandSidebar = current.alwaysExpandSidebar
		headerFont = current.headerFont
		theme = current.theme
		textFont = current.textFont

		loaded = true
	}

	fun migrate() {
		val props = Properties()

		props.load(oldConfigFile.reader(Charsets.UTF_8))

		val newConfig = Config(
			dataFolder = props.getProperty("dataFolder")
				?: (baseDir / "data").absolutePathString(),
		)

		save(newConfig)

		oldConfigFile.moveTo(
			Path(System.getProperty("user.home"), "ScanManager", "config-old.properties")
		)
	}

	fun save(config: Config = current) {
		configFile.writeText(
			json.encodeToString(config)
		)

		logger.debug { "Configuration saved successfully" }
	}

	@Serializable
	data class Config(
		var dataFolder: String = (baseDir / "data").absolutePathString(),

		var theme: Theme = Theme.AUTOMATIC,
		var alwaysExpandSidebar: SidebarMode = SidebarMode.TOGGLE,

		var headerFont: String = Fonts.Poppins.name,
		var textFont: String = Fonts.Inter.name,

		val version: Int = 1,
	)
}
