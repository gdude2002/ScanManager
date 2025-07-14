/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.m3.components.navigation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.navigationsuite.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import me.gserv.archival.m3.components.WindowAdaptiveInfoDefault

@Composable
private fun rememberStateOfItems(
	content: NavigationScope.() -> Unit
): State<NavigationScope> {
	val latestContent = rememberUpdatedState(content)
	return remember {
		derivedStateOf { NavigationScope().apply(latestContent.value) }
	}
}

@Composable
private fun NavigationItemIcon(
	icon: @Composable () -> Unit,
	badge: (@Composable () -> Unit)? = null,
) {
	if (badge != null) {
		BadgedBox(badge = { badge.invoke() }) {
			icon()
		}
	} else {
		icon()
	}
}

@Composable
fun WindowNavigationSuite(
	modifier: Modifier = Modifier,
	layoutType: NavigationSuiteType =
		NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(WindowAdaptiveInfoDefault),
	colors: NavigationSuiteColors = NavigationSuiteDefaults.colors(),
	content: NavigationScope.() -> Unit
) {
	val scope by rememberStateOfItems(content)
	val defaultItemColors = NavigationSuiteDefaults.itemColors()

	when (layoutType) {
		NavigationSuiteType.NavigationBar -> {
			NavigationBar(
				modifier = modifier,
				containerColor = colors.navigationBarContainerColor,
				contentColor = colors.navigationBarContentColor
			) {
				@Composable
				fun display(it: NavigationItem) {
					NavigationBarItem(
						modifier = it.modifier,
						selected = it.selected,
						onClick = it.onClick,
						icon = { NavigationItemIcon(icon = it.icon, badge = it.badge) },
						enabled = it.enabled,
						label = it.label,
						alwaysShowLabel = it.alwaysShowLabel,
						colors = it.colors?.navigationBarItemColors
							?: defaultItemColors.navigationBarItemColors,
						interactionSource = it.interactionSource
					)
				}

				scope.getStart().forEach { display(it) }

				Spacer(Modifier.weight(1f))

				scope.getMiddle().forEach { display(it) }

				Spacer(Modifier.weight(1f))

				scope.getEnd().forEach { display(it) }
			}
		}

		NavigationSuiteType.NavigationRail -> {
			NavigationRail(
				modifier = modifier,
				containerColor = colors.navigationRailContainerColor,
				contentColor = colors.navigationRailContentColor
			) {
				@Composable
				fun display(it: NavigationItem) {
					NavigationRailItem(
						modifier = it.modifier,
						selected = it.selected,
						onClick = it.onClick,
						icon = { NavigationItemIcon(icon = it.icon, badge = it.badge) },
						enabled = it.enabled,
						label = it.label,
						alwaysShowLabel = it.alwaysShowLabel,
						colors = it.colors?.navigationRailItemColors
							?: defaultItemColors.navigationRailItemColors,
						interactionSource = it.interactionSource
					)
				}

				scope.getStart().forEach { display(it) }

				Spacer(Modifier.weight(1f))

				scope.getMiddle().forEach { display(it) }

				Spacer(Modifier.weight(1f))

				scope.getEnd().forEach { display(it) }
			}
		}

		NavigationSuiteType.NavigationDrawer -> {
			PermanentDrawerSheet(
				modifier = modifier,
				drawerContainerColor = colors.navigationDrawerContainerColor,
				drawerContentColor = colors.navigationDrawerContentColor
			) {
				@Composable
				fun display(it: NavigationItem) {
					NavigationDrawerItem(
						modifier = it.modifier,
						selected = it.selected,
						onClick = it.onClick,
						icon = it.icon,
						badge = it.badge,
						label = { it.label?.invoke() ?: Text("") },
						colors = it.colors?.navigationDrawerItemColors
							?: defaultItemColors.navigationDrawerItemColors,
						interactionSource = it.interactionSource
					)
				}

				scope.getStart().forEach { display(it) }

				Spacer(Modifier.weight(1f))

				scope.getMiddle().forEach { display(it) }

				Spacer(Modifier.weight(1f))

				scope.getEnd().forEach { display(it) }
			}
		}

		NavigationSuiteType.None -> { /* Do nothing. */ }
	}
}
