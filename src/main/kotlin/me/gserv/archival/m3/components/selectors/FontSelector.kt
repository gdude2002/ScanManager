/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.m3.components.selectors

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import me.gserv.archival.m3.resources.fonts.Fonts

@Composable
fun FontSelector(
	default: Fonts,
	selected: Fonts,

	buttonPrefix: String = "",
	itemPrefix: String = "",

	wrapText: Boolean = false,
	boxModifier: Modifier = Modifier,
	buttonModifier: Modifier = Modifier,
	itemModifier: Modifier = Modifier,
	menuModifier: Modifier = Modifier,

	filter: (Fonts) -> Boolean = { true },
	callback: (Fonts) -> Unit,
) {
	var expanded by remember { mutableStateOf(false) }

	Box(boxModifier) {
		OutlinedButton(
			onClick = { expanded = !expanded },
			modifier = buttonModifier
		) {
			Row(
				horizontalArrangement = Arrangement.spacedBy(10.dp),
				verticalAlignment = Alignment.CenterVertically,
			) {
				Text(
					buttonPrefix + selected.name,
					textAlign = TextAlign.Start,
					softWrap = wrapText
				)

				Spacer(Modifier.weight(1f))

				if (expanded) {
					Icon(Icons.Rounded.KeyboardArrowUp, "")
				} else {
					Icon(Icons.Rounded.KeyboardArrowDown, "")
				}
			}
		}

		DropdownMenu(
			expanded,
			onDismissRequest = { expanded = false },
			modifier = menuModifier,
		) {
			DropdownMenuItem(
				onClick = {
					expanded = false

					callback(default)
				},

				leadingIcon = {
					if (default == selected) {
						Icon(
							Icons.Default.Check,
							""
						)
					} else {
						Icon(
							Icons.AutoMirrored.Filled.Undo,
							""
						)
					}
				},

				colors = if (default == selected) {
					MenuDefaults.itemColors(
						textColor = MaterialTheme.colorScheme.primary,
						leadingIconColor = MaterialTheme.colorScheme.primary,
					)
				} else {
					MenuDefaults.itemColors()
				},

				modifier = itemModifier,

				text = {
					Text(
						itemPrefix + default.name,

						color = if (default == selected) {
							MaterialTheme.colorScheme.primary
						} else {
							Color.Unspecified
						}
					)
				}
			)

			@Composable
			fun addItems(items: List<Fonts>) {
				HorizontalDivider()

				items.forEach { option ->
					DropdownMenuItem(
						onClick = {
							expanded = false

							callback(option)
						},

						leadingIcon = {
							if (option == selected) {
								Icon(
									Icons.Default.Check,
									""
								)
							}
						},

						colors = if (option == selected) {
							MenuDefaults.itemColors(
								textColor = MaterialTheme.colorScheme.primary,
								leadingIconColor = MaterialTheme.colorScheme.primary,
							)
						} else {
							MenuDefaults.itemColors()
						},

						modifier = itemModifier,

						text = {
							Text(
								itemPrefix + option.name,

								color = if (option == selected) {
									MaterialTheme.colorScheme.primary
								} else {
									Color.Unspecified
								}
							)
						}
					)
				}
			}

			addItems((Fonts.all - default).filter(filter))
			addItems((Fonts.systemFonts - default).filter(filter))
		}
	}
}
