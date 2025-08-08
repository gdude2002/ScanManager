/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.m3.config

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.ui.graphics.vector.ImageVector
import me.gserv.archival.utils.SelectableEnum

enum class Theme(override val readableName: String, override val icon: ImageVector) : SelectableEnum {
	AUTOMATIC("Automatic", Icons.Default.Refresh),

	DARK("Dark", Icons.Default.DarkMode),
	LIGHT("Light", Icons.Default.LightMode),
}
