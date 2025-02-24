/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.config

import androidx.compose.runtime.mutableStateOf
import me.gserv.archival.data.Database
import me.gserv.archival.data.Filesystem
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.reader
import kotlin.io.path.writer

object AppConfig {
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

	var theme: String?
		get() = current.theme
		set(value) {
			current.theme = value
		}

	var currentState = mutableStateOf(Config())

	var current: Config
		get() = currentState.value
		set(value) {
			currentState.value = value
		}

	fun load(force: Boolean = true) {
		if (loaded && !force) {
			return
		}

		if (configFile.exists()) {
			val props = Properties()

			props.load(configFile.reader(Charsets.UTF_8))

			current = Config(
				dataFolder = props.getProperty("dataFolder"),
				theme = props.getProperty("theme"),
			)
		} else {
			current = Config()
			save(current)
		}

		Filesystem.ensureBinders()

		if (dataFolder != null) {
			Database.connect(dataFolder!!)
		}

		loaded = true
	}

	fun save(config: Config = current) {
		val props = Properties()

		if (config.dataFolder != null) {
			props.setProperty("dataFolder", config.dataFolder)
		}

		if (config.theme != null) {
			props.setProperty("theme", config.theme)
		}

		props.store(configFile.writer(Charsets.UTF_8), null)
	}

	data class Config(
		var dataFolder: String? = null,
		var theme: String? = null,
	) {
		fun save() = save(this)
	}
}
