/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.m3.components.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.adaptive.navigationsuite.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun WindowNavigation(
	items: NavigationScope.() -> Unit,
	modifier: Modifier = Modifier,
	colors: NavigationSuiteColors = NavigationSuiteDefaults.colors(),
	containerColor: Color = NavigationSuiteScaffoldDefaults.containerColor,
	contentColor: Color = NavigationSuiteScaffoldDefaults.contentColor,
	content: @Composable () -> Unit = {},
) {
	// I'm only supporting the drawer type. Do I look like Google??

	Surface(modifier = modifier, color = containerColor, contentColor = contentColor) {
		NavigationSuiteScaffoldLayout(
			layoutType = NavigationSuiteType.NavigationDrawer,

			navigationSuite = {
				WindowNavigationSuite(
					colors = colors,
					content = items,
				)
			},

			content = {
				Box(
					Modifier.consumeWindowInsets(DrawerDefaults.windowInsets)
				) {
					content()
				}
			}
		)
	}
}
