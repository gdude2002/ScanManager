/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.m3.views

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import me.gserv.archival.m3.components.MainHeader
import me.gserv.archival.m3.components.SubHeader
import me.gserv.archival.m3.components.layout.ContainerColumn
import me.gserv.archival.m3.data.GlobalState
import me.gserv.archival.m3.data.entities.Set

@Composable
fun bindersView() {
	Column {
		Row {
			MainHeader(
				"Binders",
				textAlign = TextAlign.Center,
				modifier = Modifier
					.weight(1f)
			)
		}

		val setCounts = remember(GlobalState.binders) {
			GlobalState.binders
				.associate { it.id to Set.binderCount(it) }
				.toMap()
		}

		val scrollState = rememberScrollState()

		Box(Modifier.fillMaxWidth().fillMaxHeight().background(MaterialTheme.colorScheme.error)) {
			val boxScope = this

			FlowRow(
				modifier = Modifier
					.verticalScroll(scrollState)
					.absolutePadding(right = 17.dp, bottom = 65.dp),

				horizontalArrangement = Arrangement.spacedBy(10.dp),
				verticalArrangement = Arrangement.spacedBy(10.dp),
			) {
				GlobalState.binders
					.sortedBy { it.id.value }
					.forEach { binder ->
						val isSelected = binder == GlobalState.binder

						ContainerColumn(
							Modifier.width(250.dp),
							backgroundColor = if (isSelected) {
								MaterialTheme.colorScheme.surfaceVariant
							} else {
								null
							}
						) {
							Row {
								SubHeader(
									binder.id.value,

									modifier = Modifier.weight(1f),
									textAlign = TextAlign.Center,
								)
							}

							Spacer(Modifier.height(10.dp))

							Row {
								Text(
									"Sets: ${setCounts[binder.id]}",

									modifier = Modifier.weight(1f),
									textAlign = TextAlign.Center
								)
							}

							Spacer(Modifier.height(10.dp))

							Row {
								Button(
									{
										// TODO: Consider loading sets at this point?
										GlobalState.binder = binder
									},

									enabled = !isSelected
								) {
									Icon(
										Icons.Filled.FolderOpen,
										""
									)

									Spacer(Modifier.width(10.dp))

									Text(
										"Open",

										modifier = Modifier.align(Alignment.CenterVertically),
										textAlign = TextAlign.Start,
									)
								}

								Spacer(Modifier.weight(1f))

								FilledIconButton(
									{
										// TODO: Deletion action.
									},

									colors = IconButtonDefaults.filledIconButtonColors(
										MaterialTheme.colorScheme.error,
										MaterialTheme.colorScheme.onError,
									)
								) {
									Icon(
										Icons.Filled.Delete,
										"Delete Binder"
									)
								}


							}
						}
					}
			}

			VerticalScrollbar(
				modifier = with(boxScope) { Modifier.align(Alignment.CenterEnd) },
				adapter = rememberScrollbarAdapter(scrollState)
			)

			Row(
				Modifier.height(65.dp).align(Alignment.BottomEnd),
			) {
				Spacer(Modifier.weight(1f))

				FloatingActionButton(
					{
						// TODO: Add binder action.
					},

					Modifier.padding(5.dp)
				) {
					Icon(Icons.Filled.Add, "Add binder.")
				}
			}
		}
	}
}
