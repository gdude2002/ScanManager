/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.m3.resources.fonts

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import me.gserv.archival.scanmanager.generated.resources.*
import org.jetbrains.compose.resources.Font

sealed class Fonts(val name: String) {
	@Composable
	abstract fun font(): FontFamily

	@Composable
	open fun typography(): Typography {
		return fontToTypography(font())
	}

	object APhont : Fonts("APhont") {
		@Composable
		override fun font() = FontFamily(
			Font(Res.font.APHont_Bold, FontWeight.Bold),
			Font(Res.font.APHont_BoldItalic, FontWeight.Bold, FontStyle.Italic),

			Font(Res.font.APHont_Regular, FontWeight.Normal),
			Font(Res.font.APHont_Italic, FontWeight.Normal, FontStyle.Italic),
		)
	}

	object AtkinsonHyperlegible : Fonts("Atkinson Hyperlegible") {
		@Composable
		override fun font() = FontFamily(
			Font(Res.font.AtkinsonHyperlegible_Bold, FontWeight.Bold),
			Font(Res.font.AtkinsonHyperlegible_BoldItalic, FontWeight.Bold, FontStyle.Italic),

			Font(Res.font.AtkinsonHyperlegible_Regular, FontWeight.Normal),
			Font(Res.font.AtkinsonHyperlegible_Italic, FontWeight.Normal, FontStyle.Italic),
		)
	}

	object Inter : Fonts("Inter") {
		@Composable
		override fun font() = FontFamily(
			Font(Res.font.Inter_Black, FontWeight.Black),
			Font(Res.font.Inter_Bold, FontWeight.Bold),
			Font(Res.font.Inter_ExtraBold, FontWeight.ExtraBold),
			Font(Res.font.Inter_ExtraLight, FontWeight.ExtraLight),
			Font(Res.font.Inter_Light, FontWeight.Light),
			Font(Res.font.Inter_Medium, FontWeight.Medium),
			Font(Res.font.Inter_Regular, FontWeight.Normal),
			Font(Res.font.Inter_SemiBold, FontWeight.SemiBold),
			Font(Res.font.Inter_Thin, FontWeight.Thin),

			Font(Res.font.Inter_BlackItalic, FontWeight.Black, FontStyle.Italic),
			Font(Res.font.Inter_BoldItalic, FontWeight.Bold, FontStyle.Italic),
			Font(Res.font.Inter_ExtraBoldItalic, FontWeight.ExtraBold, FontStyle.Italic),
			Font(Res.font.Inter_ExtraLightItalic, FontWeight.ExtraLight, FontStyle.Italic),
			Font(Res.font.Inter_LightItalic, FontWeight.Light, FontStyle.Italic),
			Font(Res.font.Inter_MediumItalic, FontWeight.Medium, FontStyle.Italic),
			Font(Res.font.Inter_Italic, FontWeight.Normal, FontStyle.Italic),
			Font(Res.font.Inter_SemiBoldItalic, FontWeight.SemiBold, FontStyle.Italic),
			Font(Res.font.Inter_ThinItalic, FontWeight.Thin, FontStyle.Italic),
		)
	}

	object Luicole : Fonts("Luicole") {
		@Composable
		override fun font() = FontFamily(
			Font(Res.font.Luciole_Bold, FontWeight.Bold),
			Font(Res.font.Luciole_Bold_Italic, FontWeight.Bold, FontStyle.Italic),

			Font(Res.font.Luciole_Regular, FontWeight.Normal),
			Font(Res.font.Luciole_Regular_Italic, FontWeight.Normal, FontStyle.Italic),
		)
	}

	object Poppins : Fonts("Poppins") {
		@Composable
		override fun font() = FontFamily(
			Font(Res.font.Poppins_Black, FontWeight.Black),
			Font(Res.font.Poppins_Bold, FontWeight.Bold),
			Font(Res.font.Poppins_ExtraBold, FontWeight.ExtraBold),
			Font(Res.font.Poppins_ExtraLight, FontWeight.ExtraLight),
			Font(Res.font.Poppins_Light, FontWeight.Light),
			Font(Res.font.Poppins_Medium, FontWeight.Medium),
			Font(Res.font.Poppins_Regular, FontWeight.Normal),
			Font(Res.font.Poppins_SemiBold, FontWeight.SemiBold),
			Font(Res.font.Poppins_Thin, FontWeight.Thin),
		)
	}

	object OpenDyslexic : Fonts("Open Dyslexic") {
		@Composable
		override fun font() = FontFamily(
			Font(Res.font.OpenDyslexic_Bold, FontWeight.Bold),
			Font(Res.font.OpenDyslexic_Bold_Italic, FontWeight.Bold, FontStyle.Italic),

			Font(Res.font.OpenDyslexic_Regular, FontWeight.Normal),
			Font(Res.font.OpenDyslexic_Italic, FontWeight.Normal, FontStyle.Italic),
		)
	}

	@Composable
	fun fontToTypography(font: FontFamily): Typography {
		return with(MaterialTheme.typography) {
			copy(
				displayLarge = displayLarge.copy(fontFamily = font, fontWeight = FontWeight.Bold),
				displayMedium = displayMedium.copy(fontFamily = font, fontWeight = FontWeight.Bold),
				displaySmall = displaySmall.copy(fontFamily = font, fontWeight = FontWeight.Bold),

				headlineLarge = headlineLarge.copy(fontFamily = font, fontWeight = FontWeight.Bold),
				headlineMedium = headlineMedium.copy(fontFamily = font, fontWeight = FontWeight.Bold),
				headlineSmall = headlineSmall.copy(fontFamily = font, fontWeight = FontWeight.Bold),

				titleLarge = titleLarge.copy(fontFamily = font, fontWeight = FontWeight.Bold),
				titleMedium = titleMedium.copy(fontFamily = font, fontWeight = FontWeight.Bold),
				titleSmall = titleSmall.copy(fontFamily = font, fontWeight = FontWeight.Bold),

				labelLarge = labelLarge.copy(fontFamily = font, fontWeight = FontWeight.Normal),
				labelMedium = labelMedium.copy(fontFamily = font, fontWeight = FontWeight.Normal),
				labelSmall = labelSmall.copy(fontFamily = font, fontWeight = FontWeight.Normal),

				bodyLarge = bodyLarge.copy(fontFamily = font, fontWeight = FontWeight.Normal),
				bodyMedium = bodyMedium.copy(fontFamily = font, fontWeight = FontWeight.Normal),
				bodySmall = bodySmall.copy(fontFamily = font, fontWeight = FontWeight.Normal),
			)
		}
	}

	companion object {
		val all by lazy {
			Fonts::class.sealedSubclasses
				.map { it.objectInstance as Fonts }
				.sortedBy { it.name.lowercase() }
		}

		fun get(name: String) =
			all.firstOrNull { it.name.equals(name, true) }
	}
}
