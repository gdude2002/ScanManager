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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.TextStyle
import me.gserv.archival.Colors
import me.gserv.archival.Colors.IColors

@Composable
fun PrimaryButton(
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
	enabled: Boolean = true,
	interactionSource: MutableInteractionSource? = null,
	elevation: ButtonElevation? = ButtonDefaults.elevation(),
	shape: Shape = MaterialTheme.shapes.small,
	border: BorderStroke? = null,
	contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
	content: @Composable ((theme: IColors) -> Unit)
) {
	val material = Colors.getMaterial()
	val theme = Colors.get()

	val textColor = if (enabled) {
		theme.Text
	} else {
		theme.Text.copy(alpha = ContentAlpha.disabled)
	}

	Button(
		onClick = onClick,
		modifier = modifier,
		enabled = enabled,
		interactionSource = interactionSource,
		elevation = elevation,
		shape = shape,
		border = border,

		colors = ButtonDefaults.buttonColors(
			backgroundColor = material.primary,
			contentColor = theme.Text,
			disabledBackgroundColor = theme.Text.copy(alpha = 0.12f).compositeOver(theme.SectionBackground),
			disabledContentColor = theme.Text.copy(alpha = ContentAlpha.disabled),
		),

		contentPadding = contentPadding
	) {
		ProvideTextStyle(TextStyle(color = textColor)) {
			content(theme)
		}
	}
}

@Composable
fun SecondaryButton(
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
	enabled: Boolean = true,
	interactionSource: MutableInteractionSource? = null,
	elevation: ButtonElevation? = ButtonDefaults.elevation(),
	shape: Shape = MaterialTheme.shapes.small,
	border: BorderStroke? = null,
	contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
	content: @Composable ((theme: IColors) -> Unit)
) {
	val material = Colors.getMaterial()
	val theme = Colors.get()

	val textColor = if (enabled) {
		theme.Text
	} else {
		theme.Text.copy(alpha = ContentAlpha.disabled)
	}

	Button(
		onClick = onClick,
		modifier = modifier,
		enabled = enabled,
		interactionSource = interactionSource,
		elevation = elevation,
		shape = shape,
		border = border,

		colors = ButtonDefaults.buttonColors(
			backgroundColor = material.secondary,
			contentColor = theme.Text,
			disabledBackgroundColor = theme.Text.copy(alpha = 0.12f).compositeOver(theme.SectionBackground),
			disabledContentColor = theme.Text.copy(alpha = ContentAlpha.disabled),
		),

		contentPadding = contentPadding
	) {
		ProvideTextStyle(TextStyle(color = textColor)) {
			content(theme)
		}
	}
}

@Composable
fun DangerButton(
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
	enabled: Boolean = true,
	interactionSource: MutableInteractionSource? = null,
	elevation: ButtonElevation? = ButtonDefaults.elevation(),
	shape: Shape = MaterialTheme.shapes.small,
	border: BorderStroke? = null,
	contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
	content: @Composable (RowScope.(theme: IColors) -> Unit)
) {
	val theme = Colors.get()

	val textColor = if (enabled) {
		theme.Text
	} else {
		theme.Text.copy(alpha = ContentAlpha.disabled)
	}

	Button(
		onClick = onClick,
		modifier = modifier,
		enabled = enabled,
		interactionSource = interactionSource,
		elevation = elevation,
		shape = shape,
		border = border,

		colors = ButtonDefaults.buttonColors(
			backgroundColor = theme.DangerBackground,
			contentColor = theme.Text,

			disabledBackgroundColor = theme.DangerForeground.copy(alpha = 0.12f).compositeOver(theme.DangerBackground),
			disabledContentColor = theme.Text.copy(alpha = ContentAlpha.disabled),
		),

		contentPadding = contentPadding
	) {
		ProvideTextStyle(TextStyle(color = textColor)) {
			content(theme)
		}
	}
}
