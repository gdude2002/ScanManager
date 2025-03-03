/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival

import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import io.github.kdroidfilter.platformtools.darkmodedetector.isSystemInDarkMode

object Colors {
	@Composable
	fun get(): IColors = if (isSystemInDarkMode()) {
		Dark
	} else {
		Light
	}

	@Composable
	fun getMaterial() = if (isSystemInDarkMode()) {
		darkColors().copy(primary = Color(0xFF552BB3), primaryVariant = Color(0xFFBB86FC))
	} else {
		lightColors().copy(primary = Color(0xFFBB86FC), primaryVariant = Color(0xFF552BB3))
	}

	@Composable
	fun Theme(content: @Composable ((theme: IColors) -> Unit)) {
		MaterialTheme(colors = getMaterial()) {
			ProvideTextStyle(value = TextStyle.Default.copy(color = get().Text)) {
				content(get())
			}
		}
	}

	object Dark : IColors {
		override val Danger: Color = Color(0xFFCF6679)
		override val Text = Color.White

		override val SectionBackground: Color = Color(0xFF222222)
		override val WindowBackground: Color = Color.Black

		override val RowHovered: Color = Color(0xFF552BB3)
		override val RowEven: Color = SectionBackground
		override val RowOdd: Color = WindowBackground
	}

	object Light : IColors {
		override val Danger: Color = Color(0xFFEE0000)
		override val Text = Color.Black

		override val SectionBackground: Color = Color(0xFFEEEEEE)
		override val WindowBackground: Color = Color.White

		override val RowHovered: Color = Color(0xFFBB86FC)
		override val RowEven: Color = SectionBackground
		override val RowOdd: Color = WindowBackground
	}

	interface IColors {
		val Danger: Color
		val Text: Color

		val SectionBackground: Color
		val WindowBackground: Color

		val RowHovered: Color
		val RowEven: Color
		val RowOdd: Color
	}
}
