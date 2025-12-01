/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.m3.data.tables

import me.gserv.archival.m3.data.enums.ImageQuality
import org.jetbrains.exposed.dao.id.IdTable

object ImageTable : IdTable<String>("images") {
	override val id = text("id").entityId()
	override val primaryKey: PrimaryKey = PrimaryKey(BinderTable.id)

	val binder = reference("binder_id", BinderTable.id)
	val set = reference("set_id", SetTable.id)

	val quality = enumeration<ImageQuality>("quality")
		.clientDefault { ImageQuality.NOT_EVALUATED }

	val averageHash = text("average_hash")
	val differenceHash = text("difference_hash")
	val medianHash = text("median_hash")
	val perceptiveHash = text("perceptive_hash")
	val rotationalHash = text("rotational_hash")  // RotP
}
