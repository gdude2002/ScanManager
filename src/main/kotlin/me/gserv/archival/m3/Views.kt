/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.m3

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import me.gserv.archival.m3.components.navigation.NavigationPosition
import me.gserv.archival.m3.views.homeView

object Views {
	object Home : View() {
		override val icon: ImageVector = Icons.Outlined.Home
		override val label: String = "Home"
		override val ordinal: Int = 0
		override val render: @Composable (() -> Unit) = { homeView() }
	}

	object Binders : View() {
		override val icon: ImageVector = Icons.Outlined.Book
		override val label: String = "Binders"
		override val ordinal: Int = 1
		override val render: @Composable (() -> Unit) = { }

		override val needsDataDir: Boolean = true
	}

	object Sets : View() {
		override val icon: ImageVector = Icons.Outlined.Image
		override val label: String = "Sets"
		override val ordinal: Int = 2
		override val render: @Composable (() -> Unit) = { }

		override val needsDataDir: Boolean = true
	}

	object Settings : View() {
		override val icon: ImageVector = Icons.Outlined.Settings
		override val label: String = "Settings"
		override val ordinal: Int = 999
		override val render: @Composable (() -> Unit) = { }

		override val position: NavigationPosition = NavigationPosition.END
	}

	val all by lazy {
		View::class.sealedSubclasses
			.map { it.objectInstance as View }
			.sortedBy { it.ordinal }
	}
}

sealed class View {
	abstract val icon: ImageVector
	abstract val label: String
	abstract val ordinal: Int
	abstract val render: @Composable (() -> Unit)

	open val modifier: Modifier = Modifier
	open val position: NavigationPosition = NavigationPosition.MIDDLE
	open val needsDataDir: Boolean = false
}
