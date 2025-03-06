/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

@file:OptIn(ExperimentalMaterialApi::class)

package me.gserv.archival

import androidx.compose.material.*
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
			ProvideTextStyle(TextStyle(color = get().Text)) {
				content(get())
			}
		}
	}

	object Dark : IColors() {
		override val Primary: Color = Color(0xFF552BB3)
		override val PrimaryVariant: Color = Color(0xFFBB86FC)
		override val Secondary: Color = Color(0xFF018786)
		override val SecondaryVariant: Color = Color(0xFF03DAC6)

		override val DangerBackground: Color = Color(0xFFEE0000)
		override val DangerIcon: Color = Color(0xFFFC6E6E)
		override val DangerForeground = Color.White

		override val SuccessBackground: Color = Color(0xFF2CEE00)
		override val SuccessIcon: Color = Color(0xFF81FC6E)
		override val SuccessForeground: Color = Color.Black

		override val Text = Color.White

		override val TooltipBackground = Color(0xFF5D5D00)
		override val TooltipText = Light.TooltipBackground

		override val SectionBackground: Color = Color(0xFF222222)
		override val WindowBackground: Color = Color.Black

		override val RowHovered: Color = Color(0xff3f3a42)
	}

	object Light : IColors() {
		override val Primary: Color = Color(0xFFBB86FC)
		override val PrimaryVariant: Color = Color(0xFF552BB3)
		override val Secondary: Color = Color(0xFF03DAC6)
		override val SecondaryVariant: Color = Color(0xFF018786)

		override val DangerBackground: Color = Color(0xFFFC6E6E)
		override val DangerIcon: Color = Color(0xFFEE0000)
		override val DangerForeground = Color.Black

		override val SuccessBackground: Color = Color(0xFF81FC6E)
		override val SuccessIcon: Color = Color(0xFF2CEE00)
		override val SuccessForeground: Color = Color.Black

		override val Text = Color.Black

		override val TooltipBackground = Color(255, 255, 210)
		override val TooltipText = Dark.TooltipBackground

		override val SectionBackground: Color = Color(0xFFEEEEEE)
		override val WindowBackground: Color = Color.White

		override val RowHovered: Color = Color(0xffecdfee)
	}

	abstract class IColors {
		abstract val Primary: Color
		abstract val PrimaryVariant: Color
		abstract val Secondary: Color
		abstract val SecondaryVariant: Color

		abstract val DangerBackground: Color
		abstract val DangerForeground: Color
		abstract val DangerIcon: Color

		abstract val SuccessBackground: Color
		abstract val SuccessForeground: Color
		abstract val SuccessIcon: Color

		abstract val Text: Color

		abstract val TooltipBackground: Color
		abstract val TooltipText: Color

		abstract val SectionBackground: Color
		abstract val WindowBackground: Color

		abstract val RowHovered: Color

		@Composable
		fun defaultChipColors(): ChipColors =
			ChipDefaults.chipColors(
				backgroundColor = WindowBackground,
				contentColor = Text,
				leadingIconContentColor = Text.copy(alpha = 0.5f)
			)

		@Composable
		fun primaryChipColors(): ChipColors =
			ChipDefaults.chipColors(
				backgroundColor = Primary,
				contentColor = Text,
				leadingIconContentColor = Text.copy(alpha = 0.5f)
			)

		@Composable
		fun secondaryChipColors(): ChipColors =
			ChipDefaults.chipColors(
				backgroundColor = Secondary,
				contentColor = Text,
				leadingIconContentColor = Text.copy(alpha = 0.5f)
			)

		@Composable
		fun successChipColors(): ChipColors =
			ChipDefaults.chipColors(
				backgroundColor = SuccessBackground,
				contentColor = SuccessForeground,
				leadingIconContentColor = Text.copy(alpha = 0.5f)
			)

		@Composable
		fun dangerChipColors(): ChipColors =
			ChipDefaults.chipColors(
				backgroundColor = DangerBackground,
				contentColor = DangerForeground,
				leadingIconContentColor = Text.copy(alpha = 0.5f)
			)
	}
}
