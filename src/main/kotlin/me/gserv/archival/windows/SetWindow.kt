/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

@file:OptIn(ExperimentalFoundationApi::class)

package me.gserv.archival.windows

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Help
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.RenderVectorGroup
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import coil3.ImageLoader
import coil3.PlatformContext
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.animation.circular.CircularRevealPlugin
import com.skydoves.landscapist.coil3.CoilImage
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.placeholder.placeholder.PlaceholderPlugin
import io.github.kdroidfilter.platformtools.darkmodedetector.windows.setWindowsAdaptiveTitleBar
import io.github.oshai.kotlinlogging.KotlinLogging
import me.gserv.archival.Colors
import me.gserv.archival.data.Database
import me.gserv.archival.data.Filesystem
import me.gserv.archival.data.GlobalState
import me.gserv.archival.data.entities.Set
import me.gserv.archival.dropTarget
import me.gserv.archival.types.VisibilityTogglingWindow
import me.gserv.archival.utils.DirectoryWatcher
import me.gserv.archival.utils.PsdDecoder
import me.gserv.archival.utils.StringTooltip
import me.gserv.archival.utils.components.PrimaryButton
import me.gserv.archival.utils.components.SecondaryButton
import me.gserv.archival.utils.components.SuccessButton
import java.awt.Desktop
import java.awt.Dimension

class SetWindow(val parent: BinderWindow) : VisibilityTogglingWindow() {
	val logger = KotlinLogging.logger { }
	var watcher: DirectoryWatcher? by mutableStateOf(null)

	var isOpen by mutableStateOf(false)
	var setFiles by mutableStateOf(SnapshotStateList<Set.FileContainer>())

	val state = WindowState(
		size = DpSize(1000.dp, 700.dp),
	)

	fun close() {
		isOpen = false
		GlobalState.set = null

		watcher?.stop()
		watcher = null

		parent.show()
	}

	fun open(currentSet: Set) {
		GlobalState.set = currentSet

		watcher = DirectoryWatcher(Filesystem.inputFolder ?: error("Data directory not configured."))
		watcher!!.start()

		isOpen = true
	}

	@Composable
	fun create() {
		val imageLoader = ImageLoader
			.Builder(PlatformContext.INSTANCE)
			.components {
				add(PsdDecoder.Factory)
			}
			.build()

		if (isOpen) {
			parent.hide()

			LaunchedEffect(GlobalState.set) {
				if (GlobalState.set != null) {
					dropTarget.state {
						smallText = "Set"
						bigText = GlobalState.set!!.id.value.toString()
					}

					Database.transaction {
						setFiles = GlobalState.set!!.getFiles().toMutableStateList()
					}
				}
			}

			Window(
				::close,
				state = state,
				title = "Set ${GlobalState.set?.id?.value} (Binder ${GlobalState.binder?.id?.value})"
			) {
				scope = this
				window.minimumSize = Dimension(1000, 700)
				window.setWindowsAdaptiveTitleBar()

				val compareWindow = CompareWindow(this@SetWindow)
				compareWindow.create()

				if (isVisible) {
					Colors.Theme { colors ->
						Box(Modifier.background(colors.WindowBackground).fillMaxSize()) {
							Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
								Box(
									Modifier
										.background(colors.SectionBackground)
										.fillMaxWidth()
								) {
									Column {
										Row(
											Modifier.padding(15.dp),
											horizontalArrangement = Arrangement.spacedBy(10.dp),
											verticalAlignment = Alignment.CenterVertically
										) {
											Text(
												"Set ${GlobalState.set?.id?.value} (${setFiles.size} scans)",
												fontSize = 1.5.em,

												modifier = Modifier
													.height(50.dp)
													.wrapContentHeight(Alignment.CenterVertically),
											)

											Spacer(Modifier.weight(1f, true))

											SecondaryButton({
												if (Filesystem.inputFolder != null) {
													Desktop.getDesktop()
														.browse(Filesystem.inputFolder!!.toUri())
												}
											}) {
												Icon(
													Icons.Rounded.FolderOpen,
													""
												)

												Text("Open Import Folder")
											}

											PrimaryButton(
												{
													// TODO: Button Action
												},
												enabled = watcher?.files?.isEmpty() == false
											) {
												Icon(
													Icons.Rounded.UploadFile,
													""
												)

												if (watcher?.files?.isEmpty() == true) {
													Text("Nothing to import")
												} else if (watcher?.files?.size == 1) {
													Text("Import 1 file")
												} else {
													Text("Import ${watcher?.files?.size} files")
												}
											}
										}

										HorizontalDivider(
											color = colors.Material.primary,
											thickness = 1.dp,
											modifier = Modifier.fillMaxWidth()
										)
									}
								}

								if (!setFiles.isEmpty()) {
									LazyColumn(
										Modifier.fillMaxWidth()
											.absolutePadding(left = 10.dp, right = 10.dp, bottom = 10.dp),
										verticalArrangement = Arrangement.spacedBy(10.dp)
									) {
										items(setFiles) { files ->
											Column(Modifier.fillMaxWidth()) {
												Row(
													Modifier.height(300.dp),
													horizontalArrangement = Arrangement.spacedBy(10.dp)
												) {
													Column(
														Modifier.width(150.dp).fillMaxHeight(),
														verticalArrangement = Arrangement.spacedBy(5.dp)
													) {
														Text(
															files.index.toString().padStart(3, '0'),
															modifier = Modifier
																.background(
																	colors.SectionBackground,
																	RoundedCornerShape(15.dp)
																)
																.padding(vertical = 10.dp)
																.fillMaxWidth()
																.wrapContentHeight(Alignment.CenterVertically),
															textAlign = TextAlign.Center,
															fontSize = 1.5.em,
														)

														if (files.original != null && files.edit != null) {
															PrimaryButton(
																{ compareWindow.open(files.original, files.edit) },
																Modifier.fillMaxWidth()
															) {
																Icon(
																	Icons.Rounded.Image,
																	""
																)

																Text("Compare")
															}
														}
													}

													val iconTintPainter =
														@Composable { image: ImageVector, color: Color ->
															rememberVectorPainter(
																defaultWidth = image.defaultWidth,
																defaultHeight = image.defaultHeight,
																viewportWidth = image.viewportWidth,
																viewportHeight = image.viewportHeight,
																name = image.name,
																tintColor = color,
																tintBlendMode = image.tintBlendMode,
																autoMirror = image.autoMirror
															) { _, _ ->
																RenderVectorGroup(group = image.root)
															}
														}

													val imageComponent = rememberImageComponent {
														add(
															CircularRevealPlugin(
																duration = 350
															)
														)

														add(
															PlaceholderPlugin.Loading(
																iconTintPainter(
																	Icons.Rounded.Cached,
																	colors.Material.primary
																)
															)
														)

														add(
															PlaceholderPlugin.Failure(
																iconTintPainter(
																	Icons.Rounded.Error,
																	colors.Material.error
																)
															)
														)
													}

													Row(
														Modifier.fillMaxWidth(),
														horizontalArrangement = Arrangement.spacedBy(10.dp)
													) {
														Box(Modifier.size(300.dp)) {
															if (files.original != null) {
																OutlinedButton(
																	{
																		Desktop.getDesktop()
																			.browse(files.original.toURI())
																	},
																	border = BorderStroke(0.dp, Color.Transparent),
																	modifier = Modifier.fillMaxSize(),
																	shape = MaterialTheme.shapes.small,
																	contentPadding = PaddingValues(0.dp),
																) {
																	CoilImage(
																		component = imageComponent,
																		imageModel = { files.original },
																		imageLoader = { imageLoader },
																		modifier = Modifier.fillMaxSize(),

																		imageOptions = ImageOptions(
																			contentScale = ContentScale.Crop,
																			alignment = Alignment.Center,
																			contentDescription = "Original image",
																			requestSize = IntSize(1000, 1000),
																		)
																	)
																}
															} else {
																Column(
																	Modifier.fillMaxSize()
																		.border(
																			1.dp,
																			colors.Material.secondaryContainer,
																			MaterialTheme.shapes.small
																		),
																	verticalArrangement = Arrangement.spacedBy(10.dp),
																	horizontalAlignment = Alignment.CenterHorizontally,
																) {
																	Spacer(Modifier.weight(1f))

																	SuccessButton({
																		// TODO: Button Action
																	}) {
																		Icon(
																			Icons.Rounded.Add,
																			""
																		)

																		Text("Add missing file")
																	}

																	if (files.edit != null) {
																		PrimaryButton({
																			// TODO: Button Action
																		}) {
																			Icon(
																				Icons.Rounded.ContentCopy,
																				""
																			)

																			Text("Copy from edit")
																		}
																	}

																	Spacer(Modifier.weight(1f))
																}
															}

															Row(
																horizontalArrangement = Arrangement.spacedBy(10.dp),
																modifier = Modifier.offset(5.dp, 5.dp)
																	.background(
																		colors.Material.secondaryContainer.copy(0.75f),
																		MaterialTheme.shapes.small
																	)
																	.padding(5.dp),
															) {
																StringTooltip("Original scan") {
																	Icon(
																		Icons.Rounded.Image,
																		"Original scan",
																		tint = colors.Material.primary,
																	)
																}

																if (files.original == null) {
																	StringTooltip("File missing") {
																		Icon(
																			Icons.Rounded.ImageNotSupported,
																			"File missing",
																			tint = colors.Material.error,
																		)
																	}
																}

																if (files.originalInDatabase == false) {
																	StringTooltip("Not in database") {
																		Icon(
																			Icons.AutoMirrored.Rounded.Help,
																			"Not in database",
																			tint = colors.Material.error,
																		)
																	}
																}
															}
														}

														Box(Modifier.size(300.dp)) {
															if (files.edit != null) {
																OutlinedButton(
																	{ Desktop.getDesktop().browse(files.edit.toURI()) },
																	border = BorderStroke(0.dp, Color.Transparent),
																	modifier = Modifier.fillMaxSize(),
																	shape = MaterialTheme.shapes.small,
																	contentPadding = PaddingValues(0.dp),
																) {
																	CoilImage(
																		component = imageComponent,
																		imageModel = { files.edit },
																		imageLoader = { imageLoader },
																		modifier = Modifier.fillMaxSize(),

																		imageOptions = ImageOptions(
																			contentScale = ContentScale.Crop,
																			alignment = Alignment.Center,
																			contentDescription = "Edited image",
																			requestSize = IntSize(1000, 1000),
																		)
																	)
																}
															} else {
																Column(
																	Modifier.fillMaxSize()
																		.border(
																			1.dp,
																			colors.Material.secondaryContainer,
																			MaterialTheme.shapes.small
																		),
																	verticalArrangement = Arrangement.spacedBy(10.dp),
																	horizontalAlignment = Alignment.CenterHorizontally,
																) {
																	Spacer(Modifier.weight(1f))

																	SuccessButton({
																		// TODO: Button Action
																	}) {
																		Icon(
																			Icons.Rounded.Add,
																			""
																		)

																		Text("Add missing file")
																	}

																	if (files.original != null) {
																		PrimaryButton({
																			// TODO: Button Action
																		}) {
																			Icon(
																				Icons.Rounded.ContentCopy,
																				""
																			)

																			Text("Copy from original")
																		}
																	}

																	Spacer(Modifier.weight(1f))
																}
															}

															Row(
																horizontalArrangement = Arrangement.spacedBy(10.dp),
																modifier = Modifier.offset(5.dp, 5.dp)
																	.background(
																		colors.Material.secondaryContainer.copy(0.75f),
																		MaterialTheme.shapes.small
																	)
																	.padding(5.dp),
															) {
																StringTooltip("Edited scan") {
																	Icon(
																		Icons.Rounded.Brush,
																		"Edited scan",
																		tint = colors.MaterialSuccess,
																	)
																}

																if (files.edit == null) {
																	StringTooltip("File missing") {
																		Icon(
																			Icons.Rounded.ImageNotSupported,
																			"File missing",
																			tint = colors.Material.error,
																		)
																	}
																}

																if (files.editInDatabase == false) {
																	StringTooltip("Not in database") {
																		Icon(
																			Icons.AutoMirrored.Rounded.Help,
																			"Not in database",
																			tint = colors.Material.error,
																		)
																	}
																}
															}
														}
													}

													if (files.index != setFiles.last().index) {
														HorizontalDivider(
															Modifier.fillMaxWidth().absolutePadding(top = 10.dp),
															color = colors.Material.primary
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
				}
			}
		}
	}
}
