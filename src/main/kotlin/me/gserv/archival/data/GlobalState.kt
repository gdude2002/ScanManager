/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import me.gserv.archival.data.entities.Binder
import me.gserv.archival.data.entities.Set

object GlobalState {
	val binders = mutableStateListOf<Binder>()
	var binder by mutableStateOf<Binder?>(null)
	var set by mutableStateOf<Set?>(null)

	var sets: SnapshotStateList<Set> = mutableStateListOf()

	fun clear() {
		binders.clear()
	}

	fun load() {
		loadBinders()
	}

	fun loadBinders() {
		binders.clear()

		Database.transaction {
			binders.addAll(
				Binder.all()
					.sortedWith(
						compareBy({ it.archived }, { it.id.value })
					)
			)
		}
	}
}
