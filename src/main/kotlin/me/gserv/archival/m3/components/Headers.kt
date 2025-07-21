/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.m3.components

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em
import me.gserv.archival.m3.resources.fonts.Fonts

@Composable
fun MainHeader(
	text: String,
	modifier: Modifier = Modifier,
	color: Color = Color.Unspecified,
	fontStyle: FontStyle? = null,
	letterSpacing: TextUnit = TextUnit.Unspecified,
	textDecoration: TextDecoration? = null,
	textAlign: TextAlign? = null,
	lineHeight: TextUnit = TextUnit.Unspecified,
	overflow: TextOverflow = TextOverflow.Ellipsis,
	softWrap: Boolean = false,
	maxLines: Int = 1,
	minLines: Int = 1,
	onTextLayout: ((TextLayoutResult) -> Unit)? = null,
	style: TextStyle = LocalTextStyle.current
) {
	Text(
		text = text,
		modifier = modifier,
		color = color,
		fontSize = 1.75.em,
		fontStyle = fontStyle,
		fontWeight = FontWeight.SemiBold,
		fontFamily = Fonts.Poppins.font(),
		letterSpacing = letterSpacing,
		textDecoration = textDecoration,
		textAlign = textAlign,
		lineHeight = lineHeight,
		overflow = overflow,
		softWrap = softWrap,
		maxLines = maxLines,
		minLines = minLines,
		onTextLayout = onTextLayout,
		style = style
	)
}

@Composable
fun SubHeader(
	text: String,
	modifier: Modifier = Modifier,
	color: Color = Color.Unspecified,
	fontStyle: FontStyle? = null,
	letterSpacing: TextUnit = TextUnit.Unspecified,
	textDecoration: TextDecoration? = null,
	textAlign: TextAlign? = null,
	lineHeight: TextUnit = TextUnit.Unspecified,
	overflow: TextOverflow = TextOverflow.Ellipsis,
	softWrap: Boolean = false,
	maxLines: Int = 1,
	minLines: Int = 1,
	onTextLayout: ((TextLayoutResult) -> Unit)? = null,
	style: TextStyle = LocalTextStyle.current
) {
	Text(
		text = text,
		modifier = modifier,
		color = color,
		fontSize = 1.25.em,
		fontStyle = fontStyle,
		fontWeight = FontWeight.Medium,
		fontFamily = Fonts.Poppins.font(),
		letterSpacing = letterSpacing,
		textDecoration = textDecoration,
		textAlign = textAlign,
		lineHeight = lineHeight,
		overflow = overflow,
		softWrap = softWrap,
		maxLines = maxLines,
		minLines = minLines,
		onTextLayout = onTextLayout,
		style = style
	)
}
