/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.data.entities

import io.github.oshai.kotlinlogging.KotlinLogging
import me.gserv.archival.data.Database
import me.gserv.archival.data.Filesystem
import me.gserv.archival.data.tables.SetTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.and
import java.io.File

class Set(id: EntityID<Long>) : LongEntity(id) {
	companion object : LongEntityClass<Set>(SetTable) {
		fun create(
			id: Long,
			binder: Binder,
			body: Set.() -> Unit = {}
		) = new(id) {
			this.binder = binder

			body()
		}

		fun exists(id: Long, binder: Binder): Boolean =
			Database.transaction {
				find {
					(SetTable.id eq id) and (SetTable.binder eq binder.id)
				}.any()
			}
	}

	val logger = KotlinLogging.logger { }

	var binder by Binder referencedOn SetTable.binder
	var description by SetTable.description
	var date by SetTable.date

	var createdAt by SetTable.createdAt
	var finishedAt by SetTable.finishedAt

	var totalScans by SetTable.totalScans

	fun editFileName(index: String) =
		Filesystem.fileName(binder.id.value, id.value.toString(), index, "pst")

	fun originalFileName(index: String) =
		Filesystem.fileName(binder.id.value, id.value.toString(), index, "jpeg")

	fun getHighestIndex(): Long {
		val files = getFiles()

		return files.maxOfOrNull { it.index } ?: -1
	}

	fun getFiles(): List<FileContainer> {
		val binderDir = Filesystem.ensureBinder(binder)

		val originalDir = File(binderDir, Filesystem.ORIGINAL_FOLDER_NAME)
		val originalRegex = Filesystem.fileName(binder.id.value, id.value.toString(), extension = "jpeg").toRegex()

		val editDir = File(binderDir, Filesystem.EDIT_FOLDER_NAME)
		val editRegex = Filesystem.fileName(binder.id.value, id.value.toString(), extension = "psd").toRegex()

		logger.info {
			buildString {
				appendLine("Searching for files - set ${id.value}, binder ${binder.id.value}")
				appendLine("-> Originals dir: $originalDir")
				appendLine("-> Originals regex: $originalRegex")
				appendLine("-> Edits dir: $editDir")
				appendLine("-> Edits regex: $editRegex")
			}
		}

		val containers = mutableMapOf<Long, MutableFileContainer>()

		for (file in originalDir.listFiles()) {
			val match = originalRegex.matchEntire(file.name)
				?: continue

			val index = match.groupValues[1].toLong()

			containers.getOrPut(index) { MutableFileContainer(index) }
				.original = file
		}

		for (file in editDir.listFiles()) {
			val match = editRegex.matchEntire(file.name)
				?: continue

			val index = match.groupValues[1].toLong()

			containers.getOrPut(index) { MutableFileContainer(index) }
				.edit = file
		}

		return containers.values.map { it.toContainer() }.sortedBy { it.index }
	}

	data class MutableFileContainer(
		var index: Long,
		var original: File? = null,
		var edit: File? = null,
	) {
		fun toContainer(): FileContainer =
			FileContainer(index, original, edit)
	}

	data class FileContainer(
		val index: Long,
		val original: File? = null,
		val edit: File? = null,
	) {
		fun toMutableContainer(): MutableFileContainer =
			MutableFileContainer(index, original, edit)
	}
}
