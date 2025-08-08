/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.m3.config

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.graphics.vector.ImageVector
import me.gserv.archival.utils.SelectableEnum

enum class SidebarMode(override val readableName: String, override val icon: ImageVector) : SelectableEnum {
	TOGGLE("Toggle", Icons.Default.Refresh),

	OPEN("Always Open", Icons.Default.Visibility),
	CLOSED("Always Closed", Icons.Default.VisibilityOff),
}
