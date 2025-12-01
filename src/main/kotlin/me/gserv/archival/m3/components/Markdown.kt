/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.m3.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.em
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.model.DefaultMarkdownColors
import com.mikepenz.markdown.model.DefaultMarkdownTypography
import me.gserv.archival.m3.config.AppSettings
import me.gserv.archival.m3.resources.fonts.Fonts

@Composable
public fun MDText(text: String) {
	val headerFont = Fonts.get(AppSettings.headerFont)?.font()
		?: Fonts.Poppins.font()

	val textFont = Fonts.get(AppSettings.textFont)
		?: Fonts.Inter

	Markdown(
		content = text.trimIndent(),
		colors = DefaultMarkdownColors(
			text = MaterialTheme.colorScheme.onBackground,
			codeBackground = MaterialTheme.colorScheme.secondaryContainer,
			inlineCodeBackground = MaterialTheme.colorScheme.secondaryContainer,
			dividerColor = MaterialTheme.colorScheme.onBackground,
			tableBackground = MaterialTheme.colorScheme.secondaryContainer,
		),

		typography = DefaultMarkdownTypography(
			h1 = TextStyle(fontFamily = headerFont, fontSize = 1.75.em, fontWeight = FontWeight.SemiBold),
			h2 = TextStyle(fontFamily = headerFont, fontSize = 1.60.em, fontWeight = FontWeight.SemiBold),
			h3 = TextStyle(fontFamily = headerFont, fontSize = 1.45.em, fontWeight = FontWeight.SemiBold),
			h4 = TextStyle(fontFamily = headerFont, fontSize = 1.30.em, fontWeight = FontWeight.SemiBold),
			h5 = TextStyle(fontFamily = headerFont, fontSize = 1.15.em, fontWeight = FontWeight.SemiBold),
			h6 = TextStyle(fontFamily = headerFont, fontSize = 1.em, fontWeight = FontWeight.SemiBold),

			text = textFont.typography().bodyLarge,
			code = textFont.typography().bodyLarge,
			inlineCode = textFont.typography().bodyLarge,
			quote = textFont.typography().bodyLarge,
			paragraph = textFont.typography().bodyLarge,
			ordered = textFont.typography().bodyLarge,
			bullet = textFont.typography().bodyLarge,
			list = textFont.typography().bodyLarge,
			textLink = TextLinkStyles(SpanStyle(color = MaterialTheme.colorScheme.primary, textDecoration = TextDecoration.Underline)),
			table = textFont.typography().bodyLarge,
		)
	)
}
