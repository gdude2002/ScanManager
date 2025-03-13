/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.utils.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import me.gserv.archival.Colors
import me.gserv.archival.Colors.IColors

@Composable
fun PrimaryOutlinedButton(
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
	enabled: Boolean = true,
	interactionSource: MutableInteractionSource? = null,
	content: @Composable ((theme: IColors) -> Unit)
) {
	val theme = Colors.get()

	val textColor = if (enabled) {
		theme.Material.onPrimaryContainer
	} else {
		theme.Material.onSurface.copy(alpha = 0.38f)
	}

	OutlinedButton(
		onClick = onClick,
		modifier = modifier.padding(0.dp),
		enabled = enabled,
		interactionSource = interactionSource,

		border = buttonBorder(enabled, theme.Material.primary),

		colors = ButtonDefaults.outlinedButtonColors(
			contentColor = theme.Material.onPrimaryContainer,
		),
	) {
		ProvideTextStyle(TextStyle(color = textColor)) {
			content(theme)
		}
	}
}

@Composable
fun SecondaryOutlinedButton(
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
	enabled: Boolean = true,
	interactionSource: MutableInteractionSource? = null,
	content: @Composable ((theme: IColors) -> Unit)
) {
	val theme = Colors.get()

	val textColor = if (enabled) {
		theme.Material.onSecondaryContainer
	} else {
		theme.Material.onSurface.copy(alpha = 0.38f)
	}

	OutlinedButton(
		onClick = onClick,
		modifier = modifier,
		enabled = enabled,
		interactionSource = interactionSource,

		border = buttonBorder(enabled, theme.Material.secondary),

		colors = ButtonDefaults.outlinedButtonColors(
			contentColor = theme.Material.onSecondaryContainer,
		),
	) {
		ProvideTextStyle(TextStyle(color = textColor)) {
			content(theme)
		}
	}
}

@Composable
fun TertiaryOutlinedButton(
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
	enabled: Boolean = true,
	interactionSource: MutableInteractionSource? = null,
	content: @Composable ((theme: IColors) -> Unit)
) {
	val theme = Colors.get()

	val textColor = if (enabled) {
		theme.Material.onTertiaryContainer
	} else {
		theme.Material.onSurface.copy(alpha = 0.38f)
	}

	OutlinedButton(
		onClick = onClick,
		modifier = modifier,
		enabled = enabled,
		interactionSource = interactionSource,

		border = buttonBorder(enabled, theme.Material.tertiary),

		colors = ButtonDefaults.outlinedButtonColors(
			contentColor = theme.Material.onTertiaryContainer,
		),
	) {
		ProvideTextStyle(TextStyle(color = textColor)) {
			content(theme)
		}
	}
}

private fun buttonBorder(enabled: Boolean, color: Color) =
	BorderStroke(
		width = 1.dp,
		color = if (enabled) {
			color
		} else {
			color.copy(alpha = 0.12f)
		}
	)
