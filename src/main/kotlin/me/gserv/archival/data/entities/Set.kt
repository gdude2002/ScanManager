/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.data.entities

import me.gserv.archival.data.Database
import me.gserv.archival.data.tables.SetTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.and

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

    var binder by Binder referencedOn SetTable.binder
    var description by SetTable.description
    var date by SetTable.date

    var createdAt by SetTable.createdAt
    var finishedAt by SetTable.finishedAt

    var totalScans by SetTable.totalScans
}
