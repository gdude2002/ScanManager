/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.windows.main

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.FrameWindowScope
import io.github.oshai.kotlinlogging.KotlinLogging
import me.gserv.archival.Colors
import me.gserv.archival.config.AppConfig
import me.gserv.archival.data.Database
import me.gserv.archival.data.Filesystem
import me.gserv.archival.data.GlobalState
import me.gserv.archival.data.entities.Binder
import me.gserv.archival.utils.components.PrimaryButton
import me.gserv.archival.utils.components.SecondaryButton

class CreateBinderDialog(
	val parent: FrameWindowScope,
) {
	init {
		AppConfig.load()
	}

	val logger = KotlinLogging.logger { }

	var isError by mutableStateOf(false)
	var isOpen by mutableStateOf(false)
	var binderName by mutableStateOf("")

	fun close() {
		isOpen = false
		isError = false

		binderName = ""
	}

	fun open() {
		isOpen = true
		isError = false

		binderName = ""
	}

	@Composable
	@Preview
	fun create() {
		if (isOpen) {
			Dialog({}, DialogProperties(false, false, true)) {
				Colors.Theme { colors ->
					Card(shape = RoundedCornerShape(15.dp)) {
						Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
							Row(verticalAlignment = Alignment.CenterVertically) {
								Icon(
									Icons.Rounded.Book,
									"Create binder",
									modifier = Modifier
										.absolutePadding(right = 8.dp, top = 1.dp)
										.size(30.dp)
								)

								Text(
									"Create Binder",
									fontSize = TextUnit(1.5F, TextUnitType.Em)
								)
							}

							Text(
								"Please enter a name for the new binder. If the binder has a label with an " +
									"identifying code, we recommend using that code.\n\n" +
									"Binder names must be unique."
							)

							Row {
								TextField(
									binderName,

									{
										binderName = it

										Database.transaction {
											isError = Binder.findById(binderName) != null
										}
									},

									label = { Text("Name") },

									isError = isError,
									trailingIcon = {
										if (isError) {
											Icon(
												Icons.Rounded.Warning,
												"Error"
											)
										}
									},

									modifier = Modifier.fillMaxWidth(),
								)
							}

							if (isError) {
								Row {
									Text(
										"A binder named \"$binderName\" already exists. Please pick another name.",

										color = colors.Material.error,
										textAlign = TextAlign.Center
									)
								}
							}

							Row {
								SecondaryButton(
									{
										logger.debug { "Closing dialog without creating binder" }

										close()
									}
								) {
									Icon(
										Icons.Rounded.Cancel,
										"",
										modifier = Modifier.absolutePadding(right = 4.dp)
									)

									Text("Cancel")
								}

								Spacer(Modifier.weight(1f, true))

								PrimaryButton(
									onClick = {
										logger.debug { "Creating binder $binderName" }

										val binder = Database.transaction {
											Binder.create(binderName)
										}

										Filesystem.ensureBinder(binder.slug)

										logger.debug { "Reloading global state..." }

										GlobalState.loadBinders()

										logger.debug { "Done, closing dialog" }

										close()
									},

									enabled = binderName.isNotEmpty() && !isError
								) {
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
