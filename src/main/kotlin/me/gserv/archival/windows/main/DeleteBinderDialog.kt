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
import androidx.compose.material.icons.rounded.*
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
import me.gserv.archival.data.Database
import me.gserv.archival.data.Filesystem
import me.gserv.archival.data.GlobalState
import me.gserv.archival.data.entities.Binder

class DeleteBinderDialog(
    val parent: FrameWindowScope,
) {
    init {
        AppConfig.load()
    }

    var binder: Binder? = null

    var isOpen by mutableStateOf(false)

    fun close() {
        this.binder = null

        isOpen = false
    }

    fun open(binder: Binder) {
        this.binder = binder

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
                                "Delete binder",
                                modifier = Modifier
                                    .absolutePadding(right = 8.dp, top = 1.dp)
                                    .size(30.dp)
                            )

                            Text(
                                "Delete Binder",
                                fontSize = TextUnit(1.5F, TextUnitType.Em)
                            )
                        }

                        Text(
                            "Are you sure you wish to delete Binder ${binder?.id}?\n\n" +

                                    "The binder, its data and its images will be permanently removed, and you won't " +
                                    "be able to restore it.\n\n" +

                                    "Consider using the backup function before deleting any binders."
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
                                    binder?.let {
                                        Filesystem.deleteBinder(it.slug)

                                        Database.transaction {
                                            it.delete()
                                        }
                                    }

                                    GlobalState.loadBinders()

                                    close()
                                },
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Rounded.Delete,
                                        "",
                                        modifier = Modifier.absolutePadding(right = 4.dp)
                                    )

                                    Text("Delete Binder")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
