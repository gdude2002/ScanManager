/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.m3.components.layout

import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.Modifier.Companion
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import kotlinx.serialization.json.JsonNull.content

@Composable
fun ResponsiveStaggeredGrid(
	breakAtWidth: Int = WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND,

	columns: StaggeredGridCells,
	rows: StaggeredGridCells,

	modifier: Modifier = Modifier,
	state: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
	contentPadding: PaddingValues = PaddingValues(0.dp),
	reverseLayout: Boolean = false,
	itemSpacing: Dp = 10.dp,
	horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(10.dp),
	verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(10.dp),
	flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
	userScrollEnabled: Boolean = true,
	overscrollEffect: OverscrollEffect? = rememberOverscrollEffect(),
	content: LazyStaggeredGridScope.(isWide: Boolean) -> Unit,
) {
	val current = currentWindowAdaptiveInfo().windowSizeClass
	val isWide = current.isWidthAtLeastBreakpoint(breakAtWidth)

	if (current.isWidthAtLeastBreakpoint(breakAtWidth)) {
		LazyVerticalStaggeredGrid(
			columns = columns,
			contentPadding = contentPadding,
			flingBehavior = flingBehavior,
			horizontalArrangement = horizontalArrangement,
			modifier = modifier,
			overscrollEffect = overscrollEffect,
			reverseLayout = reverseLayout,
			state = state,
			userScrollEnabled = userScrollEnabled,
			verticalItemSpacing = itemSpacing,
		) {
			content(this, isWide)
		}
	} else {
		LazyHorizontalStaggeredGrid(
			contentPadding = contentPadding,
			flingBehavior = flingBehavior,
			horizontalItemSpacing = itemSpacing,
			modifier = modifier,
			overscrollEffect = overscrollEffect,
			reverseLayout = reverseLayout,
			rows = rows,
			state = state,
			userScrollEnabled = userScrollEnabled,
			verticalArrangement = verticalArrangement,
		) {
			content(this, isWide)
		}
	}
}

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
