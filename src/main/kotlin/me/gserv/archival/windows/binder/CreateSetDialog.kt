/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.windows.binder

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.VideoFile
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.datetime.LocalDate
import me.gserv.archival.data.Database
import me.gserv.archival.data.GlobalState
import me.gserv.archival.data.entities.Binder
import me.gserv.archival.data.entities.Set
import me.gserv.archival.utils.now
import me.gserv.archival.windows.BinderWindow

class CreateSetDialog(
	val parent: BinderWindow,
	val binder: Binder,
) {
	val logger = KotlinLogging.logger { }

	var isError by mutableStateOf(false)
	var isOpen by mutableStateOf(false)

	var setDescription by mutableStateOf("")
	var setIdentifier by mutableStateOf("")

	fun close() {
		isOpen = false
		isError = false

		setDescription = ""
		setIdentifier = ""
	}

	fun open() {
		isOpen = true
		isError = false

		setDescription = ""
		setIdentifier = ""
	}

	@Composable
	@Preview
	fun create() {
		if (isOpen) {
			Dialog({}, DialogProperties(false, false, true)) {
				Card(backgroundColor = Color.White, shape = RoundedCornerShape(15.dp)) {
					Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
						Row(verticalAlignment = Alignment.CenterVertically) {
							Icon(
								Icons.Rounded.VideoFile,
								"",
								modifier = Modifier
									.absolutePadding(right = 8.dp, top = 1.dp)
									.size(30.dp)
							)

							Text(
								"Create Set",
								fontSize = TextUnit(1.5F, TextUnitType.Em)
							)
						}

						Text(
							"Please enter a numeric identifier for the new set. If the set has a label with an " +
								"identifying code, we recommend using that code. " +
								"Don't include the binder name.\n\n" +
								"Set identifiers must be unique within the binder."
						)

						Row {
							TextField(
								setIdentifier,

								{
									if (!it.all { c -> c.isDigit() }) {
										return@TextField
									}

									if (it.isEmpty()) {
										setIdentifier = it

										return@TextField
									}

									setIdentifier = it.trimStart('0').padStart(1, '0')

									Database.transaction {
										isError = Set.exists(setIdentifier.toLong(), binder)
									}
								},

								keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
								label = { Text("Identifier") },
								modifier = Modifier.fillMaxWidth()
							)
						}

						Row {
							TextField(
								setDescription,
								{ setDescription = it },

								label = { Text("Description") },
								modifier = Modifier.fillMaxWidth()
							)
						}

						if (isError) {
							Row {
								Text(
									"Identifier $setIdentifier is invalid, or a set with that identifier already " +
										"exists. Please pick another identifier.",

									color = Color.Red,
									textAlign = TextAlign.Center
								)
							}
						}

						Row {
							Button(
								{
									logger.info { "Closing dialog without creating set." }
									close()
								}
							) {
								Row(verticalAlignment = Alignment.CenterVertically) {
									Icon(
										Icons.Rounded.Cancel,
										"",
										modifier = Modifier.absolutePadding(right = 4.dp)
									)

									Text("Cancel")
								}
							}

							Spacer(Modifier.weight(1f, true))

							Button(
								enabled = setIdentifier.isNotEmpty() && !isError,

								onClick = {
									logger.info { "Creating set $setIdentifier in binder ${binder.id.value}" }
									logger.info { "Description: $setDescription" }

									val set = Database.transaction {
										Set.create(setIdentifier.toLong(), binder) {
											date = LocalDate.now()

											if (setDescription.isNotEmpty()) {
												description = setDescription
											}

											totalScans = binder.countSetScans(setIdentifier.toLong()).toLong()
										}
									}

									logger.info { "Set created, updating global and window states..." }

									GlobalState.sets.add(set)
									GlobalState.sets.sortByDescending { it.id.value.toLong() }

									parent.allSets.add(set)
									parent.allSets.sortByDescending { it.id.value.toLong() }

									logger.info { "Done, closing dialog" }

									close()
								},
							) {
								Row(verticalAlignment = Alignment.CenterVertically) {
									Icon(
										Icons.Rounded.Check,
										"",
										modifier = Modifier.absolutePadding(right = 4.dp)
									)

									Text("Okay")
								}
							}
						}
					}
				}
			}
		}
	}
}
