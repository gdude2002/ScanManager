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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import me.gserv.archival.m3.config.AppSettings
import me.gserv.archival.m3.config.SidebarMode
import me.gserv.archival.m3.config.Theme
import me.gserv.archival.utils.components.EnumDropdown

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
			Text("Settings!")
		}

		EnumDropdown(
			SidebarMode.TOGGLE,
			AppSettings.alwaysExpandSidebar
		) {
			AppSettings.alwaysExpandSidebar = it
		}

		EnumDropdown(
			Theme.AUTOMATIC,
			AppSettings.theme
		) {
			AppSettings.theme = it
		}
	}


}
