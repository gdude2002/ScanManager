/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.m3.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import me.gserv.archival.config.AppConfig
import me.gserv.archival.data.GlobalState
import me.gserv.archival.m3.components.MainHeader
import me.gserv.archival.m3.components.SubHeader
import me.gserv.archival.m3.components.layout.ContainerColumn
import me.gserv.archival.m3.components.layout.ContainerRow

@Composable
fun homeView() {
	Column {
		MainHeader("Scan Manager")

		ContainerRow(
			rowModifier = Modifier,
			verticalAlignment = Alignment.CenterVertically,
		) {
			OutlinedTextField(
				value = AppConfig.dataFolder,
				onValueChange = {},
				label = { Text("Current Folder") },
				modifier = Modifier.weight(1f),
			)

			Spacer(Modifier.width(10.dp))

			Button(onClick = {}) {
				Text("Change")
			}
		}
	}
}
