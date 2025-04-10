/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.utils.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import me.gserv.archival.Colors
import me.gserv.archival.utils.SelectableEnum

@Composable
fun <T : SelectableEnum> EnumDropdown(
	options: List<T>,
	default: T,
	selected: T,

	buttonPrefix: String = "",
	itemPrefix: String = "",

	wrapText: Boolean = false,
	boxModifier: Modifier = Modifier,
	buttonModifier: Modifier = Modifier,
	itemModifier: Modifier = Modifier,
	menuModifier: Modifier = Modifier,

	callback: (T) -> Unit,
) {
	var selected = selected
	var expanded by mutableStateOf(false)
	val finalOptions = options.toSet() - default

	Colors.Theme { colors ->
		Box(boxModifier) {
			PrimaryOutlinedButton(
				onClick = { expanded = !expanded },
				modifier = buttonModifier
			) {
				Row(
					horizontalArrangement = Arrangement.spacedBy(10.dp),
					verticalAlignment = Alignment.CenterVertically,
				) {
					Text(
						buttonPrefix + selected.readableName,
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
						selected = default
						expanded = false

						callback(default)
					},

					leadingIcon = {
						Icon(
							default.icon,
							""
						)
					},

					colors = if (default == selected) {
						colors.successMenuItemColors()
					} else {
						colors.defaultMenuItemColors()
					},

					modifier = itemModifier,

					text = {
						Text(
							itemPrefix + default.readableName,

							color = if (default == selected) {
								colors.MaterialSuccess
							} else {
								colors.Text
							}
						)
					}
				)

				HorizontalDivider()

				finalOptions.forEach { option ->
					DropdownMenuItem(
						onClick = {
							selected = option
							expanded = false

							callback(option)
						},

						leadingIcon = {
							Icon(
								option.icon,
								""
							)
						},

						colors = if (option == selected) {
							colors.successMenuItemColors()
						} else {
							colors.defaultMenuItemColors()
						},

						modifier = itemModifier,

						text = {
							Text(
								itemPrefix + option.readableName,

								color = if (option == selected) {
									colors.MaterialSuccess
								} else {
									colors.Text
								}
							)
						}
					)
				}
			}
		}
	}
}

@Composable
inline fun <reified T> EnumDropdown(
	default: T,
	selected: T,

	buttonPrefix: String = "",
	itemPrefix: String = "",

	wrapText: Boolean = false,
	boxModifier: Modifier = Modifier,
	buttonModifier: Modifier = Modifier,
	itemModifier: Modifier = Modifier,
	menuModifier: Modifier = Modifier,

	noinline callback: (T) -> Unit,
) where T : Enum<T>, T : SelectableEnum {
	EnumDropdown(
		options = enumValues<T>().toList(),

		default = default,
		selected = selected,

		buttonPrefix = buttonPrefix,
		itemPrefix = itemPrefix,

		wrapText = wrapText,
		boxModifier = boxModifier,
		buttonModifier = buttonModifier,
		itemModifier = itemModifier,
		menuModifier = menuModifier,

		callback = callback,
	)
}
