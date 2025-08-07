/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.config

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.oshai.kotlinlogging.KotlinLogging
import me.gserv.archival.baseDir
import me.gserv.archival.data.Database
import me.gserv.archival.data.Filesystem
import me.gserv.archival.m3.resources.fonts.Fonts
import java.util.*
import kotlin.io.path.*

object AppConfig {
	val logger = KotlinLogging.logger { }
	val configFile = Path(System.getProperty("user.home"), "ScanManager", "config.properties")

	var loaded = false

	var dataFolder: String
		get() = current.dataFolder
		set(value) {
			current.dataFolder = value

			Database.connect(value)
		}

	var current: Config by mutableStateOf(Config())

	fun load(force: Boolean = true) {
		logger.debug { "Loading configuration..." }

		if (loaded && !force) {
			logger.debug { "Skipped: Not reloading existing configuration" }

			return
		}

		if (configFile.exists()) {
			logger.debug { "Loading file: ${configFile.absolutePathString()}" }

			val props = Properties()

			props.load(configFile.reader(Charsets.UTF_8))

			current = Config(
				dataFolder = props.getProperty("dataFolder") ?: (baseDir / "data").absolutePathString(),
			)
		} else {
			logger.debug { "Saving default configuration to file: ${configFile.absolutePathString()}" }

			current = Config()
			save(current)
		}

		logger.debug { "Ensuring data folder exists..." }

		Filesystem.ensureBinders()

		logger.debug { "Connecting to database..." }

		Database.connect(dataFolder)

		loaded = true
	}

	fun save(config: Config = current) {
		val props = Properties()

		props.setProperty("dataFolder", config.dataFolder)
		props.store(configFile.writer(Charsets.UTF_8), null)

		logger.debug { "Configuration saved successfully" }
	}

	data class Config(
		var dataFolder: String = (baseDir / "data").absolutePathString(),

		var lightMode: Boolean? = null,
		var alwaysExpandSidebar: Boolean = false,

		var headerFont: String = Fonts.Poppins.name,
		var textFont: String = Fonts.Inter.name,
	) {
		fun save() =
			save(this)
	}
}
