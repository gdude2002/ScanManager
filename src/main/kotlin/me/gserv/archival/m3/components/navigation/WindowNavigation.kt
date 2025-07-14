/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.m3.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationRailDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuite
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteColors
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldLayout
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScope
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import me.gserv.archival.m3.components.navigation.NavigationScope
import me.gserv.archival.m3.components.navigation.WindowNavigationSuite

internal val WindowAdaptiveInfoDefault
	@Composable
	get() = currentWindowAdaptiveInfo()


@Composable
fun WindowNavigation(
	items: NavigationScope.() -> Unit,
	modifier: Modifier = Modifier,
	layoutType: NavigationSuiteType =
		NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(WindowAdaptiveInfoDefault),
	colors: NavigationSuiteColors = NavigationSuiteDefaults.colors(),
	containerColor: Color = NavigationSuiteScaffoldDefaults.containerColor,
	contentColor: Color = NavigationSuiteScaffoldDefaults.contentColor,
	content: @Composable () -> Unit = {},
) {
	Surface(modifier = modifier, color = containerColor, contentColor = contentColor) {
		NavigationSuiteScaffoldLayout(
			navigationSuite = {
				WindowNavigationSuite(
					layoutType = layoutType,
					colors = colors,
					content = items,
				)
			},
			layoutType = layoutType,
			content = {
				Box(
					Modifier.consumeWindowInsets(
						when (layoutType) {
							NavigationSuiteType.NavigationBar ->
								NavigationBarDefaults.windowInsets

							NavigationSuiteType.NavigationRail ->
								NavigationRailDefaults.windowInsets

							NavigationSuiteType.NavigationDrawer ->
								DrawerDefaults.windowInsets
							else -> WindowInsets(0, 0, 0, 0)
						}
					)
				) {
					content()
				}
			}
		)
	}
}
