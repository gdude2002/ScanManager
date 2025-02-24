/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.utils

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.TooltipPlacement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Tooltip(
	tooltip: @Composable () -> Unit,
	modifier: Modifier = Modifier,
	delayMillis: Int = 500,
	tooltipPlacement: TooltipPlacement = TooltipPlacement.CursorPoint(
		DpOffset(0.dp, (-10).dp),
		Alignment.TopCenter
	),
	content: @Composable () -> Unit
) {
	TooltipArea(
		tooltip = {
			Surface(
				modifier = Modifier.shadow(4.dp),
				color = Color(255, 255, 210),
				shape = RoundedCornerShape(4.dp)
			) {
				tooltip()
			}
		},
		modifier = modifier,
		delayMillis = delayMillis,
		tooltipPlacement = tooltipPlacement,
		content = content,
	)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StringTooltip(
	text: String,
	modifier: Modifier = Modifier,
	delayMillis: Int = 500,
	tooltipPlacement: TooltipPlacement = TooltipPlacement.CursorPoint(
		DpOffset(0.dp, (-10).dp),
		Alignment.TopCenter
	),
	content: @Composable () -> Unit
) {
	Tooltip(
		tooltip = {
			Text(
				text = text,
				modifier = Modifier.padding(10.dp)
			)
		},
		modifier = modifier,
		delayMillis = delayMillis,
		tooltipPlacement = tooltipPlacement,
		content = content,
	)
}
