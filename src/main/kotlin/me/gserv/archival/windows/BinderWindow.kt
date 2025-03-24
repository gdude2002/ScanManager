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
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Article
import androidx.compose.material.icons.automirrored.rounded.EventNote
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import io.github.kdroidfilter.platformtools.darkmodedetector.windows.setWindowsAdaptiveTitleBar
import io.github.oshai.kotlinlogging.KotlinLogging
import me.gserv.archival.Colors
import me.gserv.archival.data.Database
import me.gserv.archival.data.GlobalState
import me.gserv.archival.data.entities.Binder
import me.gserv.archival.data.entities.Set
import me.gserv.archival.data.tables.SetTable
import me.gserv.archival.dropTarget
import me.gserv.archival.utils.StringTooltip
import me.gserv.archival.utils.components.PrimaryButton
import me.gserv.archival.utils.components.SecondaryButton
import me.gserv.archival.utils.components.TertiaryButton
import me.gserv.archival.utils.format
import me.gserv.archival.windows.binder.*
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.and
import java.awt.Dimension

class BinderWindow(val parent: MainWindow) {
	val logger = KotlinLogging.logger { }

	var isOpen by mutableStateOf(false)
	var isVisible by mutableStateOf(true)

	var isFilterDropdownOpen by mutableStateOf(false)
	var isSortingDropdownOpen by mutableStateOf(false)

	var filterState by mutableStateOf<FilterState>(FilterState.All)
	var filterText by mutableStateOf("")

	var sortState: SortingField<*> by mutableStateOf(SortingField.Number)
	var sortAsc: Boolean by mutableStateOf(false)

	var allSets: SnapshotStateList<Set> = mutableStateListOf()

	val state = WindowState(
		size = DpSize(1000.dp, 700.dp),
	)

	lateinit var scope: FrameWindowScope

	fun hide() {
		isVisible = false
		scope.window.isVisible = false
	}

	fun show() {
		scope.window.isVisible = true
		scope.window.requestFocus()
		isVisible = true

		dropTarget.state {
			if (GlobalState.sets.isEmpty()) {
				icon = Icons.Default.QuestionMark
				smallText = "No sets\nvisible"
			} else {
				smallText = "Set"
				bigText = GlobalState.sets[dropTarget.currentSet].id.value.toString()
			}
		}
	}

	fun close() {
		GlobalState.binder = null
		GlobalState.sets.clear()

		allSets.clear()

		isOpen = false

		parent.show()

		isFilterDropdownOpen = false

		filterState = FilterState.All
		filterText = ""

		sortState = SortingField.Number
		sortAsc = false
	}

	fun open(binder: Binder) {
		parent.hide()

		GlobalState.binder = binder

		updateSets(true)

		isOpen = true
	}

	fun updateSets(replaceFullList: Boolean = false) {
		val sets = Database.transaction {
			val op = with(SqlExpressionBuilder) {
				var current = SetTable.binder eq GlobalState.binder!!.id

				when (filterState) {
					FilterState.All -> {}
					FilterState.Complete -> current = current and (SetTable.finishedAt neq null)
					FilterState.Incomplete -> current = current and (SetTable.finishedAt eq null)
				}

				current
			}

			val direction = if (sortAsc) {
				SortOrder.ASC
			} else {
				SortOrder.DESC
			}

			Set.find(op)
				.orderBy(sortState.expression to direction)
				.toList()
				.toMutableStateList()
		}

		GlobalState.sets = sets

		if (replaceFullList) {
			this.allSets = sets
		}
	}

	@OptIn(ExperimentalMaterialApi::class)
	@Composable
	@Preview
	fun create() {
		if (isOpen) {
			if (isVisible) {
				LaunchedEffect(GlobalState.sets, dropTarget.currentSet) {
					if (dropTarget.currentSet >= GlobalState.sets.size) {
						dropTarget.currentSet = 0
					}

					dropTarget.state {
						if (GlobalState.sets.isEmpty()) {
							icon = Icons.Default.QuestionMark
							smallText = "No sets\nvisible"
						} else {
							smallText = "Set"
							bigText = GlobalState.sets[dropTarget.currentSet].id.value.toString()
						}
					}
				}

				LaunchedEffect(filterState, filterText, sortState, sortAsc) {
					var filtered = allSets.toList()

					when (filterState) {
						FilterState.Incomplete -> filtered = filtered.filter { it.finishedAt == null }
						FilterState.Complete -> filtered = filtered.filter { it.finishedAt != null }

						FilterState.All -> {}
					}

					if (filterText.isNotBlank()) {
						filtered = filtered.filter { it.description?.contains(filterText, true) == true }
					}

					updateSets()
				}
			}

			Window(::close, state = state, title = "Binder ${GlobalState.binder?.id}") {
				scope = this

				window.minimumSize = Dimension(1000, 700)
				window.setWindowsAdaptiveTitleBar()

				val createSetDialog = CreateSetDialog(this@BinderWindow)
				createSetDialog.create()

				val deleteSetDialog = DeleteSetDialog(this@BinderWindow)
				deleteSetDialog.create()

				val editSetDialog = EditSetDialog(this@BinderWindow)
				editSetDialog.create()

				val setWindow = SetWindow(this@BinderWindow)
				setWindow.create()

				Colors.Theme { colors ->
					Column(
						modifier = Modifier.fillMaxSize()
							.background(colors.WindowBackground)
					) {
						Row(
							horizontalArrangement = Arrangement.spacedBy(10.dp),
							modifier = Modifier
								.background(
									colors.SectionBackground
								)
								.padding(vertical = 10.dp, horizontal = 15.dp)
								.height(50.dp)
//								.height(66.dp)
								.fillMaxWidth()
						) {
							PrimaryButton(
								{ createSetDialog.open() },
								modifier = Modifier.fillMaxHeight()
							) {
								Text("Add")
							}

							Spacer(Modifier.weight(1f, true))

							Box(Modifier.fillMaxHeight()) {
								OutlinedButton(
									{ isFilterDropdownOpen = !isFilterDropdownOpen },
									enabled = allSets.isNotEmpty(),
									modifier = Modifier.fillMaxHeight()
								) {
									if (allSets.isNotEmpty()) {
										if (isFilterDropdownOpen) {
											Icon(Icons.Rounded.KeyboardArrowUp, "")
										} else {
											Icon(Icons.Rounded.KeyboardArrowDown, "")
										}

										Text("Filter: ${filterState.readableName} Sets")
									} else {
										Text("No sets")
									}
								}

								DropdownMenu(isFilterDropdownOpen, { isFilterDropdownOpen = false }) {
									DropdownMenuItem(
										onClick = {
											logger.info { "Updating completion filter: All sets" }

											filterState = FilterState.All
											isFilterDropdownOpen = false
										},

										colors = if (filterState == FilterState.All) {
											colors.successMenuItemColors()
										} else {
											colors.defaultMenuItemColors()
										},

										leadingIcon = { Icon(Icons.Rounded.AllInclusive, "") },
										text = { Text("All Sets") }
									)

									HorizontalDivider()

									DropdownMenuItem(
										onClick = {
											logger.info { "Updating completion filter: Incomplete sets only" }

											filterState = FilterState.Incomplete
											isFilterDropdownOpen = false
											dropTarget.currentSet = 0
										},

										colors = if (filterState == FilterState.Incomplete) {
											colors.successMenuItemColors()
										} else {
											colors.defaultMenuItemColors()
										},

										leadingIcon = { Icon(Icons.Rounded.Close, "") },
										text = { Text("Incomplete Sets") }
									)

									DropdownMenuItem(
										onClick = {
											logger.info { "Updating completion filter: Complete sets only" }

											filterState = FilterState.Complete
											isFilterDropdownOpen = false
											dropTarget.currentSet = 0
										},

										colors = if (filterState == FilterState.Complete) {
											colors.successMenuItemColors()
										} else {
											colors.defaultMenuItemColors()
										},

										leadingIcon = { Icon(Icons.Rounded.Check, "") },
										text = { Text("Complete Sets") }
									)
								}
							}

							Box(Modifier.fillMaxHeight()) {
								OutlinedButton(
									{ isSortingDropdownOpen = !isSortingDropdownOpen },
									enabled = allSets.isNotEmpty(),
									modifier = Modifier.fillMaxHeight()
								) {
									if (allSets.isNotEmpty()) {
										if (isSortingDropdownOpen) {
											Icon(Icons.Rounded.KeyboardArrowUp, "")
										} else {
											Icon(Icons.Rounded.KeyboardArrowDown, "")
										}

										Text("Sort: ${sortState.readableName}")

										if (sortAsc) {
											Icon(Icons.Rounded.North, "")
										} else {
											Icon(Icons.Rounded.South, "")
										}
									} else {
										Text("No sets")
									}
								}

								DropdownMenu(isSortingDropdownOpen, { isSortingDropdownOpen = false }) {
									DropdownMenuItem(
										onClick = { sortAsc = true },
										leadingIcon = { Icon(Icons.Rounded.North, "") },
										text = { Text("Ascending") },

										colors = if (sortAsc) {
											colors.successMenuItemColors()
										} else {
											colors.defaultMenuItemColors()
										},
									)

									DropdownMenuItem(
										onClick = { sortAsc = false },
										leadingIcon = { Icon(Icons.Rounded.South, "") },
										text = { Text("Descending") },

										colors = if (sortAsc) {
											colors.defaultMenuItemColors()
										} else {
											colors.successMenuItemColors()
										},
									)

									HorizontalDivider()

									DropdownMenuItem(
										onClick = {
											logger.info { "Updating sorting field: ID Number" }

											sortState = SortingField.Number
											isSortingDropdownOpen = false
										},

										colors = if (sortState == SortingField.Number) {
											colors.successMenuItemColors()
										} else {
											colors.defaultMenuItemColors()
										},

										leadingIcon = { Icon(Icons.Rounded.LocalOffer, "") },
										text = { Text("ID Number") }
									)

									HorizontalDivider()

									DropdownMenuItem(
										onClick = {
											logger.info { "Updating sorting field: Completion Date" }

											sortState = SortingField.CompletionDate
											isSortingDropdownOpen = false
										},

										colors = if (sortState == SortingField.CompletionDate) {
											colors.successMenuItemColors()
										} else {
											colors.defaultMenuItemColors()
										},

										leadingIcon = { Icon(Icons.Rounded.EventAvailable, "") },
										text = { Text("Completion Date") }
									)

									DropdownMenuItem(
										onClick = {
											logger.info { "Updating sorting field: Creation Date" }

											sortState = SortingField.CreationDate
											isSortingDropdownOpen = false
										},

										colors = if (sortState == SortingField.CreationDate) {
											colors.successMenuItemColors()
										} else {
											colors.defaultMenuItemColors()
										},

										leadingIcon = { Icon(Icons.Rounded.Event, "") },
										text = { Text("Creation Date") }
									)

									DropdownMenuItem(
										onClick = {
											logger.info { "Updating sorting field: Photography Date" }

											sortState = SortingField.PhotographyDate
											isSortingDropdownOpen = false
										},

										colors = if (sortState == SortingField.PhotographyDate) {
											colors.successMenuItemColors()
										} else {
											colors.defaultMenuItemColors()
										},

										leadingIcon = { Icon(Icons.AutoMirrored.Rounded.EventNote, "") },
										text = { Text("Photography Date") }
									)

									HorizontalDivider()

									DropdownMenuItem(
										onClick = {
											logger.info { "Updating sorting field: Description" }

											sortState = SortingField.Description
											isSortingDropdownOpen = false
										},

										colors = if (sortState == SortingField.Description) {
											colors.successMenuItemColors()
										} else {
											colors.defaultMenuItemColors()
										},

										leadingIcon = { Icon(Icons.AutoMirrored.Rounded.Article, "") },
										text = { Text("Description") }
									)

									DropdownMenuItem(
										onClick = {
											logger.info { "Updating sorting field: Total Scans" }

											sortState = SortingField.TotalScans
											isSortingDropdownOpen = false
										},

										colors = if (sortState == SortingField.TotalScans) {
											colors.successMenuItemColors()
										} else {
											colors.defaultMenuItemColors()
										},

										leadingIcon = { Icon(Icons.Rounded.CropFree, "") },
										text = { Text("Total Scans") }
									)
								}
							}

							TextField(
								value = filterText,

								onValueChange = {
									logger.info { "Updating description filter: \"$it\"" }

									filterText = it
									dropTarget.currentSet = 0
								},

								label = { Text("Description") },
								modifier = Modifier.fillMaxHeight()
							)
						}

						HorizontalDivider(
							color = colors.Material.primary,
							thickness = 1.dp,
							modifier = Modifier.fillMaxWidth()
						)

						Spacer(Modifier.height(10.dp))

						Box(
							modifier = Modifier
								.background(colors.WindowBackground)
								.fillMaxWidth()
								.weight(1f)
						) {
							val scrollState = rememberScrollState(0)
							val scrollAdapter = rememberScrollbarAdapter(scrollState)

							Column(
								Modifier
									.verticalScroll(scrollState)
									.fillMaxSize()
									.absolutePadding(left = 10.dp, right = 17.dp)
							) {
								GlobalState.sets.forEachIndexed { index, set ->
									Column(
										verticalArrangement = Arrangement.spacedBy(10.dp),
										modifier = Modifier
											.background(
												if (index == dropTarget.currentSet) {
													colors.RowHovered
												} else {
													colors.SectionBackground
												},
												RoundedCornerShape(15.dp)
											)
											.fillMaxWidth()
											.padding(10.dp)
									) {
										Row(
											verticalAlignment = Alignment.CenterVertically,
											horizontalArrangement = Arrangement.spacedBy(10.dp),
										) {
											Text(
												"Set ${set.id.value}",
												fontSize = 2.em,
											)

											StringTooltip("Number of scans") {
												SuggestionChip(
													{},

													colors = colors.defaultChipColors(),

													icon = {
														Icon(
															Icons.Rounded.Image,
															"",
															modifier = Modifier.absolutePadding(left = 5.dp)
														)
													},

													label = { Text(set.totalScans.toString()) }
												)
											}

											StringTooltip("Set completion state") {
												SuggestionChip(
													{},

													colors = if (set.finishedAt != null) {
														colors.successChipColors()
													} else {
														colors.tertiaryChipColors()
													},

													icon = {
														Icon(
															if (set.finishedAt != null) {
																Icons.Rounded.AssignmentTurnedIn
															} else {
																Icons.Rounded.AssignmentLate
															},
															"",
															modifier = Modifier.absolutePadding(left = 5.dp)
														)
													},

													label = {
														if (set.finishedAt != null) {
															Text("Complete")
														} else {
															Text("Incomplete")
														}
													}
												)
											}

											Spacer(Modifier.weight(1f, true))

											Row(verticalAlignment = Alignment.CenterVertically) {
												Text("Select:", modifier = Modifier.absolutePadding(bottom = 5.dp))

												Checkbox(
													dropTarget.currentSet == index,
													{ dropTarget.currentSet = index },
													enabled = dropTarget.currentSet != index,
												)
											}
										}

										if (set.description != null) {
											Text(
												"Description: ${set.description}",
												modifier = Modifier.absolutePadding(left = 10.dp)
											)
										} else {
											Text(
												"No description set",
												modifier = Modifier.absolutePadding(left = 10.dp)
											)
										}

										Row(
											verticalAlignment = Alignment.CenterVertically,
											horizontalArrangement = Arrangement.spacedBy(10.dp)
										) {
											if (set.date != null) {
												StringTooltip("Photography date") {
													SuggestionChip(
														{},

														colors = colors.primaryChipColors(),

														icon = {
															Icon(
																Icons.Rounded.CameraRoll,
																"",
																modifier = Modifier.absolutePadding(left = 5.dp)
															)
														},

														label = { Text(set.date!!.format()) }
													)
												}
											}

											StringTooltip("Set creation date") {
												SuggestionChip(
													{},

													colors = colors.secondaryChipColors(),

													icon = {
														Icon(
															Icons.Rounded.AutoAwesome,
															"",
															modifier = Modifier.absolutePadding(left = 5.dp)
														)
													},

													label = { Text(set.createdAt.format()) }
												)
											}

											if (set.finishedAt != null) {
												StringTooltip("Set completion date") {
													SuggestionChip(
														{},

														colors = colors.successChipColors(),

														icon = {
															Icon(
																Icons.Rounded.EventAvailable,
																"",
																modifier = Modifier.absolutePadding(left = 5.dp)
															)
														},

														label = { Text(set.createdAt.format()) }
													)
												}
											}

											Spacer(Modifier.weight(1f, true))

											TertiaryButton({ deleteSetDialog.open(set) }) {
												Icon(
													Icons.Rounded.Delete,
													"",
													tint = colors.Text.copy(alpha = 0.5f)
												)

												Text("Delete")
											}

											SecondaryButton({ editSetDialog.open(set) }) {
												Icon(
													Icons.Rounded.Edit,
													"Edit"
												)

												Text("Edit")
											}

											PrimaryButton({ setWindow.open(set) }) {
												Icon(
													Icons.Rounded.FolderOpen,
													"Open"
												)

												Text("Open")
											}
										}
									}

									if (index != GlobalState.sets.lastIndex) {
										Spacer(Modifier.height(10.dp))
									}
								}
							}

							VerticalScrollbar(
								scrollAdapter,
								Modifier.align(Alignment.CenterEnd)
									.height(state.size.height)
							)
						}

						Spacer(Modifier.height(10.dp))

						HorizontalDivider(
							color = colors.Material.primary,
							thickness = 1.dp,
							modifier = Modifier.fillMaxWidth()
						)

						Row(
							modifier = Modifier
								.background(colors.SectionBackground)
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
	}
}
