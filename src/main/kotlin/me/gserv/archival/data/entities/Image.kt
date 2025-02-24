/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.data.entities

import me.gserv.archival.data.tables.ImageTable
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.io.File
import java.nio.file.Path
import kotlin.io.path.absolutePathString

class Image(id: EntityID<String>) : Entity<String>(id) {
	companion object : EntityClass<String, Image>(ImageTable) {
		fun create(path: Path, body: Image.() -> Unit) =
			create(path.absolutePathString(), body)

		fun create(path: File, body: Image.() -> Unit) =
			create(path.absolutePath, body)

		fun create(path: String, body: Image.() -> Unit) =
			new(path, body)
	}

	val averageHash by ImageTable.averageHash
	val differenceHash by ImageTable.differenceHash
	val medianHash by ImageTable.medianHash
	val perceptiveHash by ImageTable.perceptiveHash
	val rotationalHash by ImageTable.rotationalHash

	val set by Set referencedOn ImageTable.set
	val quality by ImageTable.quality
}
