/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.m3.views

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

		val scrollState = rememberScrollState()

		Row {
			Box {
				FlowRow(
					modifier = Modifier
						.verticalScroll(scrollState)
						.absolutePadding(right = 17.dp),

					horizontalArrangement = Arrangement.spacedBy(10.dp),
					verticalArrangement = Arrangement.spacedBy(10.dp),
				) {
					GlobalState.binders
						.sortedBy { it.id.value }
						.forEach { binder ->
							ContainerColumn(Modifier.width(250.dp)) {
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
										"Sets: ${Set.binderCount(binder)}",

										modifier = Modifier.weight(1f),
										textAlign = TextAlign.Center
									)
								}

								Spacer(Modifier.height(10.dp))

								Row {
									Button(
										{},
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
										{},
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
					modifier = Modifier.align(Alignment.CenterEnd),
					adapter = rememberScrollbarAdapter(scrollState)
				)
			}
		}
	}
}
