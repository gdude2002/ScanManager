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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.FrameWindowScope
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.vinceglb.filekit.compose.rememberDirectoryPickerLauncher
import io.github.vinceglb.filekit.core.FileKitPlatformSettings
import me.gserv.archival.Colors
import me.gserv.archival.config.AppConfig
import me.gserv.archival.data.Filesystem
import me.gserv.archival.utils.components.DialogContainer
import me.gserv.archival.utils.components.PrimaryButton
import me.gserv.archival.utils.components.PrimaryIconButton
import me.gserv.archival.utils.components.SecondaryButton
import java.io.File

class DataDirectoryDialog(
	val parent: FrameWindowScope,
) {
	init {
		AppConfig.load()
	}

	val logger = KotlinLogging.logger { }

	var allowCancel by mutableStateOf(true)
	var isOpen by mutableStateOf(false)
	var isPickerOpen by mutableStateOf(false)

	var dataDirectory by mutableStateOf(
		AppConfig.dataFolder
			?: File("data").absolutePath
	)

	var newDirectory by mutableStateOf(
		AppConfig.dataFolder
			?: File("data").absolutePath
	)

	fun close() {
		this.allowCancel = true
		isOpen = false
	}

	fun open(allowCancel: Boolean = true) {
		this.allowCancel = allowCancel
		isOpen = true
	}

	@Composable
	@Preview
	fun create() {
		if (isOpen) {
			val currentFile = File(newDirectory)

			if (isPickerOpen) {
				val launcher = rememberDirectoryPickerLauncher(
					title = "Select data folder",
					platformSettings = FileKitPlatformSettings(parentWindow = parent.window),

					initialDirectory = if (currentFile.isDirectory) {
						currentFile.absolutePath
					} else if (currentFile.parentFile.isDirectory) {
						currentFile.parentFile.absolutePath
					} else {
						null
					}
				) { directory ->
					if (directory != null) {
						newDirectory = directory.file.absolutePath
					}

					isPickerOpen = false
				}

				launcher.launch()
			}

			Dialog({}, DialogProperties(false, false, true)) {
				Colors.Theme { colors ->
					DialogContainer {
						Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
							Row(verticalAlignment = Alignment.CenterVertically) {
								Icon(
									Icons.Rounded.Folder,
									"Open folder",
									modifier = Modifier
										.absolutePadding(right = 8.dp, top = 1.dp)
										.size(30.dp)
								)

								Text(
									"Select Data Folder",
									fontSize = TextUnit(1.5F, TextUnitType.Em)
								)
							}

							Text(
								"Please select the folder you wish to use to store management data.\n" +
									"Select a folder containing existing data to use the data in that folder."
							)

							Row {
								TextField(
									newDirectory,
									{ newDirectory = it },
									label = { Text("Data Folder") },
									modifier = Modifier.fillMaxWidth(0.9f).absolutePadding(right = 10.dp)
								)

								PrimaryIconButton(
									{ isPickerOpen = true },
									modifier = Modifier.size(56.dp),
								) {
									Icon(
										Icons.Rounded.FolderOpen,
										"Open folder"
									)
								}
							}

							Row {
								if (allowCancel) {
									SecondaryButton(onClick = {
										logger.debug { "Closing without changing data directory" }

										close()
									}) {
										Icon(
											Icons.Rounded.Cancel,
											"",
											modifier = Modifier.absolutePadding(right = 4.dp)
										)

										Text("Cancel")
									}
								}

								Spacer(Modifier.weight(1f, true))

								PrimaryButton(onClick = {
									logger.debug { "Changing data directory to $newDirectory" }

									dataDirectory = newDirectory

									AppConfig.dataFolder = dataDirectory
									AppConfig.save()

									logger.debug { "Ensuring data directory exists..." }

									Filesystem.ensureBinders()

									logger.debug { "Done, closing dialog" }

									close()
								}) {
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
