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
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import javafx.scene.paint.Color.color
import me.gserv.archival.Colors

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Tooltip(
	tooltip: @Composable (colors: Colors.IColors) -> Unit,
	modifier: Modifier = Modifier,
	delayMillis: Int = 500,
	tooltipPlacement: TooltipPlacement = TooltipPlacement.CursorPoint(
		DpOffset(0.dp, (-10).dp),
		Alignment.TopCenter
	),
	content: @Composable (colors: Colors.IColors) -> Unit
) {
	Colors.Theme { colors ->
		TooltipArea(
			tooltip = {
				Surface(
					modifier = Modifier.shadow(4.dp),
					color = colors.TooltipBackground,
					shape = RoundedCornerShape(4.dp)
				) {
					ProvideTextStyle(LocalTextStyle.current.copy(color = colors.TooltipText)) {
						tooltip(colors)
					}
				}
			},
			modifier = modifier,
			delayMillis = delayMillis,
			tooltipPlacement = tooltipPlacement,
			content = { content(colors) },
		)
	}
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
	content: @Composable (colors: Colors.IColors) -> Unit
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
