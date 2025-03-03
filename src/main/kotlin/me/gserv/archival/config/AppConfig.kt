/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.config

import androidx.compose.runtime.mutableStateOf
import io.github.oshai.kotlinlogging.KotlinLogging
import me.gserv.archival.data.Database
import me.gserv.archival.data.Filesystem
import java.util.*
import kotlin.io.path.*

object AppConfig {
	val logger = KotlinLogging.logger { }
	val configFile = Path(System.getProperty("user.home"), "ScanManager.properties")

	var loaded = false

	var dataFolder: String?
		get() = current.dataFolder
		set(value) {
			current.dataFolder = value

			if (value != null) {
				Database.connect(value)
			} else {
				Database.close()
			}
		}

	var currentState = mutableStateOf(Config())

	var current: Config
		get() = currentState.value
		set(value) {
			currentState.value = value
		}

	fun load(force: Boolean = true) {
		logger.info { "Loading configuration..." }

		if (loaded && !force) {
			logger.info { "Skipped: Not reloading existing configuration" }

			return
		}

		if (configFile.exists()) {
			logger.info { "Loading file: ${configFile.absolutePathString()}" }

			val props = Properties()

			props.load(configFile.reader(Charsets.UTF_8))

			current = Config(
				dataFolder = props.getProperty("dataFolder"),
			)
		} else {
			logger.info { "Saving default configuration to file: ${configFile.absolutePathString()}" }

			current = Config()
			save(current)
		}

		logger.info { "Ensuring data folder exists..." }

		Filesystem.ensureBinders()

		if (dataFolder != null) {
			logger.info { "Connecting to database..." }

			Database.connect(dataFolder!!)
		}

		loaded = true
	}

	fun save(config: Config = current) {
		val props = Properties()

		if (config.dataFolder != null) {
			props.setProperty("dataFolder", config.dataFolder)
		}

		props.store(configFile.writer(Charsets.UTF_8), null)

		logger.info { "Configuration saved successfully" }
	}

	data class Config(
		var dataFolder: String? = null,
	) {
		fun save() = save(this)
	}
}
