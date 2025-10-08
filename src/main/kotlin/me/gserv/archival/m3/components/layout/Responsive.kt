/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.m3.components.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.window.core.layout.WindowSizeClass

@Composable
fun ResponsiveRow(
	breakAtWidth: Int = WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND,
	modifier: Modifier = Modifier,

	colVerticalArrangement: Arrangement.Vertical = Arrangement.Top,
	colHorizontalAlignment: Alignment.Horizontal = Alignment.Start,

	rowHorizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
	rowVerticalAlignment: Alignment.Vertical = Alignment.Top,

	content: @Composable (isWide: Boolean) -> Unit = {},
) {
	val current = currentWindowAdaptiveInfo().windowSizeClass
	val isWide = current.isWidthAtLeastBreakpoint(breakAtWidth)

	if (current.isWidthAtLeastBreakpoint(breakAtWidth)) {
		Row(
			modifier,

			rowHorizontalArrangement,
			rowVerticalAlignment,
		) {
			content(isWide)
		}
	} else {
		Column (
			modifier,

			colVerticalArrangement,
			colHorizontalAlignment,
		) {
			content(isWide)
		}
	}
}
