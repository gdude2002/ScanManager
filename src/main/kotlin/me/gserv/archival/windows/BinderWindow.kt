/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

@file:OptIn(ExperimentalFoundationApi::class)

package me.gserv.archival.windows

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import com.seanproctor.datatable.DataColumn
import com.seanproctor.datatable.TableColumnWidth
import com.seanproctor.datatable.TableRowScope
import com.seanproctor.datatable.material3.DataTable
import me.gserv.archival.Colors
import me.gserv.archival.data.Database
import me.gserv.archival.data.GlobalState
import me.gserv.archival.data.entities.Binder
import me.gserv.archival.data.entities.Set
import me.gserv.archival.data.tables.SetTable
import me.gserv.archival.dropTarget
import me.gserv.archival.utils.StringTooltip
import me.gserv.archival.utils.format
import me.gserv.archival.windows.binder.CreateSetDialog
import me.gserv.archival.windows.binder.FilterState
import org.jetbrains.exposed.sql.SortOrder
import java.awt.Dimension

class BinderWindow(val parent: MainWindow) {
    var isOpen by mutableStateOf(false)
    var isDropdownOpen by mutableStateOf(false)

    var filterState by mutableStateOf<FilterState>(FilterState.All)

    var allSets: SnapshotStateList<Set> = mutableStateListOf()

    val state = WindowState(
        size = DpSize(1000.dp, 700.dp),
    )

    lateinit var scope: FrameWindowScope

    fun close() {
        GlobalState.binder = null
        GlobalState.sets.clear()

        allSets.clear()

        isOpen = false
        isDropdownOpen = false
        filterState = FilterState.All

        parent.scope.window.isVisible = true
    }

    fun open(binder: Binder) {
        GlobalState.binder = binder

        val sets = Database.transaction {
            Set.find {
                SetTable.binder eq binder.id
            }.orderBy(SetTable.id to SortOrder.DESC)
                .toList()
        }

        GlobalState.sets.addAll(sets)
        this.allSets.addAll(sets)

        isOpen = true
        parent.scope.window.isVisible = false
    }

    @Composable
    @Preview
    fun create() {
        if (isOpen) {
            Window({ close(); }, state = state, title = "Binder ${GlobalState.binder?.id}") {
                scope = this
                window.minimumSize = Dimension(1000, 700)

                val createSetDialog = CreateSetDialog(this@BinderWindow, GlobalState.binder!!)
                createSetDialog.create()

                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .background(
                                Colors.LightGray
                            )
                            .padding(vertical = 10.dp, horizontal = 15.dp)
                            .height(40.dp)
                            .fillMaxWidth()
                    ) {
                        Button(
                            { createSetDialog.open() }
                        ) {
                            Text("Add")
                        }

                        Spacer(Modifier.weight(1f, true))

                        Box(Modifier) {
                            OutlinedButton({ isDropdownOpen = !isDropdownOpen }, enabled = allSets.isNotEmpty()) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (allSets.isNotEmpty()) {
                                        Text("Filter: ${filterState.readableName} Sets")

                                        if (isDropdownOpen) {
                                            Icon(Icons.Default.ArrowDropUp, "")
                                        } else {
                                            Icon(Icons.Default.ArrowDropDown, "")
                                        }
                                    } else {
                                        Text("No sets")
                                    }
                                }
                            }

                            DropdownMenu(isDropdownOpen, { isDropdownOpen = false }) {
                                DropdownMenuItem({
                                    filterState = FilterState.All

                                    GlobalState.sets.clear()
                                    GlobalState.sets.addAll(allSets)

                                    isDropdownOpen = false
                                }) {
                                    Text("All Sets")
                                }

                                Divider()

                                DropdownMenuItem({
                                    filterState = FilterState.Incomplete

                                    GlobalState.sets.clear()
                                    GlobalState.sets.addAll(allSets.filter { it.finishedAt == null })

                                    isDropdownOpen = false
                                }) {
                                    Text("Incomplete Sets")
                                }

                                DropdownMenuItem({
                                    filterState = FilterState.Complete

                                    GlobalState.sets.clear()
                                    GlobalState.sets.addAll(allSets.filter { it.finishedAt != null })

                                    isDropdownOpen = false
                                }) {
                                    Text("Complete Sets")
                                }
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        DataTable(
                            modifier = Modifier.fillMaxSize(),

                            columns = listOf(
                                HeaderColumn("Set"),
                                HeaderColumn("Scans"),
                                HeaderColumn("State"),
                                HeaderColumn("Set Date"),
                                HeaderColumn("Creation Date"),
                                HeaderColumn("Completion Date"),
                                HeaderColumn("Description"),
                                HeaderColumn("", TableColumnWidth.MinIntrinsic),
                            ),

                            rowBackgroundColor = {
                                if (it % 2 == 0) {
                                    if (dropTarget.isHovered && it == dropTarget.currentSet) {
                                        Colors.LightGreen
                                    } else {
                                        Colors.LightGray
                                    }
                                } else {
                                    if (dropTarget.isHovered && it == dropTarget.currentSet) {
                                        Colors.LighterGreen
                                    } else {
                                        MaterialTheme.colors.surface
                                    }
                                    MaterialTheme.colors.surface
                                }
                            },
                        ) {
                            GlobalState.sets.forEach { set ->
                                row {
                                    text(set.id.value.toString())
                                    text(set.totalScans.toString())

                                    if (set.finishedAt != null) {
                                        text("Complete")
                                    } else {
                                        text("Incomplete")
                                    }

                                    text(set.date?.format())
                                    text(set.createdAt.format())
                                    text(set.finishedAt?.format())
                                    text(set.description)

                                    cell {
                                        StringTooltip("Delete set") {
                                            TextButton(
                                                {
                                                    // TODO: Delete action
                                                },
                                                modifier = Modifier.padding(horizontal = 0.dp).width(40.dp),
                                                contentPadding = PaddingValues(0.dp)
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.padding(0.dp)
                                                ) {
                                                    Icon(
                                                        Icons.Rounded.Delete,
                                                        "Delete set",
                                                        tint = Color.Red
                                                    )
                                                }
                                            }
                                        }

                                        StringTooltip("Open set") {
                                            TextButton(
                                                {
                                                    // TODO: Open action
                                                },
                                                modifier = Modifier.padding(horizontal = 0.dp).width(40.dp),
                                                contentPadding = PaddingValues(0.dp)
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.padding(0.dp)
                                                ) {
                                                    Icon(
                                                        Icons.Rounded.FolderOpen,
                                                        "Open set"
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .background(Colors.LightGray)
                            .padding(vertical = 10.dp, horizontal = 15.dp)
                            .height(20.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            buildString {
                                append("${allSets.size} total sets")

                                if (filterState != FilterState.All) {
                                    append(
                                        " (${allSets.size - GlobalState.sets.size} hidden, " +
                                                "${GlobalState.sets.size} visible)"
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun HeaderColumn(content: String, width: TableColumnWidth = TableColumnWidth.MaxIntrinsic) = DataColumn(
        width = width
    ) { HeaderText(content) }

    @Composable
    fun HeaderText(content: String) = Text(
        content,
        softWrap = false,
        modifier = Modifier.padding(horizontal = 16.dp)
    )

    fun TableRowScope.text(content: String?) = cell {
        cell { Text(content ?: "", softWrap = false) }
    }

    fun TableRowScope.checkbox(checked: Boolean, onCheckedChange: ((Boolean) -> Unit)?) = cell {
        Checkbox(checked, onCheckedChange)
    }
}
