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
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Help
import androidx.compose.material.icons.filled.FolderOff
import androidx.compose.material.icons.filled.WavingHand
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.zIndex
import io.github.kdroidfilter.platformtools.darkmodedetector.windows.setWindowsAdaptiveTitleBar
import io.github.oshai.kotlinlogging.KotlinLogging
import me.gserv.archival.Colors
import me.gserv.archival.config.AppConfig
import me.gserv.archival.data.Database
import me.gserv.archival.data.GlobalState
import me.gserv.archival.dropTarget
import me.gserv.archival.utils.StringTooltip
import me.gserv.archival.utils.components.PrimaryButton
import me.gserv.archival.utils.components.PrimaryIconButton
import me.gserv.archival.utils.components.PrimaryOutlinedButton
import me.gserv.archival.utils.components.SecondaryOutlinedButton
import me.gserv.archival.windows.main.CreateBinderDialog
import me.gserv.archival.windows.main.DataDirectoryDialog
import me.gserv.archival.windows.main.DeleteBinderDialog
import java.awt.Desktop
import java.net.URI

class MainWindow(val applicationScope: ApplicationScope) {
	val logger = KotlinLogging.logger { }

	var isVisible by mutableStateOf(true)
	var showArchived by mutableStateOf(false)

	val state = WindowState(
		size = DpSize(1000.dp, Dp.Unspecified)
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
	}

	@Composable
	@Preview
	fun open() {
		if (::scope.isInitialized) {
			return
		}

		val binderWindow = BinderWindow(this@MainWindow)
		binderWindow.create()

		val compareWindow = CompareWindow(this@MainWindow)
		compareWindow.create()

		Window(
			onCloseRequest = {
				Database.close()
				applicationScope.exitApplication()
			},

			resizable = false,
			state = state,
			title = "Scan Manager"
		) {
			scope = this

			window.setWindowsAdaptiveTitleBar()

			val dataDirectoryDialog = DataDirectoryDialog(this)
			dataDirectoryDialog.create()

			val createBinderDialog = CreateBinderDialog(this)
			createBinderDialog.create()

			val deleteBinderDialog = DeleteBinderDialog(this)
			deleteBinderDialog.create()

			if (isVisible) {
				dropTarget.state {
					if (AppConfig.dataFolder == null) {
						icon = Icons.Default.FolderOff
						smallText = "No data\nfolder"
					} else {
						icon = Icons.Default.WavingHand
						smallText = "Welcome!"
					}
				}
			}

			Colors.Theme { colors ->
				Box(Modifier.background(colors.WindowBackground)) {
					Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
						Row(
							horizontalArrangement = Arrangement.spacedBy(10.dp),
							modifier = Modifier
								.background(colors.SectionBackground, RoundedCornerShape(15.dp))
								.padding(vertical = 10.dp, horizontal = 15.dp)
						) {
							Column {
								Text("Welcome!", fontSize = TextUnit(1.5F, TextUnitType.Em))
								Text("Please select a binder or one of the tools to get started.")
							}

							Spacer(Modifier.weight(1f))

							StringTooltip("Help") {
								PrimaryIconButton(
									modifier = Modifier.size(60.dp),
									onClick = {
										Desktop.getDesktop()
											.browse(URI("https://github.com/gdude2002/ScanManager/wiki"))
									},
								) {
									Icon(
										Icons.AutoMirrored.Rounded.Help,
										"Help",
									)
								}
							}
						}

						Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
							val verticalScrollState = rememberScrollState(0)

							Box(
								modifier = Modifier
									.background(colors.SectionBackground, RoundedCornerShape(15.dp))
									.fillMaxWidth(0.5F)
									.padding(vertical = 10.dp, horizontal = 15.dp)
									.requiredHeight(500.dp)
							) {
								Text(
									"Binders",
									fontSize = TextUnit(1.5F, TextUnitType.Em),
									modifier = Modifier
										.background(colors.SectionBackground)
										.padding(vertical = 5.dp)
										.fillMaxWidth()
										.zIndex(10f)
								)

								Column(
									modifier = Modifier
										.verticalScroll(verticalScrollState)
										.absolutePadding(right = 17.dp, top = 42.dp)
								) {
									if (showArchived) {
										if (GlobalState.binders.isEmpty()) {
											Text(
												"No binders found. Click \"Add Binder\" below to create one."
											)
										}
									} else {
										if (GlobalState.binders.none { !it.archived }) {
											Text(
												"No binders found. Click \"Add Binder\" below to create one."
											)
										}
									}

									for (binder in GlobalState.binders) {
										if (!binder.archived || showArchived) {
											Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
												PrimaryOutlinedButton(
													modifier = Modifier.weight(1f),
													onClick = {
														binderWindow.open(binder)
													},
													enabled = !binder.archived
												) {
													Row(verticalAlignment = Alignment.CenterVertically) {
														if (binder.archived) {
															Icon(
																Icons.Rounded.Lock,
																"Archived binder",
																tint = Color.Gray
															)
														} else {
															Icon(
																Icons.Rounded.FolderOpen,
																"Binder"
															)
														}

														if (binder.archived) {
															Text(
																"Binder ${binder.id.value}",
																modifier = Modifier.fillMaxWidth()
																	.padding(horizontal = 8.dp),
																textAlign = TextAlign.Left,
																softWrap = false,
																overflow = TextOverflow.Ellipsis,
																color = Color.Gray
															)
														} else {
															Text(
																"Binder ${binder.id.value}",
																modifier = Modifier.fillMaxWidth()
																	.padding(horizontal = 8.dp),
																textAlign = TextAlign.Left,
																softWrap = false,
																overflow = TextOverflow.Ellipsis,
															)
														}
													}
												}

												if (binder.archived) {
													StringTooltip("Delete binder") {
														TextButton(
															onClick = {
																deleteBinderDialog.open(binder)
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
																	"Delete binder",
																	tint = colors.Material.tertiary
																)
															}
														}
													}
												} else if (showArchived && GlobalState.binders.any { it.archived }) {
													Spacer(Modifier.width(40.dp))
												}

												val tooltipText = if (binder.archived) {
													"Un-archive binder"
												} else {
													"Archive binder"
												}

												StringTooltip(tooltipText) {
													TextButton(
														onClick = {
															logger.info {
																if (binder.archived) {
																	"Un-archiving binder ${binder.id.value}"
																} else {
																	"Archiving binder ${binder.id.value}"
																}
															}

															Database.transaction {
																binder.archived = !binder.archived
															}

															logger.info { "Reloading global state..." }

															GlobalState.loadBinders()

															logger.info { "Done" }
														},
														modifier = Modifier.padding(horizontal = 0.dp).width(40.dp),
														contentPadding = PaddingValues(0.dp)
													) {
														Row(
															verticalAlignment = Alignment.CenterVertically,
															modifier = Modifier.padding(0.dp)
														) {
															if (binder.archived) {
																Icon(
																	Icons.Rounded.LockOpen,
																	"Un-archive binder",
																	tint = colors.Material.primary
																)
															} else {
																Icon(
																	Icons.Rounded.Lock,
																	"Archive binder",
																	tint = colors.Material.primary
																)
															}
														}
													}
												}
											}
										}
									}
								}

								VerticalScrollbar(
									modifier = Modifier.align(Alignment.CenterEnd)
										.height(state.size.height)
										.absolutePadding(top = 45.dp),
									adapter = rememberScrollbarAdapter(verticalScrollState)
								)
							}

							Column(
								modifier = Modifier
									.background(colors.SectionBackground, RoundedCornerShape(15.dp))
									.fillMaxWidth()
									.padding(vertical = 10.dp, horizontal = 15.dp)
									.requiredHeight(500.dp)
							) {
								Text(
									"Tools",
									fontSize = TextUnit(1.5F, TextUnitType.Em),
									modifier = Modifier.padding(vertical = 5.dp)
								)

								SecondaryOutlinedButton(
									modifier = Modifier.fillMaxWidth().align(Alignment.Start),
									onClick = {},
									enabled = false
								) {
									Row(verticalAlignment = Alignment.CenterVertically) {
										Icon(
											Icons.Rounded.SettingsBackupRestore,
											""
										)

										Text(
											"Back Up & Restore",
											modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
											textAlign = TextAlign.Left
										)
									}
								}

								SecondaryOutlinedButton(
									modifier = Modifier.fillMaxWidth().align(Alignment.Start),
									onClick = { compareWindow.open() },
								) {
									Row(verticalAlignment = Alignment.CenterVertically) {
										Icon(
											Icons.Rounded.Image,
											""
										)

										Text(
											"Compare Images",
											modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
											textAlign = TextAlign.Left
										)
									}
								}

								SecondaryOutlinedButton(
									modifier = Modifier.fillMaxWidth().align(Alignment.Start),
									onClick = {},
									enabled = false
								) {
									Row(verticalAlignment = Alignment.CenterVertically) {
										Icon(
											Icons.Rounded.Healing,
											""
										)

										Text(
											"Fix Scans",
											modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
											textAlign = TextAlign.Left
										)
									}
								}

								SecondaryOutlinedButton(
									modifier = Modifier.fillMaxWidth().align(Alignment.Start),
									onClick = {},
									enabled = false
								) {
									Row(verticalAlignment = Alignment.CenterVertically) {
										Icon(
											Icons.Rounded.Download,
											""
										)

										Text(
											"Import Scans",
											modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
											textAlign = TextAlign.Left
										)
									}
								}

								SecondaryOutlinedButton(
									modifier = Modifier.fillMaxWidth().align(Alignment.Start),
									onClick = {},
									enabled = false
								) {
									Row(verticalAlignment = Alignment.CenterVertically) {
										Icon(
											Icons.Rounded.Search,
											""
										)

										Text(
											"Search Scans",
											modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
											textAlign = TextAlign.Left
										)
									}
								}

								Spacer(Modifier.weight(1f))

								if (AppConfig.dataFolder != null) {
									Row(verticalAlignment = Alignment.CenterVertically) {
										Icon(
											Icons.Rounded.Folder,
											"Data folder",
											modifier = Modifier.absolutePadding(right = 4.dp),
											tint = colors.Material.primary,
										)

										Text(
											dataDirectoryDialog.dataDirectory,

											maxLines = 1,
											modifier = Modifier.weight(1f),
											overflow = TextOverflow.Ellipsis,
											softWrap = false,
										)
									}
								}
							}
						}

						Row(
							horizontalArrangement = Arrangement.spacedBy(10.dp),
							modifier = Modifier.requiredHeight(60.dp)
						) {
							Column(
								modifier = Modifier
									.background(colors.SectionBackground, RoundedCornerShape(15.dp))
									.fillMaxWidth(0.5F)
									.padding(vertical = 10.dp, horizontal = 15.dp)
									.align(Alignment.CenterVertically)
							) {
								Row {
									PrimaryOutlinedButton(onClick = { showArchived = !showArchived }) {
										if (showArchived) {
											Text("Hide Archived")
										} else {
											Text("Show Archived")
										}
									}

									Spacer(Modifier.weight(1f))

									PrimaryButton(onClick = { createBinderDialog.open() }) {
										Text("Add Binder")
									}
								}
							}

							Column(
								modifier = Modifier
									.background(colors.SectionBackground, RoundedCornerShape(15.dp))
									.fillMaxWidth()
									.padding(vertical = 10.dp, horizontal = 15.dp)
							) {
								Row {
									PrimaryButton(
										onClick = {},
										enabled = false
									) {
										Text("Check Integrity")
									}

									Spacer(Modifier.weight(1f))

									PrimaryButton(onClick = {
										dataDirectoryDialog.open()
									}) {
										Text("Change Folder")
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
