/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.m3.components.navigation

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuOpen
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteColors
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.math.exp

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
fun WindowNavigationSuite(
	modifier: Modifier = Modifier,
	colors: NavigationSuiteColors = NavigationSuiteDefaults.colors(),
	content: NavigationScope.() -> Unit
) {
	val scope by rememberStateOfItems(content)
	val defaultItemColors = NavigationSuiteDefaults.itemColors()
	var expanded by remember { mutableStateOf(false) }
	var showLabels by remember { mutableStateOf(false) }

	// I'm only supporting the drawer type. Do I look like Google??

	val animatedWidth by animateDpAsState(
		if (expanded) {
			154.dp
		} else {
			72.dp
		},
		label = "width",
		finishedListener = { dp ->
			showLabels = expanded
		}
	)

	PermanentNavigationDrawer(
		modifier = modifier,

		drawerContent = {
			PermanentDrawerSheet(
				modifier = Modifier
					.width(animatedWidth),

				drawerContainerColor = colors.navigationRailContainerColor,
				drawerContentColor = colors.navigationDrawerContentColor
			) {
				@Composable
				fun display(it: NavigationItem) {
					if (!it.enabled) {
						return
					}

					DrawerItem(
						modifier = it.modifier
							.padding(end = 8.dp, start = 8.dp),
						selected = it.selected,
						onClick = it.onClick,
						icon = it.icon,
						badge = it.badge,
						label = { it.label?.invoke() },
						showLabel = expanded,
						colors = it.colors?.navigationDrawerItemColors
							?: defaultItemColors.navigationDrawerItemColors,
						interactionSource = it.interactionSource
					)
				}

				Spacer(Modifier.height(8.dp))

				DrawerItem(
					modifier = Modifier
						.padding(end = 8.dp, start = 8.dp),
					selected = false,
					onClick = { expanded = !expanded },

					icon = {
						if (expanded) {
							Icon(Icons.AutoMirrored.Outlined.MenuOpen, "")
						} else {
							Icon(Icons.Outlined.Menu, "")
						}
					},

					label = { Text("Hide", softWrap = false) },
					showLabel = expanded,
					colors = defaultItemColors.navigationDrawerItemColors,
				)

				// TODO: Back button

				scope.getStart().filter { it.enabled }.forEachIndexed { index, item ->
					Spacer(Modifier.height(8.dp))

					display(item)
				}

				Spacer(Modifier.weight(1f))

				scope.getMiddle().filter { it.enabled }.forEachIndexed { index, item ->
					if (index != 0) {
						Spacer(Modifier.height(8.dp))
					}

					display(item)
				}

				Spacer(Modifier.weight(1f))

				scope.getEnd().filter { it.enabled }.forEachIndexed { index, item ->
					if (index != 0) {
						Spacer(Modifier.height(8.dp))
					}

					display(item)
				}

				Spacer(Modifier.height(8.dp))
			}
		}
	) {

	}
}
