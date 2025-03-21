/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.windows.binder

import kotlinx.datetime.LocalDate
import me.gserv.archival.data.tables.SetTable
import org.jetbrains.exposed.sql.Expression

sealed class SortingField<T : Any?>(val readableName: String, val expression: Expression<*>) {
	object Number : SortingField<Long>("Number", SetTable.id)

	object Description : SortingField<String?>("Description", SetTable.description)
	object TotalScans : SortingField<Long>("Total Scans", SetTable.totalScans)

	object CompletionDate : SortingField<LocalDate?>("Completion Date", SetTable.finishedAt)
	object CreationDate : SortingField<LocalDate>("Creation Date", SetTable.createdAt)
	object PhotographyDate : SortingField<LocalDate?>("Photography Date", SetTable.date)
}
