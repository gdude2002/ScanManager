/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.m3.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import me.gserv.archival.m3.components.MainHeader
import me.gserv.archival.m3.data.GlobalState

@Composable
fun setsView() {
	Column {
		Row {
			MainHeader(
				"Sets",
				textAlign = TextAlign.Center,
				modifier = Modifier
					.weight(1f)
			)
		}

		if (GlobalState.binder == null) {
			Row {
				Text(
					"No binder selected. Please pick one from the Binders page.",
					textAlign = TextAlign.Center,
					modifier = Modifier
						.weight(1f)
				)
			}
		} else {
			Text("#TODO")
		}
	}
}
