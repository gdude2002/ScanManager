/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.m3.components.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Container(
	modifier: Modifier = Modifier,
	contentAlignment: Alignment = Alignment.TopStart,
	propagateMinConstraints: Boolean = false,

	content: @Composable BoxScope.() -> Unit
) {
	Box(
		modifier = modifier
			.background(MaterialTheme.colorScheme.surfaceContainer, ShapeDefaults.Small)
			.padding(10.dp),

		contentAlignment = contentAlignment,
		propagateMinConstraints = propagateMinConstraints,
		content = content,
	)
}

@Composable
fun ContainerColumn(
	modifier: Modifier = Modifier,
	contentAlignment: Alignment = Alignment.TopStart,
	propagateMinConstraints: Boolean = false,

	columnModifier: Modifier = Modifier,
	verticalArrangement: Arrangement.Vertical = Arrangement.Top,
	horizontalAlignment: Alignment.Horizontal = Alignment.Start,

	content: @Composable ColumnScope.() -> Unit
) {
	Container(
		modifier = modifier,
		contentAlignment = contentAlignment,
		propagateMinConstraints = propagateMinConstraints,
	) {
		Column(
			modifier = columnModifier,
			verticalArrangement = verticalArrangement,
			horizontalAlignment = horizontalAlignment,
			content = content
		)
	}
}

@Composable
fun ContainerRow(
	modifier: Modifier = Modifier,
	contentAlignment: Alignment = Alignment.TopStart,
	propagateMinConstraints: Boolean = false,

	rowModifier: Modifier = Modifier,
	horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
	verticalAlignment: Alignment.Vertical = Alignment.Top,

	content: @Composable RowScope.() -> Unit
) {
	Container(
		modifier = modifier,
		contentAlignment = contentAlignment,
		propagateMinConstraints = propagateMinConstraints,
	) {
		Row(
			modifier = rowModifier,
			horizontalArrangement = horizontalArrangement,
			verticalAlignment = verticalAlignment,
			content = content
		)
	}
}
