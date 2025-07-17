/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.m3

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import me.gserv.archival.config.AppConfig
import me.gserv.archival.data.Database
import me.gserv.archival.m3.components.navigation.WindowNavigation

@Composable
fun mainWindow(scope: ApplicationScope) {
	var currentDestination: View by remember { mutableStateOf(Views.Home) }

	val state = WindowState(
		size = DpSize(1000.dp, 800.dp)
	)

	Window(
		onCloseRequest = {
			Database.close()
			scope.exitApplication()
		},

		state = state,
		title = "Scan Manager"
	) {
		WindowNavigation(
			modifier = Modifier.height(state.size.height),

			items = {
				Views.all
					.forEach {
						item(
							icon = { Icon(it.icon, it.label) },
							label = { Text(it.label, softWrap = false) },
							selected = currentDestination == it,
							onClick = { currentDestination = it },
							position = it.position,

							enabled = !it.needsDataDir || AppConfig.dataFolder != null,
						)
					}
			},

			colors = NavigationSuiteDefaults.colors(
				navigationRailContainerColor = NavigationBarDefaults.containerColor
			),
		) {

		}
	}
}
