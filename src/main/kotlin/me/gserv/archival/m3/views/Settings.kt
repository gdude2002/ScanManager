/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.m3.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import me.gserv.archival.m3.components.MainHeader
import me.gserv.archival.m3.components.SubHeader
import me.gserv.archival.m3.components.layout.ContainerColumn
import me.gserv.archival.m3.components.layout.ResponsiveRow
import me.gserv.archival.m3.components.selectors.FontSelector
import me.gserv.archival.m3.config.AppSettings
import me.gserv.archival.m3.config.SidebarMode
import me.gserv.archival.m3.config.Theme
import me.gserv.archival.m3.resources.fonts.Fonts
import me.gserv.archival.utils.components.EnumDropdown
import org.jetbrains.exposed.sql.Column

@Composable
fun settingsView() {
	/* Necessary settings
	 *   Fonts (Support system fonts too).
	 *   Themes.
	 *   Always expand the sidebar.
	 *
	 *   Data dir:
	 *     Image formats (saved files only).
	 *     Filename patterns.
	 *     Storage tree.
	 */

	Column {
		Row {
			MainHeader(
				"Settings",
				textAlign = TextAlign.Center,
				modifier = Modifier
					.weight(1f)
			)
		}

		Spacer(modifier = Modifier.height(20.dp))

		ResponsiveRow { isWide ->
			val colModifier = if (isWide) {
				Modifier.weight(1f)
			} else {
				Modifier.padding(bottom = 8.dp)
			}

			ContainerColumn(colModifier) {
				Row {
					SubHeader(
						"General",
						textAlign = TextAlign.Center,
						modifier = Modifier
							.weight(1f)
					)
				}

				Spacer(modifier = Modifier.height(10.dp))

				Row {
					Text(
						"Sidebar",

						Modifier
							.align(Alignment.CenterVertically)
							.width(100.dp)
					)

					EnumDropdown(
						SidebarMode.TOGGLE,
						AppSettings.alwaysExpandSidebar
					) {
						AppSettings.alwaysExpandSidebar = it
					}
				}

				Row {
					Text(
						"Theme",

						Modifier
							.align(Alignment.CenterVertically)
							.width(100.dp)
					)

					EnumDropdown(
						Theme.AUTOMATIC,
						AppSettings.theme
					) {
						AppSettings.theme = it
					}
				}
			}

			if (isWide) {
				Spacer(Modifier.width(8.dp))
			}

			ContainerColumn(colModifier) {
				Row {
					SubHeader(
						"Fonts",
						textAlign = TextAlign.Center,
						modifier = Modifier
							.weight(1f)
					)
				}

				Spacer(modifier = Modifier.height(10.dp))

				Row {
					Text(
						"Headers",

						Modifier
							.align(Alignment.CenterVertically)
							.width(100.dp)
					)

					FontSelector(
						Fonts.Poppins,
						Fonts.get(AppSettings.headerFont) ?: Fonts.Poppins,
					) {
						AppSettings.headerFont = it.name
					}
				}

				Row {
					Text(
						"Text",

						Modifier
							.align(Alignment.CenterVertically)
							.width(100.dp)
					)

					FontSelector(
						Fonts.Inter,
						Fonts.get(AppSettings.textFont) ?: Fonts.Inter,
					) {
						AppSettings.textFont = it.name
					}
				}
			}
		}
	}
}
