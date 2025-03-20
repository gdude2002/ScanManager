/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.utils.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import me.gserv.archival.Colors
import me.gserv.archival.Colors.IColors

@Composable
fun PrimaryIconButton(
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

	IconButton(
		onClick = onClick,
		modifier = modifier,
		enabled = enabled,
		interactionSource = interactionSource,

		colors = IconButtonDefaults.iconButtonColors(
			containerColor = theme.Material.primaryContainer,
			contentColor = theme.Material.onPrimaryContainer,
		),
	) {
		ProvideTextStyle(TextStyle(color = textColor)) {
			content(theme)
		}
	}
}

@Composable
fun SecondaryIconButton(
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

	IconButton(
		onClick = onClick,
		modifier = modifier,
		enabled = enabled,
		interactionSource = interactionSource,

		colors = IconButtonDefaults.iconButtonColors(
			containerColor = theme.Material.secondaryContainer,
			contentColor = theme.Material.onSecondaryContainer,
		),
	) {
		ProvideTextStyle(TextStyle(color = textColor)) {
			content(theme)
		}
	}
}

@Composable
fun TertiaryIconButton(
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

	IconButton(
		onClick = onClick,
		modifier = modifier,
		enabled = enabled,
		interactionSource = interactionSource,

		colors = IconButtonDefaults.iconButtonColors(
			containerColor = theme.Material.tertiaryContainer,
			contentColor = theme.Material.onTertiaryContainer,
		),
	) {
		ProvideTextStyle(TextStyle(color = textColor)) {
			content(theme)
		}
	}
}
