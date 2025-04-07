package me.gserv.archival.utils.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DialogContainer(
	content: @Composable ColumnScope.() -> Unit
) {
	Card(
		shape = RoundedCornerShape(15.dp),
		modifier = Modifier.padding(5.dp),
		content = content,
	)
}
