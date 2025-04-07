/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.data.enums

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.vector.ImageVector
import me.gserv.archival.utils.SelectableEnum

enum class ImageQuality(override val readableName: String, override val icon: ImageVector) : SelectableEnum {
	NOT_EVALUATED("Not Evaluated", Icons.Rounded.QuestionMark),

	TERRIBLE("Terrible", Icons.Rounded.SentimentVeryDissatisfied),
	BAD("Bad", Icons.Rounded.SentimentDissatisfied),
	FAIR("Fair", Icons.Rounded.SentimentNeutral),
	GOOD("Good", Icons.Rounded.SentimentSatisfied),
	GREAT("Great", Icons.Rounded.SentimentVerySatisfied);
}
