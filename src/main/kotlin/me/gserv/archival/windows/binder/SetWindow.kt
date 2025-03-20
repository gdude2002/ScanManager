/*
 * Copyrighted (Gareth Coles, 2024). Licensed under the EUPL-1.2
 * with the specific provision (EUPL articles 14 & 15) that the
 * applicable law is the (Republic of) Irish law and the Jurisdiction
 * Dublin.
 * Any redistribution must include the specific provision above.
 */

package me.gserv.archival.windows.binder

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.HelpCenter
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
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.util.DebugLogger
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.animation.circular.CircularRevealPlugin
import com.skydoves.landscapist.coil3.CoilImage
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.placeholder.placeholder.PlaceholderPlugin
import io.github.kdroidfilter.platformtools.darkmodedetector.windows.setWindowsAdaptiveTitleBar
import io.github.oshai.kotlinlogging.KotlinLogging
import me.gserv.archival.Colors
import me.gserv.archival.data.Database
import me.gserv.archival.data.GlobalState
import me.gserv.archival.data.entities.Set
import me.gserv.archival.dropTarget
import me.gserv.archival.utils.PsdDecoder
import me.gserv.archival.utils.components.PrimaryButton
import me.gserv.archival.utils.components.SuccessButton
import me.gserv.archival.windows.BinderWindow
import java.awt.Desktop
import java.awt.Dimension

class SetWindow(val parent: BinderWindow) {
	val logger = KotlinLogging.logger { }

	var isOpen by mutableStateOf(false)
	var setFiles by mutableStateOf(SnapshotStateList<Set.FileContainer>())

	val state = WindowState(
		size = DpSize(1000.dp, 700.dp),
	)

	lateinit var scope: FrameWindowScope

	fun close() {
		isOpen = false
		GlobalState.set = null

		parent.show()
	}

	fun open(currentSet: Set) {
		GlobalState.set = currentSet
		isOpen = true
	}

	@Composable
	fun create() {
		val imageLoader = ImageLoader
			.Builder(PlatformContext.INSTANCE)
			.logger(DebugLogger())
			.components {
				add(PsdDecoder.Factory)
			}
			.build()

		logger.info { "Creating window..." }

		if (isOpen) {
			logger.info { "Window is open!" }

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

				200.dp

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

										PrimaryButton({}) {
											Row(
												horizontalArrangement = Arrangement.spacedBy(10.dp),
												verticalAlignment = Alignment.CenterVertically,
											) {
												Icon(
													Icons.Rounded.Add,
													""
												)

												Text("Add Scan")
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

													PrimaryButton(
														{},
														Modifier.fillMaxWidth()
													) {
														Row(
															horizontalArrangement = Arrangement.spacedBy(10.dp),
															verticalAlignment = Alignment.CenterVertically,
														) {
															Icon(
																Icons.Rounded.Image,
																""
															)

															Text("Compare")
														}
													}

													Spacer(Modifier.weight(1f))

													if (files.original == null) {
														Row(
															horizontalArrangement = Arrangement.spacedBy(10.dp),
															verticalAlignment = Alignment.CenterVertically,
														) {
															Icon(
																Icons.AutoMirrored.Rounded.HelpCenter,
																"File missing",
																tint = colors.Material.error
															)

															Text(
																"Original Scan",
																color = colors.Material.error
															)
														}
													}

													if (files.edit == null) {
														Row(
															horizontalArrangement = Arrangement.spacedBy(10.dp),
															verticalAlignment = Alignment.CenterVertically,
														) {
															Icon(
																Icons.AutoMirrored.Rounded.HelpCenter,
																"File missing",
																tint = colors.Material.error
															)

															Text(
																"Edited Scan",
																color = colors.Material.error
															)
														}
													}
												}

												val iconTintPainter = @Composable { image: ImageVector, color: Color ->
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
															iconTintPainter(Icons.Rounded.Error, colors.Material.error)
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
																{ Desktop.getDesktop().browse(files.original.toURI()) },
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
															Row(
																Modifier.fillMaxSize()
																	.border(
																		1.dp,
																		colors.Material.secondaryContainer,
																		MaterialTheme.shapes.small
																	),
																horizontalArrangement = Arrangement.spacedBy(10.dp),
																verticalAlignment = Alignment.CenterVertically,
															) {
																Spacer(Modifier.weight(1f))

																SuccessButton({
																	// TODO: Button Action
																}) {
																	Row(
																		horizontalArrangement = Arrangement.spacedBy(10.dp),
																		verticalAlignment = Alignment.CenterVertically,
																	) {
																		Icon(
																			Icons.Rounded.Add,
																			""
																		)

																		Text("Add Missing File")
																	}
																}

																Spacer(Modifier.weight(1f))
															}
														}

														Icon(
															Icons.Rounded.Image,
															"Original image",
															tint = colors.Material.onSecondaryContainer,

															modifier = Modifier.offset(5.dp, 5.dp)
																.background(
																	colors.Material.secondaryContainer.copy(0.5f),
																	MaterialTheme.shapes.small
																)
																.padding(5.dp),
														)
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
															Row(
																Modifier.fillMaxSize()
																	.border(
																		1.dp,
																		colors.Material.secondaryContainer,
																		MaterialTheme.shapes.small
																	),
																horizontalArrangement = Arrangement.spacedBy(10.dp),
																verticalAlignment = Alignment.CenterVertically,
															) {
																Spacer(Modifier.weight(1f))

																SuccessButton({
																	// TODO: Button Action
																}) {
																	Row(
																		horizontalArrangement = Arrangement.spacedBy(10.dp),
																		verticalAlignment = Alignment.CenterVertically,
																	) {
																		Icon(
																			Icons.Rounded.Add,
																			""
																		)

																		Text("Add Missing File")
																	}
																}

																Spacer(Modifier.weight(1f))
															}
														}

														Icon(
															Icons.Rounded.Brush,
															"Edited image",
															tint = colors.Material.onSecondaryContainer,

															modifier = Modifier.offset(5.dp, 5.dp)
																.background(
																	colors.Material.secondaryContainer.copy(0.5f),
																	MaterialTheme.shapes.small
																)
																.padding(5.dp),
														)
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
