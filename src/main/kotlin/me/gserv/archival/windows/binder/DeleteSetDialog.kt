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
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.FrameWindowScope
import me.gserv.archival.Colors
import me.gserv.archival.config.AppConfig
import me.gserv.archival.data.Database
import me.gserv.archival.data.Filesystem
import me.gserv.archival.data.GlobalState
import me.gserv.archival.data.entities.Binder
import me.gserv.archival.data.entities.Set
import me.gserv.archival.windows.BinderWindow

class DeleteSetDialog(
	val parent: BinderWindow,
) {
	init {
		AppConfig.load()
	}

	var set: Set? = null
	var totalFiles: Int = 0

	var isOpen by mutableStateOf(false)

	fun close() {
		this.set = null
		this.totalFiles = 0

		isOpen = false
	}

	fun open(set: Set) {
		this.set = set

		Database.transaction {
			set.getFiles().forEach {
				if (it.original != null) {
					totalFiles += 1
				}

				if (it.edit != null) {
					totalFiles += 1
				}
			}
		}

		isOpen = true
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
								Icons.Rounded.Warning,
								"Delete set",
								modifier = Modifier
									.absolutePadding(right = 8.dp, top = 1.dp)
									.size(30.dp)
							)

							Text(
								"Delete Set",
								fontSize = TextUnit(1.5F, TextUnitType.Em)
							)
						}

						Text(
							"Are you sure you wish to delete Set ${set?.id?.value} and its $totalFiles associated " +
								"images?\n\n" +

								"The set, its data and its images will be permanently removed, and you won't " +
								"be able to restore it.\n\n" +

								"Consider using the backup function before deleting any sets."
						)

						Row {
							Button(
								onClick = {
									close()
								},
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
								onClick = {
									set?.let {
										Database.transaction {
											it.getFiles().forEach { container ->
												container.original?.delete()
												container.edit?.delete()
											}

											it.delete()
										}
									}

									parent.allSets.remove(set)
									GlobalState.sets.remove(set)

									close()
								},
								colors = ButtonDefaults.buttonColors(
									backgroundColor = Colors.Danger,
									contentColor = Color.White
								),
							) {
								Row(verticalAlignment = Alignment.CenterVertically) {
									Icon(
										Icons.Rounded.Delete,
										"",
										modifier = Modifier.absolutePadding(right = 4.dp)
									)

									Text("Delete Set")
								}
							}
						}
					}
				}
			}
		}
	}
}
