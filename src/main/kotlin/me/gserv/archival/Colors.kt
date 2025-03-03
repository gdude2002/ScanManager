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
		darkColors().copy(
			primary = Dark.Primary, primaryVariant = Dark.PrimaryVariant,
			secondary = Dark.Secondary, secondaryVariant = Dark.SecondaryVariant,
			background = Dark.SectionBackground, surface = Dark.WindowBackground,
			error = Dark.DangerBackground,

			onPrimary = Dark.PrimaryVariant, onSecondary = Dark.SecondaryVariant,
			onBackground = Dark.Text, onSurface = Dark.Text,
			onError = Dark.DangerForeground,
		)
	} else {
		lightColors().copy(
			primary = Light.Primary, primaryVariant = Light.PrimaryVariant,
			secondary = Light.Secondary, secondaryVariant = Light.SecondaryVariant,
			background = Light.SectionBackground, surface = Light.WindowBackground,
			error = Light.DangerBackground,

			onPrimary = Light.PrimaryVariant, onSecondary = Light.SecondaryVariant,
			onBackground = Light.Text, onSurface = Light.Text,
			onError = Light.DangerForeground,
		)
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
		override val Primary: Color = Color(0xFF552BB3)
		override val PrimaryVariant: Color = Color(0xFFBB86FC)
		override val Secondary: Color = Color(0xFF018786)
		override val SecondaryVariant: Color = Color(0xFF03DAC6)

		override val DangerBackground: Color = Color(0xFFEE0000)
		override val DangerForeground = Color.White

		override val Text = Color.White

		override val TooltipBackground = Color(0xFF5D5D00)
		override val TooltipText = Light.TooltipBackground

		override val SectionBackground: Color = Color(0xFF222222)
		override val WindowBackground: Color = Color.Black

		override val RowHovered: Color = Color(0xFF552BB3)
		override val RowEven: Color = SectionBackground
		override val RowOdd: Color = WindowBackground
	}

	object Light : IColors {
		override val Primary: Color = Color(0xFFBB86FC)
		override val PrimaryVariant: Color = Color(0xFF552BB3)
		override val Secondary: Color = Color(0xFF03DAC6)
		override val SecondaryVariant: Color = Color(0xFF018786)

		override val DangerBackground: Color = Color(0xFFFC6E6E)
		override val DangerForeground = Color.Black

		override val Text = Color.Black

		override val TooltipBackground = Color(255, 255, 210)
		override val TooltipText = Dark.TooltipBackground

		override val SectionBackground: Color = Color(0xFFEEEEEE)
		override val WindowBackground: Color = Color.White

		override val RowHovered: Color = Color(0xFFBB86FC)
		override val RowEven: Color = SectionBackground
		override val RowOdd: Color = WindowBackground
	}

	interface IColors {
		val Primary: Color
		val PrimaryVariant: Color
		val Secondary: Color
		val SecondaryVariant: Color

		val DangerBackground: Color
		val DangerForeground: Color

		val Text: Color

		val TooltipBackground: Color
		val TooltipText: Color

		val SectionBackground: Color
		val WindowBackground: Color

		val RowHovered: Color
		val RowEven: Color
		val RowOdd: Color
	}
}
