/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package me.gserv.archival.windows.binder

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import me.gserv.archival.Colors
import me.gserv.archival.data.Database
import me.gserv.archival.data.GlobalState
import me.gserv.archival.data.entities.Set
import me.gserv.archival.utils.StringTooltip
import me.gserv.archival.utils.ZERO
import me.gserv.archival.utils.components.*
import me.gserv.archival.utils.format
import me.gserv.archival.utils.fromEpochMilliseconds
import me.gserv.archival.windows.BinderWindow
import java.util.*

class EditSetDialog(
	val parent: BinderWindow,
) {
	val logger = KotlinLogging.logger { }

	var isDatePickerOpen by mutableStateOf(false)

	var isOpen by mutableStateOf(false)

	var set: Set? by mutableStateOf(null)
	var setDescription by mutableStateOf("")

	var setCreatedAt: LocalDate by mutableStateOf(LocalDate.ZERO)
	var setDate: LocalDate? by mutableStateOf(null)
	var setFinishedAt: LocalDate? by mutableStateOf(null)

	val datePickerState = DatePickerState(
		initialSelectedDateMillis = null,
		initialDisplayedMonthMillis = null,
		yearRange = DatePickerDefaults.YearRange,
		initialDisplayMode = DisplayMode.Picker,
		selectableDates = DatePickerDefaults.AllDates,
		locale = Locale.getDefault()
	)

	var datePickerCallback: ((LocalDate?) -> Unit) = { }

	fun close() {
		isDatePickerOpen = false
		isOpen = false

		set = null
		setDescription = ""

		setCreatedAt = LocalDate.ZERO
		setDate = LocalDate.ZERO
		setFinishedAt = LocalDate.ZERO
	}

	fun open(set: Set) {
		isOpen = true

		this.set = set
		setDescription = set.description ?: ""

		setCreatedAt = set.createdAt
		setDate = set.date
		setFinishedAt = set.finishedAt
	}

	fun openDatePicker(currentDate: LocalDate?, callback: (LocalDate?) -> Unit) {
		datePickerCallback = callback

		datePickerState.selectedDateMillis = currentDate?.atStartOfDayIn(TimeZone.UTC)?.toEpochMilliseconds()

		isDatePickerOpen = true
	}

	@Composable
	fun DatePickerModal() {
		DatePickerDialog(
			onDismissRequest = { isDatePickerOpen = false },

			confirmButton = {
				PrimaryButton(
					enabled = datePickerState.selectedDateMillis != null,

					onClick = {
						datePickerCallback(
							datePickerState.selectedDateMillis?.let {
								LocalDate.fromEpochMilliseconds(it)
							}
						)

						isDatePickerOpen = false
					}
				) {
					Text("Select")
				}
			},

			dismissButton = {
				SecondaryButton(onClick = { isDatePickerOpen = false }) {
					Text("Cancel")
				}
			}
		) {
			DatePicker(state = datePickerState)
		}
	}

	@Composable
	@Preview
	fun create() {
		if (isOpen) {
			Dialog({}, DialogProperties(false, false, true)) {
				Colors.Theme { colors ->
					DialogContainer {
						Colors.Theme { colors ->
							if (isDatePickerOpen) {
								DatePickerModal()
							}

							Column(
								modifier = Modifier.padding(10.dp),
								verticalArrangement = Arrangement.spacedBy(20.dp)
							) {
								Row(verticalAlignment = Alignment.CenterVertically) {
									Icon(
										Icons.Rounded.VideoFile,
										"",
										modifier = Modifier
											.absolutePadding(right = 8.dp, top = 1.dp)
											.size(30.dp)
									)

									Text(
										"Edit Set",
										fontSize = TextUnit(1.5F, TextUnitType.Em)
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

								HorizontalDivider(Modifier.fillMaxWidth())

								Row(
									horizontalArrangement = Arrangement.spacedBy(10.dp),
									verticalAlignment = Alignment.CenterVertically,
								) {
									Column(modifier = Modifier.weight(1f)) {
										Text(
											"Photography Date",
											fontWeight = FontWeight.Bold,
										)

										if (setDate == null) {
											Text(
												"Not set",
												modifier = Modifier.absolutePadding(left = 10.dp)
											)
										} else {
											Text(
												setDate!!.format(),
												modifier = Modifier.absolutePadding(left = 10.dp)
											)
										}
									}

									StringTooltip("Clear Date") {
										TertiaryIconButton(
											{ setDate = null },
											modifier = Modifier.size(55.dp)
										) {
											Icon(
												Icons.Rounded.Delete,
												"Clear Date"
											)
										}
									}

									StringTooltip("Select Date") {
										PrimaryIconButton(
											{ openDatePicker(setDate) { setDate = it } },
											modifier = Modifier.size(55.dp)
										) {
											Icon(
												Icons.Rounded.EditCalendar,
												"Select Date"
											)
										}
									}
								}

								Row(
									horizontalArrangement = Arrangement.spacedBy(10.dp),
									verticalAlignment = Alignment.CenterVertically,
								) {
									Column(modifier = Modifier.weight(1f)) {
										Text(
											"Set Completion Date",
											fontWeight = FontWeight.Bold,
										)

										if (setFinishedAt == null) {
											Text(
												"Not set",
												modifier = Modifier.absolutePadding(left = 10.dp)
											)
										} else {
											Text(
												setFinishedAt!!.format(),
												modifier = Modifier.absolutePadding(left = 10.dp)
											)
										}
									}

									StringTooltip("Clear Date") {
										TertiaryIconButton(
											{ setFinishedAt = null },
											modifier = Modifier.size(55.dp)
										) {
											Icon(
												Icons.Rounded.Delete,
												"Clear Date"
											)
										}
									}

									StringTooltip("Select Date") {
										PrimaryIconButton(
											{ openDatePicker(setFinishedAt) { setFinishedAt = it } },
											modifier = Modifier.size(55.dp)
										) {
											Icon(
												Icons.Rounded.EditCalendar,
												"Select Date"
											)
										}
									}
								}

								Row(
									horizontalArrangement = Arrangement.spacedBy(10.dp),
									verticalAlignment = Alignment.CenterVertically,
								) {
									Column(modifier = Modifier.weight(1f)) {
										Text(
											"Set Creation Date",
											fontWeight = FontWeight.Bold,
										)

										Text(
											setCreatedAt.format(),
											modifier = Modifier.absolutePadding(left = 10.dp)
										)
									}

									StringTooltip("Select Date") {
										PrimaryIconButton(
											{ openDatePicker(setCreatedAt) { setCreatedAt = it!! } },
											modifier = Modifier.size(55.dp)
										) {
											Icon(
												Icons.Rounded.EditCalendar,
												"Select Date"
											)
										}
									}
								}

								HorizontalDivider(Modifier.fillMaxWidth())

								Row {
									SecondaryButton(
										{
											logger.debug { "Closing dialog without editing set." }
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
											logger.debug { "Editing set ${set!!.id.value} in binder ${GlobalState.binder?.id?.value}" }

											Database.transaction {
												set!!.description = setDescription
												set!!.createdAt = setCreatedAt
												set!!.date = setDate
												set!!.finishedAt = setFinishedAt
											}

											logger.debug { "Set edited, updating global and window states..." }

											GlobalState.sets.removeIf { it.id == set!!.id }
											GlobalState.sets.add(set!!)
											GlobalState.sets.sortByDescending { it.id.value.toLong() }

											parent.allSets.removeIf { it.id == set!!.id }
											parent.allSets.add(set!!)
											parent.allSets.sortByDescending { it.id.value.toLong() }

											logger.debug { "Done, closing dialog." }

											close()
										},
									) {
										Icon(
											Icons.Rounded.Save,
											"",
											modifier = Modifier.absolutePadding(right = 4.dp)
										)

										Text("Save")
									}
								}
							}
						}
					}
				}
			}
		}
	}
}
