/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.windows.binder

sealed class FilterState(val readableName: String) {
    object All : FilterState("All")
    object Complete : FilterState("Complete")
    object Incomplete : FilterState("Incomplete")
}
