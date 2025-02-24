/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.data.tables

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column

object BinderTable : IdTable<String>("binders") {
	override val id: Column<EntityID<String>> = varchar("id", 128).entityId()
	override val primaryKey: PrimaryKey = PrimaryKey(id)

	val slug = varchar("slug", 128)
	val archived = bool("archived").default(false)
}
