/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.data

import me.gserv.archival.config.AppConfig
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.absolute

object Filesystem {
	const val ORIGINAL_FOLDER_NAME = "JPEG Originals"
	const val EDIT_FOLDER_NAME = "PSD Edits"

	val bindersFolder
		get() =
			AppConfig.dataFolder?.let { Path(it, "binders").absolute() }

	fun ensureBinders() {
		val folder = bindersFolder
			?: error("Data folder hasn't been configured yet.")

		folder.toFile().mkdirs()
	}

	fun binderExists(binder: String): Boolean {
		val folder = bindersFolder
			?: return false

		return File(folder.toFile(), binder).isDirectory()
	}

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
