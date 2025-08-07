/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.data

import me.gserv.archival.config.AppConfig
import me.gserv.archival.data.Filesystem.EDIT_FOLDER_NAME
import me.gserv.archival.data.Filesystem.FILE_TEMPLATE
import me.gserv.archival.data.Filesystem.ORIGINAL_FOLDER_NAME
import me.gserv.archival.data.Filesystem.bindersFolder
import me.gserv.archival.data.Filesystem.inputFolder
import me.gserv.archival.data.entities.Binder
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.absolute

object Filesystem {
	const val ORIGINAL_FOLDER_NAME = "JPEG Originals"
	const val EDIT_FOLDER_NAME = "PSD Edits"
	const val FILE_TEMPLATE = "{binder}-{set}-{index}.{extension}"

	val bindersFolder
		get() = Path(AppConfig.dataFolder, "binders").absolute()

	val inputFolder
		get() = Path(AppConfig.dataFolder, "import").absolute()

	/* Omitting an argument will return a regex pattern that matches all valid files matching the provided arguments. */
	fun fileName(
		binder: String? = null,
		set: String? = null,
		index: String? = null,
		extension: String? = null
	): String {
		var template = FILE_TEMPLATE

		if (binder == null || set == null || index == null || extension == null) {
			// Escape `.` for regex format
			template = FILE_TEMPLATE.replace(".", "\\.")
		}

		return template
			.replace("{binder}", binder ?: "([^-]*)")
			.replace("{set}", set?.padStart(4, '0') ?: "(\\d+)")
			.replace("{index}", index ?: "(\\d+)")
			.replace("{extension}", extension ?: "(jpeg|psd)")
	}

	fun ensureBinders() {
		val bindersFolder = bindersFolder
			?: error("Data folder hasn't been configured yet.")

		val inputFolder = inputFolder
			?: error("Data folder hasn't been configured yet.")

		bindersFolder.toFile().mkdirs()
		inputFolder.toFile().mkdirs()
	}

	fun binderExists(binder: String): Boolean {
		val folder = bindersFolder
			?: return false

		return File(folder.toFile(), binder).isDirectory()
	}

	fun ensureBinder(binder: Binder): File =
		ensureBinder(binder.id.value)

	fun ensureBinder(binder: String): File {
		val folder = bindersFolder
			?: error("Data folder hasn't been configured yet.")

		val binderFile = File(folder.toFile(), binder)

		File(binderFile, ORIGINAL_FOLDER_NAME).mkdirs()
		File(binderFile, EDIT_FOLDER_NAME).mkdirs()

		return binderFile
	}

	fun deleteBinder(binder: String) {
		val folder = bindersFolder
			?: error("Data folder hasn't been configured yet.")

		val binderFile = File(folder.toFile(), binder)

		if (binderFile.exists()) {
			binderFile.deleteRecursively()
		}
	}
}
