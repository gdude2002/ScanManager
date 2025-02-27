/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 *
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import com.seanproctor.datatable.DataColumn
import com.seanproctor.datatable.TableColumnWidth
import com.seanproctor.datatable.TableRowScope
import com.seanproctor.datatable.material3.DataTable
import io.github.oshai.kotlinlogging.KotlinLogging
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
import me.gserv.archival.windows.binder.DeleteSetDialog
import me.gserv.archival.windows.binder.FilterState
import org.jetbrains.exposed.sql.SortOrder
import java.awt.Dimension

class BinderWindow(val parent: MainWindow) {
	val logger = KotlinLogging.logger { }

	var isOpen by mutableStateOf(false)
	var isDropdownOpen by mutableStateOf(false)

	var filterState by mutableStateOf<FilterState>(FilterState.All)
	var filterText by mutableStateOf("")

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
		filterText = ""

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
		val rowColorEvenHovered = Colors.LightGreen
		val rowColorEven = Colors.LightGray

		val rowColorOddHovered = Colors.LighterGreen
		val rowColorOdd = MaterialTheme.colors.surface

		if (isOpen) {
			LaunchedEffect(filterState, filterText) {
				var filtered = allSets.toList()

				when (filterState) {
					FilterState.Incomplete -> filtered = filtered.filter { it.finishedAt == null }
					FilterState.Complete -> filtered = filtered.filter { it.finishedAt != null }

					FilterState.All -> {}
				}

				if (filterText.isNotBlank()) {
					filtered = filtered.filter { it.description?.contains(filterText, true) == true }
				}

				GlobalState.sets.clear()
				GlobalState.sets = filtered.toMutableStateList()
			}

			Window({ close(); }, state = state, title = "Binder ${GlobalState.binder?.id}") {
				scope = this
				window.minimumSize = Dimension(1000, 700)

				val createSetDialog = CreateSetDialog(this@BinderWindow, GlobalState.binder!!)
				createSetDialog.create()

				val deleteSetDialog = DeleteSetDialog(this@BinderWindow)
				deleteSetDialog.create()

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
							.height(55.dp)
							.fillMaxWidth()
					) {
						Button(
							{ createSetDialog.open() },
							modifier = Modifier.fillMaxHeight()
						) {
							Text("Add")
						}

						Spacer(Modifier.weight(1f, true))

						Box(Modifier.fillMaxHeight()) {
							OutlinedButton(
								{ isDropdownOpen = !isDropdownOpen },
								enabled = allSets.isNotEmpty(),
								modifier = Modifier.fillMaxHeight()
							) {
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
									logger.info { "Updating completion filter: All sets" }

									filterState = FilterState.All
									isDropdownOpen = false
								}) {
									Text("All Sets")
								}

								Divider()

								DropdownMenuItem({
									logger.info { "Updating completion filter: Incomplete sets only" }

									filterState = FilterState.Incomplete
									isDropdownOpen = false
								}) {
									Text("Incomplete Sets")
								}

								DropdownMenuItem({
									logger.info { "Updating completion filter: Complete sets only" }

									filterState = FilterState.Complete
									isDropdownOpen = false
								}) {
									Text("Complete Sets")
								}
							}
						}

						TextField(
							value = filterText,
							onValueChange = {
								logger.info { "Updating description filter: \"$it\"" }
								filterText = it
							},
							label = {
								Text("Description", modifier = Modifier.absolutePadding(bottom = 10.dp))
							},
							modifier = Modifier.fillMaxHeight()
						)

//						StringTooltip("Binder Settings") {
//							Button(
//								{},
//								modifier = Modifier.fillMaxHeight(),
//								enabled = false,
//							) {
//								Icon(Icons.Rounded.Settings, "Binder settings")
//							}
//						}
					}

					Box(
						modifier = Modifier
							.fillMaxWidth()
							.weight(1f)
					) {
						DataTable(
							modifier = Modifier.fillMaxSize(),
							sortColumnIndex = 0,
							sortAscending = true,

							columns = listOf(
								HeaderColumn("Set"),
								HeaderColumn("Scans"),
								HeaderColumn("Set Date"),
								HeaderColumn("State"),
								HeaderColumn("Created"),
								HeaderColumn("Finished"),
								HeaderColumn("Description"),
								HeaderColumn("", TableColumnWidth.MinIntrinsic),
							)
						) {
							GlobalState.sets.forEachIndexed { index, set ->
								row {
									onClick = {
										dropTarget.currentSet = index
									}

									this.backgroundColor = if (index % 2 == 0) {
										if (index == dropTarget.currentSet) {
											rowColorEvenHovered
										} else {
											rowColorEven
										}
									} else {
										if (index == dropTarget.currentSet) {
											rowColorOddHovered
										} else {
											rowColorOdd
										}
									}

									text(set.id.value.toString())
									text(set.totalScans.toString())
									text(set.date?.format())

									if (set.finishedAt != null) {
										text("Complete")
									} else {
										text("Incomplete")
									}

									text(set.createdAt.format())
									text(set.finishedAt?.format())
									text(set.description, true)

									cell {
										Row(horizontalArrangement = Arrangement.End) {
											StringTooltip("Delete set") {
												TextButton(
													{
														deleteSetDialog.open(set)
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

								if (filterState != FilterState.All || filterText.isNotBlank()) {
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
	fun HeaderColumn(
		content: String,
		width: TableColumnWidth = TableColumnWidth.MaxIntrinsic,
		fillTextWidth: Boolean = false
	) = DataColumn(
		width = width
	) { HeaderText(content, fillTextWidth) }

	@Composable
	fun HeaderText(content: String, fillWidth: Boolean = false) {
		var modifier = Modifier.padding(horizontal = 16.dp)

		if (fillWidth) {
			modifier = modifier.fillMaxWidth()
		}

		Text(
			content,
			softWrap = false,
			modifier = modifier,
			textAlign = TextAlign.Start
		)
	}

	fun TableRowScope.text(content: String?, fillWidth: Boolean = false) = cell {
		var modifier = Modifier.padding(horizontal = 16.dp)

		if (fillWidth) {
			modifier = modifier.fillMaxWidth()
		}

		Text(
			content ?: "",
			softWrap = false,
			modifier = modifier,
			textAlign = TextAlign.Start
		)
	}

	fun TableRowScope.checkbox(checked: Boolean, onCheckedChange: ((Boolean) -> Unit)?) = cell {
		Checkbox(checked, onCheckedChange)
	}
}
