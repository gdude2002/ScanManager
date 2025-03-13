/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */


package me.gserv.archival

import androidx.compose.material3.*
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
	fun Theme(content: @Composable ((theme: IColors) -> Unit)) {
		val theme = get()

		MaterialTheme(colorScheme = theme.Material) {
			ProvideTextStyle(TextStyle(color = theme.Text)) {
				content(theme)
			}
		}
	}

	object Dark : IColors() {
		override val Material = darkColorScheme()

		override val MaterialSuccess: Color = ColorTokens.Success80
		override val MaterialOnSuccess: Color = ColorTokens.Success20
		override val MaterialSuccessContainer: Color = ColorTokens.Success30
		override val MaterialOnSuccessContainer: Color = ColorTokens.Success90

		override val Text = Color.White

		override val TooltipBackground = Color(0xFF5D5D00)
		override val TooltipText = Light.TooltipBackground

		override val SectionBackground: Color = Material.surfaceBright
		override val WindowBackground: Color = Material.surface

		override val RowHovered: Color = Material.surfaceTint.copy(0.25f)
	}

	object Light : IColors() {
		override val Material = lightColorScheme()

		override val MaterialSuccess: Color = ColorTokens.Success40
		override val MaterialOnSuccess: Color = ColorTokens.Success100
		override val MaterialSuccessContainer: Color = ColorTokens.Success90
		override val MaterialOnSuccessContainer: Color = ColorTokens.Success10

		override val Text = Color.Black

		override val TooltipBackground = Color(255, 255, 210)
		override val TooltipText = Dark.TooltipBackground

		override val SectionBackground: Color = Material.surfaceDim
		override val WindowBackground: Color = Material.surface

		override val RowHovered: Color = Material.surfaceTint.copy(0.25f)
	}

	abstract class IColors {
		abstract val Material: ColorScheme

		abstract val MaterialSuccess: Color
		abstract val MaterialOnSuccess: Color
		abstract val MaterialSuccessContainer: Color
		abstract val MaterialOnSuccessContainer: Color

		abstract val Text: Color

		abstract val TooltipBackground: Color
		abstract val TooltipText: Color

		abstract val SectionBackground: Color
		abstract val WindowBackground: Color

		abstract val RowHovered: Color

		@Composable
		fun defaultChipColors(): ChipColors =
			SuggestionChipDefaults.suggestionChipColors(
				containerColor = WindowBackground,
				labelColor = Text,
				iconContentColor = Text,

				disabledContainerColor = WindowBackground.copy(alpha = 0.5f),
				disabledLabelColor = Text.copy(alpha = 0.5f),
				disabledIconContentColor = Text.copy(alpha = 0.25f),
			)

		@Composable
		fun primaryChipColors(): ChipColors =
			SuggestionChipDefaults.suggestionChipColors(
				containerColor = Material.primaryContainer,
				labelColor = Material.onPrimaryContainer,
				iconContentColor = Material.onPrimaryContainer,

				disabledContainerColor = Material.primaryContainer.copy(alpha = 0.5f),
				disabledLabelColor = Material.onPrimaryContainer.copy(alpha = 0.5f),
				disabledIconContentColor = Material.onPrimaryContainer.copy(alpha = 0.25f),
			)

		@Composable
		fun secondaryChipColors(): ChipColors =
			SuggestionChipDefaults.suggestionChipColors(
				containerColor = Material.secondaryContainer,
				labelColor = Material.onSecondaryContainer,
				iconContentColor = Material.onSecondaryContainer,

				disabledContainerColor = Material.secondaryContainer.copy(alpha = 0.5f),
				disabledLabelColor = Material.onSecondaryContainer.copy(alpha = 0.5f),
				disabledIconContentColor = Material.onSecondaryContainer.copy(alpha = 0.25f),
			)

		@Composable
		fun tertiaryChipColors(): ChipColors =
			SuggestionChipDefaults.suggestionChipColors(
				containerColor = Material.tertiaryContainer,
				labelColor = Material.onTertiaryContainer,
				iconContentColor = Material.onTertiaryContainer,

				disabledContainerColor = Material.tertiaryContainer.copy(alpha = 0.5f),
				disabledLabelColor = Material.onTertiaryContainer.copy(alpha = 0.5f),
				disabledIconContentColor = Material.onTertiaryContainer.copy(alpha = 0.25f),
			)

		@Composable
		fun successChipColors(): ChipColors =
			SuggestionChipDefaults.suggestionChipColors(
				containerColor = MaterialSuccessContainer,
				labelColor = MaterialOnSuccessContainer,
				iconContentColor = MaterialOnSuccessContainer,

				disabledContainerColor = MaterialSuccessContainer.copy(alpha = 0.5f),
				disabledLabelColor = MaterialOnSuccessContainer.copy(alpha = 0.5f),
				disabledIconContentColor = MaterialOnSuccessContainer.copy(alpha = 0.25f),
			)
	}

	private object ColorTokens {
		val Success0 = Color(red = 0, green = 0, blue = 0)
		val Success10 = Color(red = 17, green = 49, blue = 11)
		val Success20 = Color(red = 37, green = 73, blue = 16)
		val Success30 = Color(red = 59, green = 99, blue = 24)
		val Success40 = Color(red = 82, green = 125, blue = 30)
		val Success50 = Color(red = 105, green = 152, blue = 46)
		val Success60 = Color(red = 131, green = 181, blue = 98)
		val Success70 = Color(red = 157, green = 210, blue = 142)
		val Success80 = Color(red = 184, green = 239, blue = 181)
		val Success90 = Color(red = 216, green = 255, blue = 220)
		val Success95 = Color(red = 236, green = 255, blue = 238)
		val Success99 = Color(red = 251, green = 255, blue = 249)
		val Success100 = Color(red = 255, green = 255, blue = 255)
	}
}
