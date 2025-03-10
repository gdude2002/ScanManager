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
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.rounded.*
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
import kotlinx.coroutines.delay
import me.gserv.archival.Colors
import me.gserv.archival.data.Database
import me.gserv.archival.data.GlobalState
import me.gserv.archival.data.entities.Binder
import me.gserv.archival.data.entities.Set
import me.gserv.archival.data.tables.SetTable
import me.gserv.archival.dropTarget
import me.gserv.archival.utils.StringTooltip
import me.gserv.archival.utils.components.DangerButton
import me.gserv.archival.utils.components.PrimaryButton
import me.gserv.archival.utils.components.SecondaryButton
import me.gserv.archival.utils.format
import me.gserv.archival.windows.binder.CreateSetDialog
import me.gserv.archival.windows.binder.DeleteSetDialog
import me.gserv.archival.windows.binder.FilterState
import me.gserv.archival.windows.binder.SetWindow
import org.jetbrains.exposed.sql.SortOrder
import java.awt.Dimension
import kotlin.time.Duration.Companion.seconds

class BinderWindow(val parent: MainWindow) {
	val logger = KotlinLogging.logger { }

	var isOpen by mutableStateOf(false)
	var isVisible by mutableStateOf(true)

	var isDropdownOpen by mutableStateOf(false)

	var filterState by mutableStateOf<FilterState>(FilterState.All)
	var filterText by mutableStateOf("")

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

		isDropdownOpen = false
		filterState = FilterState.All
		filterText = ""

	}

	fun open(binder: Binder) {
		parent.hide()

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
			}

			Window(::close, state = state, title = "Binder ${GlobalState.binder?.id}") {
				scope = this

				window.minimumSize = Dimension(1000, 700)
				window.setWindowsAdaptiveTitleBar()

				val createSetDialog = CreateSetDialog(this@BinderWindow, GlobalState.binder!!)
				createSetDialog.create()

				val deleteSetDialog = DeleteSetDialog(this@BinderWindow)
				deleteSetDialog.create()

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
								.height(55.dp)
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
										dropTarget.currentSet = 0
									}) {
										Text("Incomplete Sets")
									}

									DropdownMenuItem({
										logger.info { "Updating completion filter: Complete sets only" }

										filterState = FilterState.Complete
										isDropdownOpen = false
										dropTarget.currentSet = 0
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
									dropTarget.currentSet = 0
								},
								label = {
									Text("Description", modifier = Modifier.absolutePadding(bottom = 10.dp))
								},
								modifier = Modifier.fillMaxHeight()
							)
						}

						Divider(
							color = colors.Primary,
							modifier = Modifier
								.height(1.dp)
								.fillMaxWidth()
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
											modifier = Modifier.height(IntrinsicSize.Min)
										) {
											Text(
												"Set ${set.id.value}",
												fontSize = 2.em,
												modifier = Modifier.absolutePadding(left = 10.dp, bottom = 5.dp)
											)

											StringTooltip("Number of scans") {
												Chip(
													{},
													colors = colors.defaultChipColors(),
													leadingIcon = {
														Icon(
															Icons.Rounded.Image,
															"",
															modifier = Modifier.absolutePadding(left = 5.dp)
														)
													}
												) {
													Text(set.totalScans.toString())
												}
											}

											StringTooltip("Set completion state") {
												Chip(
													{},
													colors = if (set.finishedAt != null) {
														colors.successChipColors()
													} else {
														colors.dangerChipColors()
													},
													leadingIcon = {
														Icon(
															if (set.finishedAt != null) {
																Icons.Rounded.AssignmentTurnedIn
															} else {
																Icons.Rounded.AssignmentLate
															},
															"",
															modifier = Modifier.absolutePadding(left = 5.dp)
														)
													}
												) {
													if (set.finishedAt != null) {
														Text("Complete")
													} else {
														Text("Incomplete")
													}
												}
											}

											Spacer(Modifier.weight(1f, true))

											Row(verticalAlignment = Alignment.CenterVertically) {
												Text("Select:", modifier = Modifier.absolutePadding(bottom = 5.dp))

												Checkbox(
													dropTarget.currentSet == index,
													{ dropTarget.currentSet = index },
													enabled = dropTarget.currentSet != index,
													colors = CheckboxDefaults.colors(
														checkedColor = colors.PrimaryVariant,
														disabledColor = colors.PrimaryVariant,
														checkmarkColor = colors.WindowBackground.copy(alpha = 0.6f)
													)
												)
											}
										}

										Row(
											horizontalArrangement = Arrangement.spacedBy(10.dp)
										) {
											var setText by remember(GlobalState.binder?.id, set.id, "description") {
												mutableStateOf(set.description ?: "")
											}

											TextField(
												setText,
												{ setText = it },
												label = { Text("Description") },
												modifier = Modifier.weight(1f)
											)

											StringTooltip("Save") {
												var saveIcon by remember(GlobalState.binder?.id, set.id, "save-icon") {
													mutableStateOf(Icons.Rounded.Save)
												}

												LaunchedEffect(saveIcon) {
													if (saveIcon != Icons.Rounded.Save) {
														delay(5.seconds)

														saveIcon = Icons.Rounded.Save
													}
												}

												PrimaryButton(
													{
														Database.transaction {
															set.description = if (setText.isEmpty()) {
																null
															} else {
																setText
															}
														}

														saveIcon = Icons.Rounded.Check
													},
													enabled = (set.description ?: "") != setText,
													modifier = Modifier.height(56.dp)
												) {
													Icon(
														saveIcon,
														"Save"
													)
												}
											}
										}

										Row(
											verticalAlignment = Alignment.CenterVertically,
											horizontalArrangement = Arrangement.spacedBy(10.dp)
										) {
											if (set.date != null) {
												StringTooltip("Photography date") {
													Chip(
														{},
														colors = colors.primaryChipColors(),
														leadingIcon = {
															Icon(
																Icons.Rounded.CameraRoll,
																"",
																modifier = Modifier.absolutePadding(left = 5.dp)
															)
														}
													) {
														Text(set.date!!.format())
													}
												}
											}

											StringTooltip("Set creation date") {
												Chip(
													{},
													colors = colors.secondaryChipColors(),
													leadingIcon = {
														Icon(
															Icons.Rounded.AutoAwesome,
															"",
															modifier = Modifier.absolutePadding(left = 5.dp)
														)
													}
												) {
													Text(set.createdAt.format())
												}
											}

											if (set.finishedAt != null) {
												StringTooltip("Set completion date") {
													Chip(
														{},
														colors = colors.successChipColors(),
														leadingIcon = {
															Icon(
																Icons.Rounded.EventAvailable,
																"",
																modifier = Modifier.absolutePadding(left = 5.dp)
															)
														}
													) {
														Text(set.createdAt.format())
													}
												}
											}

											Spacer(Modifier.weight(1f, true))

											DangerButton({ deleteSetDialog.open(set) }) {
												Row(
													verticalAlignment = Alignment.CenterVertically,
													horizontalArrangement = Arrangement.spacedBy(10.dp),
												) {
													Icon(
														Icons.Rounded.Delete,
														"",
														tint = colors.Text.copy(alpha = 0.5f)
													)

													Text("Delete")
												}
											}

											SecondaryButton({ setWindow.open(set) }) {
												Row(
													verticalAlignment = Alignment.CenterVertically,
													horizontalArrangement = Arrangement.spacedBy(10.dp),
												) {
													Icon(
														Icons.Rounded.FolderOpen,
														"Open"
													)

													Text("Open")
												}
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

						Divider(
							color = colors.Primary,
							modifier = Modifier
								.height(1.dp)
								.fillMaxWidth()
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
