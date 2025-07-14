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

data class NavigationItem(
	val selected: Boolean,
	val onClick: () -> Unit,
	val icon: @Composable () -> Unit,
	val modifier: Modifier,
	val enabled: Boolean,
	val label: @Composable (() -> Unit)?,
	val alwaysShowLabel: Boolean,
	val badge: (@Composable () -> Unit)?,
	val colors: NavigationSuiteItemColors?,
	val interactionSource: MutableInteractionSource,
	val position: NavigationPosition,
)
