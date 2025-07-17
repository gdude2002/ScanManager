/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.m3.components.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.NavigationDrawerItemColors
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
fun DrawerItem(
	label: (@Composable () -> Unit)? = null,
	showLabel: Boolean = false,
	selected: Boolean,
	onClick: () -> Unit,
	modifier: Modifier = Modifier,
	icon: (@Composable () -> Unit)? = null,
	badge: (@Composable () -> Unit)? = null,
	shape: Shape = CircleShape,
	colors: NavigationDrawerItemColors = NavigationDrawerItemDefaults.colors(),
	interactionSource: MutableInteractionSource? = null
) {
	Surface(
		selected = selected,
		onClick = onClick,
		modifier =
			modifier
				.semantics { role = Role.Tab }
				.heightIn(min = 56.0.dp)
				.fillMaxWidth(),
		shape = shape,
		color = colors.containerColor(selected).value,
		interactionSource = interactionSource,
	) {
		val animatedWidth by animateDpAsState(
			if (label != null && showLabel) {
				24.dp
			} else {
				16.dp
			},
			label = "width",
		)

		Row(
			Modifier
				.padding(
					start = 16.dp,
					end = animatedWidth
				),
			verticalAlignment = Alignment.CenterVertically
		) {
			if (icon != null) {
				val iconColor = colors.iconColor(selected).value

				Box(Modifier.requiredWidth(24.dp).wrapContentWidth(unbounded = true)) {
					CompositionLocalProvider(LocalContentColor provides iconColor, content = icon)
				}

				Spacer(Modifier.width(16.dp))
			}

			if (label != null) {
				if (showLabel) {
					Box(Modifier.weight(1f)) {
						val labelColor = colors.textColor(selected).value
						CompositionLocalProvider(LocalContentColor provides labelColor, content = label)
					}
				}
			}

			if (badge != null) {
				Spacer(Modifier.width(12.dp))
				val badgeColor = colors.badgeColor(selected).value
				CompositionLocalProvider(LocalContentColor provides badgeColor, content = badge)
			}
		}
	}
}
