/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.types

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.FrameWindowScope

open class VisibilityTogglingWindow(initiallyVisible: Boolean = true) {
	var isVisible: Boolean by mutableStateOf(initiallyVisible)

	lateinit var scope: FrameWindowScope
	protected val isScopeSet = ::scope.isInitialized

	open fun show() {
		scope.window.isVisible = true
		scope.window.requestFocus()

		isVisible = true
	}

	open fun hide() {
		isVisible = false

		scope.window.isVisible = false
	}
}
