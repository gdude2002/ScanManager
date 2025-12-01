/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.m3.data.entities

import me.gserv.archival.m3.data.tables.ImageTable
import me.gserv.archival.utils.EncodedHashes
import me.gserv.archival.utils.Hashes
import me.gserv.archival.utils.relativeToDataDir
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.io.File
import java.nio.file.Path

class Image(id: EntityID<String>) : Entity<String>(id) {
	companion object : EntityClass<String, Image>(ImageTable) {
		fun create(path: File, body: Image.() -> Unit) =
			create(path.toPath(), body)

		fun create(path: Path, body: Image.() -> Unit) =
			create(path.relativeToDataDir().toString(), body)

		fun create(path: String, body: Image.() -> Unit) =
			new(path, body)
	}

	fun addHashes(hashes: Hashes) {
		addHashes(hashes.encode())
	}

	fun addHashes(hashes: EncodedHashes) {
		averageHash = hashes.average
		differenceHash = hashes.difference
		medianHash = hashes.median
		perceptiveHash = hashes.perceptive
		rotationalHash = hashes.rotational
	}

	var averageHash by ImageTable.averageHash
	var differenceHash by ImageTable.differenceHash
	var medianHash by ImageTable.medianHash
	var perceptiveHash by ImageTable.perceptiveHash
	var rotationalHash by ImageTable.rotationalHash

	var binder by Binder referencedOn ImageTable.binder
	var set by Set referencedOn ImageTable.set

	var quality by ImageTable.quality
}
