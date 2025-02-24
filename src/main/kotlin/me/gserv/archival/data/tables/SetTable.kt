/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.data.tables

import kotlinx.datetime.LocalDate
import me.gserv.archival.utils.now
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.date

object SetTable : LongIdTable("sets") {
	val binder = reference("binder", BinderTable.id)

	val description = text("description").nullable()
	val date = date("date").nullable()

	val createdAt = date("created_at").clientDefault { LocalDate.now() }
	val finishedAt = date("finished_at").nullable()

	val totalScans = long("total_scans").default(0L)
}
