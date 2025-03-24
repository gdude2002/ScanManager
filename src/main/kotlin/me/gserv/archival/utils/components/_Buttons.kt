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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import me.gserv.archival.Colors
import me.gserv.archival.Colors.IColors

// TODO: OK button, delete button, cancel button

@Composable
fun PrimaryButton(
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
	enabled: Boolean = true,
	interactionSource: MutableInteractionSource? = null,
	elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
	shape: Shape = ButtonDefaults.shape,
	border: BorderStroke? = null,
	contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
	content: @Composable (RowScope.(theme: IColors) -> Unit)
) {
	val theme = Colors.get()

	val textColor = if (enabled) {
		theme.Material.onPrimaryContainer
	} else {
		theme.Material.onSurface.copy(alpha = 0.38f)
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
			containerColor = theme.Material.primaryContainer,
			contentColor = theme.Material.onPrimaryContainer,
		),

		contentPadding = contentPadding
	) {
		ProvideTextStyle(TextStyle(color = textColor)) {
			Row(
				horizontalArrangement = Arrangement.spacedBy(10.dp),
				verticalAlignment = Alignment.CenterVertically,
			) {
				content(theme)
			}
		}
	}
}

@Composable
fun SecondaryButton(
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
	enabled: Boolean = true,
	interactionSource: MutableInteractionSource? = null,
	elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
	shape: Shape = ButtonDefaults.shape,
	border: BorderStroke? = null,
	contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
	content: @Composable (RowScope.(theme: IColors) -> Unit)
) {
	val theme = Colors.get()

	val textColor = if (enabled) {
		theme.Material.onSecondaryContainer
	} else {
		theme.Material.onSurface.copy(alpha = 0.38f)
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
			containerColor = theme.Material.secondaryContainer,
			contentColor = theme.Material.onSecondaryContainer,
		),

		contentPadding = contentPadding
	) {
		ProvideTextStyle(TextStyle(color = textColor)) {
			Row(
				horizontalArrangement = Arrangement.spacedBy(10.dp),
				verticalAlignment = Alignment.CenterVertically,
			) {
				content(theme)
			}
		}
	}
}

@Composable
fun TertiaryButton(
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
	enabled: Boolean = true,
	interactionSource: MutableInteractionSource? = null,
	elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
	shape: Shape = ButtonDefaults.shape,
	border: BorderStroke? = null,
	contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
	content: @Composable (RowScope.(theme: IColors) -> Unit)
) {
	val theme = Colors.get()

	val textColor = if (enabled) {
		theme.Material.onTertiaryContainer
	} else {
		theme.Material.onSurface.copy(alpha = 0.38f)
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
			containerColor = theme.Material.tertiaryContainer,
			contentColor = theme.Material.onTertiaryContainer,
		),

		contentPadding = contentPadding
	) {
		ProvideTextStyle(TextStyle(color = textColor)) {
			Row(
				horizontalArrangement = Arrangement.spacedBy(10.dp),
				verticalAlignment = Alignment.CenterVertically,
			) {
				content(theme)
			}
		}
	}
}


@Composable
fun SuccessButton(
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
	enabled: Boolean = true,
	interactionSource: MutableInteractionSource? = null,
	elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
	shape: Shape = ButtonDefaults.shape,
	border: BorderStroke? = null,
	contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
	content: @Composable (RowScope.(theme: IColors) -> Unit)
) {
	val theme = Colors.get()

	val textColor = if (enabled) {
		theme.MaterialOnSuccessContainer
	} else {
		theme.Material.onSurface.copy(alpha = 0.38f)
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
			containerColor = theme.MaterialSuccessContainer,
			contentColor = theme.MaterialOnSuccessContainer,
		),

		contentPadding = contentPadding
	) {
		ProvideTextStyle(TextStyle(color = textColor)) {
			Row(
				horizontalArrangement = Arrangement.spacedBy(10.dp),
				verticalAlignment = Alignment.CenterVertically,
			) {
				content(theme)
			}
		}
	}
}
