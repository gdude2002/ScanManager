/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.m3.components.navigation

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItemColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

class NavigationScope {
	fun item(
		alwaysShowLabel: Boolean = true,
		badge: (@Composable () -> Unit)? = null,
		colors: NavigationSuiteItemColors? = null,
		enabled: Boolean = true,
		icon: @Composable () -> Unit,
		interactionSource: MutableInteractionSource? = null,
		label: @Composable (() -> Unit)? = null,
		modifier: Modifier = Modifier,
		onClick: () -> Unit,
		position: NavigationPosition = NavigationPosition.MIDDLE,
		selected: Boolean,
	) {
		itemList.add(
			NavigationItem(
				alwaysShowLabel = alwaysShowLabel,
				badge = badge,
				colors = colors,
				enabled = enabled,
				icon = icon,
				interactionSource = interactionSource  ?: MutableInteractionSource(),
				label = label,
				modifier = modifier,
				onClick = onClick,
				position = position,
				selected = selected,
			)
		)
	}

	fun getStart() =
		itemList.filter { it.position == NavigationPosition.START }

	fun getMiddle() =
		itemList.filter { it.position == NavigationPosition.START }

	fun getEnd() =
		itemList.filter { it.position == NavigationPosition.START }

	val itemList: MutableList<NavigationItem> = mutableListOf()

	val itemsCount: Int
		get() = itemList.size
}
