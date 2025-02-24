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
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.FolderOpen
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
import me.gserv.archival.config.AppConfig
import me.gserv.archival.data.Filesystem
import io.github.vinceglb.filekit.compose.rememberDirectoryPickerLauncher
import io.github.vinceglb.filekit.core.FileKitPlatformSettings
import java.io.File

class DataDirectoryDialog(
    val parent: FrameWindowScope,
) {
    init {
        AppConfig.load()
    }

    var isOpen by mutableStateOf(false)
    var isPickerOpen by mutableStateOf(false)

    var dataDirectory by mutableStateOf(
        AppConfig.dataFolder
            ?: File("./data").absolutePath
    )

    var newDirectory by mutableStateOf(
        AppConfig.dataFolder
            ?: File("./data").absolutePath
    )

    fun close() {
        isOpen = false
    }

    fun open() {
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
                Card(backgroundColor = Color.White, shape = RoundedCornerShape(15.dp)) {
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

                            Button(modifier = Modifier.size(56.dp), onClick = {
                                isPickerOpen = true
                            }) {
                                Icon(
                                    Icons.Rounded.FolderOpen,
                                    "Open folder"
                                )
                            }
                        }

                        Row {
                            Button(onClick = {
                                close()
                            }) {
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

                            Button(onClick = {
                                dataDirectory = newDirectory

                                AppConfig.dataFolder = dataDirectory
                                AppConfig.save()

                                Filesystem.ensureBinders()

                                close()
                            }) {
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
