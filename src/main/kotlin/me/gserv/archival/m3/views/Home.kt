/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.m3.views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import me.gserv.archival.m3.components.MainHeader
import me.gserv.archival.m3.components.SubHeader
import me.gserv.archival.m3.components.layout.ContainerRow
import me.gserv.archival.m3.config.AppSettings

@Composable
fun homeView() {
	Column {
		Row {
			MainHeader(
				"Scan Manager",
				textAlign = TextAlign.Center,
				modifier = Modifier.weight(1f)
			)
		}

		Spacer(modifier = Modifier.height(20.dp))

		ContainerRow(
			rowModifier = Modifier,
			verticalAlignment = Alignment.CenterVertically,
		) {
			Column(modifier = Modifier.weight(1f)) {
				SubHeader("Current Folder")

				Text(
					text = AppSettings.dataFolder,
					overflow = TextOverflow.StartEllipsis,
				)
			}

			Spacer(Modifier.width(10.dp))

			Button(onClick = {}) {
				Text("Change")
			}
		}
	}
}
